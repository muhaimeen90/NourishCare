package com.nourishcare.visionservice.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.nourishcare.visionservice.model.FoodDetection;
import com.nourishcare.visionservice.repository.FoodDetectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisionService {

    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    @Autowired
    private FoodDetectionRepository foodDetectionRepository;

    @Autowired
    private ImageAnnotatorSettings imageAnnotatorSettings;

    @Autowired
    private UsdaFoodDataService usdaFoodDataService;

    @Autowired
    private PortionEstimationService portionEstimationService;

    @Autowired
    private SemanticMatchingService semanticMatchingService;

    @Autowired
    private YoloFoodDetectionService yoloFoodDetectionService;

    @Value("${vision.api.mock:false}")
    private boolean useMockService;

    /**
     * Detect food items from uploaded image using Google Vision API and USDA validation
     */
    public FoodDetection detectFoodItems(MultipartFile file) throws IOException {
        logger.info("Starting food detection for image: {}", file.getOriginalFilename());

        // Create initial food detection record
        FoodDetection detection = new FoodDetection(
            null, // imageUrl will be set later if needed
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );

        List<FoodDetection.DetectedFood> detectedFoods;
        
        if (useMockService) {
            detectedFoods = mockFoodDetection(file.getOriginalFilename());
        } else {
            detectedFoods = detectFoodItemsWithVisionAPI(file);
        }

        detection.setDetectedFoods(detectedFoods);
        
        // Save to database with error handling
        try {
            logger.info("💾 Saving detection results to MongoDB...");
            FoodDetection savedDetection = foodDetectionRepository.save(detection);
            logger.info("✅ Successfully saved detection with ID: {}", savedDetection.getId());
            logger.info("🎉 ANALYSIS COMPLETE - {} food items detected and saved", detectedFoods.size());
            return savedDetection;
        } catch (Exception e) {
            logger.error("❌ Failed to save detection results to MongoDB: {}", e.getMessage());
            logger.error("📊 Detection data: {} food items were successfully analyzed but not saved", detectedFoods.size());
            
            // Still return the detection object even if save failed
            // This allows the frontend to show results even if DB save fails
            logger.warn("⚠️ Returning unsaved detection results to frontend");
            return detection;
        }
    }

    /**
     * Detect food items using Google Cloud Vision API with USDA validation
     */
    private List<FoodDetection.DetectedFood> detectFoodItemsWithVisionAPI(MultipartFile file) throws IOException {
        logger.info("===== STARTING VISION API DETECTION PIPELINE =====");
        logger.info("Using Google Cloud Vision API for food detection");
        List<FoodDetection.DetectedFood> detectedFoods = new ArrayList<>();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();
            
            // Get image dimensions for bounding box conversion
            int[] imageDimensions = getImageDimensions(file.getBytes());
            int imageWidth = imageDimensions[0];
            int imageHeight = imageDimensions[1];
            logger.info("Image dimensions: {}x{} pixels", imageWidth, imageHeight);
            
            // === STAGE 1: GOOGLE VISION API - OBJECT DETECTION ===
            logger.info("=== STAGE 1: VISION API OBJECT DETECTION ===");
            
            // Use Vision API for comprehensive object detection
            Feature objectFeature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
            
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(objectFeature)
                .setImage(img)
                .build();

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(
                Collections.singletonList(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    logger.error("Error in Vision API response: {}", res.getError().getMessage());
                    continue;
                }
                
                List<LocalizedObjectAnnotation> allObjects = res.getLocalizedObjectAnnotationsList();
                logger.info("Vision API objects detected: {}", allObjects.size());
                for (LocalizedObjectAnnotation obj : allObjects) {
                    logger.info("  Object: {} ({:.2f})", obj.getName(), String.format("%.2f", obj.getScore()));
                }

                
                // === STAGE 2: REFERENCE OBJECT DETECTION ===
                logger.info("=== STAGE 2: REFERENCE OBJECT DETECTION ===");
                Map<String, FoodDetection.BoundingBox> referenceObjects = 
                    detectAndLogReferenceObjects(allObjects, imageWidth, imageHeight);
                
                // === STAGE 3: PROCESS VISION API FOOD DETECTIONS WITH USDA FILTERING ===
                logger.info("=== STAGE 3: PROCESSING VISION API FOOD DETECTIONS ===");
                
                for (LocalizedObjectAnnotation visionObject : allObjects) {
                    String objectName = visionObject.getName();
                    float confidence = visionObject.getScore();
                    
                    logger.info("Processing Vision API detection: {} ({:.2f})", objectName, String.format("%.2f", confidence));
                    
                    // Apply confidence threshold (55%)
                    if (confidence < 0.55f) {
                        logger.info("Skipping {} - confidence {:.2f} below threshold (55%)", objectName, String.format("%.2f", confidence));
                        continue;
                    }
                    
                    // Stage 3a: Clean object name
                    String cleanedName = cleanAndLogFoodName(objectName);
                    
                    // Stage 3b: USDA search with semantic matching to filter food items
                    Optional<UsdaFoodDataService.UsdaFoodItem> usdaFood = searchAndLogUSDA(cleanedName);
                    
                    if (usdaFood.isPresent()) {
                        // Create food detection with Vision API bounding box
                        FoodDetection.DetectedFood food = createDetectedFoodFromVisionAPI(
                            cleanedName, confidence, usdaFood.get(), visionObject, 
                            referenceObjects, imageWidth, imageHeight);
                        detectedFoods.add(food);
                        logger.info("Added food item: {} with USDA match", cleanedName);
                    } else {
                        logger.info("Discarding Vision API detection (not a food item): {}", cleanedName);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error calling Vision API: {}", e.getMessage());
            throw new IOException("Vision API error: " + e.getMessage(), e);
        }

        // Remove duplicates and sort by confidence
        detectedFoods = removeDuplicatesAndSort(detectedFoods);
        
        logger.info("===== PIPELINE COMPLETE =====");
        logger.info("Successfully detected {} food items", detectedFoods.size());
        
        // Log final summary
        for (int i = 0; i < detectedFoods.size(); i++) {
            FoodDetection.DetectedFood food = detectedFoods.get(i);
            logger.info("  {}. {} - {}g - {} kcal", 
                       i + 1, food.getName(), 
                       String.format("%.1f", food.getEstimatedGrams()), 
                       String.format("%.1f", food.getEstimatedCalories()));
        }
        
        return detectedFoods;
    }

    /**
     * Check if the detected item is a specific food item rather than generic term
     */
    private boolean isSpecificFoodItem(String item) {
        String lowerItem = item.toLowerCase();
        
        // Specific food items we want to detect
        String[] specificFoods = {
            "avocado", "tomato", "cherry tomato", "black bean", "kidney bean", "chickpea",
            "corn", "quinoa", "rice", "tofu", "tempeh", "cheese", "feta", "mozzarella",
            "spinach", "lettuce", "arugula", "cucumber", "carrot", "bell pepper",
            "broccoli", "cauliflower", "potato", "sweet potato", "egg", "chicken",
            "salmon", "tuna", "shrimp", "apple", "banana", "orange", "strawberry",
            "blueberry", "grape", "lime", "lemon", "olive", "walnut", "almond"
        };
        
        return Arrays.stream(specificFoods)
            .anyMatch(food -> lowerItem.contains(food));
    }

    /**
     * Check if the term is too generic to be useful
     */
    private boolean isGenericTerm(String term) {
        String lowerTerm = term.toLowerCase();
        String[] genericTerms = {
            "food", "ingredient", "vegetable", "fruit", "bean", "grain", "protein",
            "salad", "dish", "meal", "recipe", "bowl", "plate", "cuisine", "snack"
        };
        
        return Arrays.stream(genericTerms)
            .anyMatch(generic -> lowerTerm.equals(generic));
    }

    /**
     * Map generic terms to specific foods based on context
     */
    private List<String> mapToSpecificFoods(String genericTerm, List<EntityAnnotation> allLabels) {
        List<String> specificFoods = new ArrayList<>();
        String lowerTerm = genericTerm.toLowerCase();
        
        // Get all detected labels for context
        List<String> contextLabels = allLabels.stream()
            .map(annotation -> annotation.getDescription().toLowerCase())
            .collect(Collectors.toList());
        
        if (lowerTerm.equals("bean")) {
            // Look for clues about bean type
            if (contextLabels.contains("black") || contextLabels.contains("dark")) {
                specificFoods.add("black beans");
            } else if (contextLabels.contains("red") || contextLabels.contains("kidney")) {
                specificFoods.add("kidney beans");
            } else if (contextLabels.contains("white") || contextLabels.contains("navy")) {
                specificFoods.add("navy beans");
            } else {
                specificFoods.add("black beans"); // Default for bowl images
            }
        } else if (lowerTerm.equals("vegetable")) {
            // Common vegetables in bowl meals
            specificFoods.addAll(Arrays.asList("cherry tomatoes", "avocado", "corn", "bell pepper"));
        } else if (lowerTerm.equals("fruit")) {
            // Likely fruits in savory bowls
            specificFoods.addAll(Arrays.asList("avocado", "tomato"));
        } else if (lowerTerm.equals("grain")) {
            specificFoods.addAll(Arrays.asList("quinoa", "brown rice", "white rice"));
        }
        
        return specificFoods;
    }

    /**
     * Create bounding box from Google Vision BoundingPoly (for regular vertices)
     */
    private FoodDetection.BoundingBox createBoundingBox(BoundingPoly boundingPoly) {
        List<Vertex> vertices = boundingPoly.getVerticesList();
        if (vertices.isEmpty()) {
            return new FoodDetection.BoundingBox(0, 0, 0, 0);
        }

        int minX = vertices.stream().mapToInt(Vertex::getX).min().orElse(0);
        int maxX = vertices.stream().mapToInt(Vertex::getX).max().orElse(0);
        int minY = vertices.stream().mapToInt(Vertex::getY).min().orElse(0);
        int maxY = vertices.stream().mapToInt(Vertex::getY).max().orElse(0);

        int width = maxX - minX;
        int height = maxY - minY;

        logger.debug("Creating bounding box from vertices: {}x{} at ({}, {})", width, height, minX, minY);
        return new FoodDetection.BoundingBox(minX, minY, width, height);
    }

    /**
     * Create bounding box from Google Vision normalized vertices (for object localization)
     * Converts normalized coordinates (0-1) to pixel coordinates using image dimensions
     */
    private FoodDetection.BoundingBox createBoundingBoxFromNormalized(
            BoundingPoly boundingPoly, int imageWidth, int imageHeight) {
        
        List<NormalizedVertex> normalizedVertices = boundingPoly.getNormalizedVerticesList();
        if (normalizedVertices.isEmpty()) {
            logger.warn("No normalized vertices found in bounding poly");
            return new FoodDetection.BoundingBox(0, 0, 0, 0);
        }

        // Convert normalized coordinates (0-1) to pixel coordinates
        float minX = normalizedVertices.stream()
            .map(NormalizedVertex::getX)
            .min(Float::compare)
            .orElse(0.0f);
        float maxX = normalizedVertices.stream()
            .map(NormalizedVertex::getX)
            .max(Float::compare)
            .orElse(0.0f);
        float minY = normalizedVertices.stream()
            .map(NormalizedVertex::getY)
            .min(Float::compare)
            .orElse(0.0f);
        float maxY = normalizedVertices.stream()
            .map(NormalizedVertex::getY)
            .max(Float::compare)
            .orElse(0.0f);

        // Convert to pixel coordinates
        int pixelMinX = Math.round(minX * imageWidth);
        int pixelMaxX = Math.round(maxX * imageWidth);
        int pixelMinY = Math.round(minY * imageHeight);
        int pixelMaxY = Math.round(maxY * imageHeight);

        int width = pixelMaxX - pixelMinX;
        int height = pixelMaxY - pixelMinY;

        logger.info("Normalized to pixel conversion: ({:.3f},{:.3f})-({:.3f},{:.3f}) → ({}x{}) pixels at ({}, {})", 
                   minX, minY, maxX, maxY, width, height, pixelMinX, pixelMinY);

        return new FoodDetection.BoundingBox(pixelMinX, pixelMinY, width, height);
    }

    /**
     * Extract image dimensions from Vision API image
     */
    private int[] getImageDimensions(byte[] imageBytes) {
        try {
            // For simplicity, we'll assume common image dimensions
            // In a production system, you'd decode the image to get actual dimensions
            // Most smartphone photos are around 1200-4000 pixels wide
            return new int[]{1200, 800}; // width, height - default assumption
        } catch (Exception e) {
            logger.warn("Could not determine image dimensions, using defaults: {}", e.getMessage());
            return new int[]{1200, 800};
        }
    }

    /**
     * Create default portion estimate for label-based detection
     */
    private PortionEstimationService.PortionEstimate createDefaultPortionEstimate(String foodName) {
        PortionEstimationService.PortionEstimate estimate = new PortionEstimationService.PortionEstimate();
        estimate.estimatedGrams = getDefaultPortionSize(foodName);
        estimate.estimationMethod = "default";
        estimate.referenceObject = "none";
        estimate.confidence = 0.6;
        estimate.scaleFactor = 0.0;
        estimate.dimensions = new PortionEstimationService.Dimensions(0, 0);
        return estimate;
    }

    /**
     * Get default portion size for common foods
     */
    private double getDefaultPortionSize(String foodName) {
        String lowerName = foodName.toLowerCase();
        
        if (lowerName.contains("apple")) return 150;
        if (lowerName.contains("banana")) return 120;
        if (lowerName.contains("orange")) return 180;
        if (lowerName.contains("bread")) return 30;
        if (lowerName.contains("slice")) return 50;
        if (lowerName.contains("cup")) return 200;
        
        return 100; // Default 100g portion
    }

    /**
     * Remove duplicate foods and sort by confidence
     */
    private List<FoodDetection.DetectedFood> removeDuplicatesAndSort(List<FoodDetection.DetectedFood> foods) {
        return foods.stream()
            .collect(Collectors.toMap(
                food -> food.getName().toLowerCase(),
                food -> food,
                (existing, replacement) -> existing.getConfidence() > replacement.getConfidence() 
                    ? existing : replacement))
            .values()
            .stream()
            .sorted((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()))
            .collect(Collectors.toList());
    }

    /**
     * Get all food detections
     */
    public List<FoodDetection> getAllDetections() {
        return foodDetectionRepository.findAll();
    }

    /**
     * Get food detection by ID
     */
    public Optional<FoodDetection> getDetectionById(String id) {
        return foodDetectionRepository.findById(id);
    }

    /**
     * Search detections by food name
     */
    public List<FoodDetection> searchByFoodName(String foodName) {
        return foodDetectionRepository.findByDetectedFoodsNameContainingIgnoreCase(foodName);
    }

    /**
     * Get recent detections
     */
    public List<FoodDetection> getRecentDetections(int days) {
        return foodDetectionRepository.findByCreatedAtAfter(
            java.time.LocalDateTime.now().minusDays(days)
        );
    }

    /**
     * Mock food detection for testing
     */
    private List<FoodDetection.DetectedFood> mockFoodDetection(String filename) {
        logger.info("Using mock food detection for: {}", filename);
        
        List<FoodDetection.DetectedFood> mockFoods = new ArrayList<>();
        
        FoodDetection.DetectedFood apple = new FoodDetection.DetectedFood();
        apple.setName("Apple");
        apple.setConfidence(0.85f);
        apple.setCategory("Fruit");
        apple.setEstimatedGrams(150);
        apple.setEstimatedCalories(78);
        apple.setEstimationMethod("mock");
        apple.setReferenceObject("none");
        
        FoodDetection.NutritionInfo appleNutrition = new FoodDetection.NutritionInfo();
        appleNutrition.setCalories(78);
        appleNutrition.setCarbs(21);
        appleNutrition.setFiber(4);
        apple.setNutritionInfo(appleNutrition);
        
        mockFoods.add(apple);
        
        return mockFoods;
    }

    /**
     * Capitalize first letter of each word
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        
        return Arrays.stream(str.split(" "))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }

    /**
     * Get food category based on name
     */
    private String getFoodCategory(String foodName) {
        String lowerName = foodName.toLowerCase();
        
        if (lowerName.matches(".*\\b(apple|banana|orange|grape|strawberry|blueberry|pear|peach)\\b.*")) {
            return "Fruit";
        } else if (lowerName.matches(".*\\b(carrot|broccoli|spinach|tomato|cucumber|onion|pepper)\\b.*")) {
            return "Vegetable";
        } else if (lowerName.matches(".*\\b(chicken|beef|fish|pork|turkey|meat)\\b.*")) {
            return "Protein";
        } else if (lowerName.matches(".*\\b(bread|rice|pasta|cereal|oats)\\b.*")) {
            return "Grain";
        } else if (lowerName.matches(".*\\b(milk|cheese|yogurt|butter)\\b.*")) {
            return "Dairy";
        } else {
            return "Other";
        }
    }

    // ===== DETAILED LOGGING HELPER METHODS =====

    /**
     * Enhanced filtering with detailed logging
     */
    private boolean isValidFoodLabel(String label, float confidence) {
        // Check confidence threshold
        if (confidence < 0.7f) {
            logger.debug("  Filtering: {} rejected - low confidence ({:.2f})", label, confidence);
            return false;
        }

        // Check if specific food item
        if (!isSpecificFoodItem(label)) {
            logger.debug("  Filtering: {} rejected - not specific food", label);
            return false;
        }

        // Check if generic term
        if (isGenericTerm(label)) {
            logger.debug("  Filtering: {} rejected - generic term", label);
            return false;
        }

        logger.debug("  Filtering: {} accepted - passed all checks", label);
        return true;
    }

    /**
     * Clean food name with detailed logging
     */
    private String cleanAndLogFoodName(String originalName) {
        String cleanedName = originalName.toLowerCase()
            .replaceAll("\\b(fresh|raw|organic|natural|ripe)\\b", "")
            .replaceAll("\\b(sliced|diced|chopped|whole)\\b", "")
            .replaceAll("[^a-z\\s]", "")
            .replaceAll("\\s+", " ")
            .trim();

        if (!originalName.equals(cleanedName)) {
            logger.info("  Name cleaning: '{}' → '{}'", originalName, cleanedName);
        } else {
            logger.debug("  Name cleaning: '{}' (no changes)", originalName);
        }

        return cleanedName;
    }

    /**
     * Search USDA with semantic matching and detailed logging (OPTIMIZED)
     */
    private Optional<UsdaFoodDataService.UsdaFoodItem> searchAndLogUSDA(String foodName) {
        logger.info("  USDA query: '{}'", foodName);
        
        // First, get potential USDA candidates WITHOUT nutrition data (fast)
        List<UsdaFoodDataService.UsdaFoodCandidate> candidates = 
            usdaFoodDataService.searchFoodCandidates(foodName, 10); // Get top 10 candidates
        
        if (candidates.isEmpty()) {
            logger.warn("  ✗ No USDA candidates found for: '{}'", foodName);
            return Optional.empty();
        }
        
        logger.info("  Found {} USDA candidates for semantic matching", candidates.size());
        
        // Extract food descriptions for semantic matching
        List<String> candidateDescriptions = candidates.stream()
            .map(candidate -> candidate.description)
            .collect(java.util.stream.Collectors.toList());
        
        // Use semantic matching to find the best match
        SemanticMatchingService.SemanticMatch semanticMatch = 
            semanticMatchingService.findBestMatch(foodName, candidateDescriptions);
        
        if (semanticMatch.getBestMatch() != null && semanticMatch.getSimilarity() > 0.3) {
            // Find the corresponding USDA candidate
            UsdaFoodDataService.UsdaFoodCandidate bestCandidate = candidates.stream()
                .filter(candidate -> candidate.description.equals(semanticMatch.getBestMatch()))
                .findFirst()
                .orElse(candidates.get(0)); // Fallback to first candidate
            
            logger.info("  ✓ USDA semantic match: '{}' → '{}' (similarity: {:.3f}, method: {})", 
                       foodName, bestCandidate.description, semanticMatch.getSimilarity(), semanticMatch.getMethod());
            
            // NOW fetch nutrition data for ONLY the best match
            Optional<UsdaFoodDataService.UsdaFoodItem> nutritionData = 
                usdaFoodDataService.getFoodNutritionByCandidate(bestCandidate);
            
            if (nutritionData.isPresent()) {
                UsdaFoodDataService.UsdaFoodItem bestItem = nutritionData.get();
                logger.info("    USDA ID: {}, Calories: {:.1f} kcal/100g", 
                           bestItem.fdcId, String.format("%.1f", bestItem.caloriesPerHundredGrams));
                
                logCalorieExtraction(bestItem);
                return Optional.of(bestItem);
            } else {
                logger.warn("  ✗ Failed to fetch nutrition data for best match: '{}'", bestCandidate.description);
                return Optional.empty();
            }
            
        } else {
            logger.warn("  ✗ No good semantic match found for: '{}' (best similarity: {:.3f})", 
                       foodName, semanticMatch.getSimilarity());
            return Optional.empty();
        }
    }

    /**
     * Log calorie extraction details
     */
    private void logCalorieExtraction(UsdaFoodDataService.UsdaFoodItem item) {
        if (item.caloriesPerHundredGrams > 0) {
            logger.info("  Calories extracted: {:.1f} kcal per 100g", String.format("%.1f", item.caloriesPerHundredGrams));
        } else {
            logger.warn("  No calorie data available, using estimated value");
        }
    }

    /**
     * Detect and log reference objects with proper bounding box conversion
     */
    private Map<String, FoodDetection.BoundingBox> detectAndLogReferenceObjects(
            List<LocalizedObjectAnnotation> objects, int imageWidth, int imageHeight) {
        
        Map<String, FoodDetection.BoundingBox> referenceObjects = new HashMap<>();
        
        // Known reference objects with real-world dimensions - prioritized by reliability
        Map<String, Double> knownObjects = Map.of(
            "coin", 2.4,        // US quarter diameter in cm (most stable)
            "spoon", 15.0,      // Standard spoon length in cm
            "fork", 18.0,       // Standard fork length in cm
            "knife", 20.0,      // Standard knife length in cm
            "plate", 25.0,      // Standard dinner plate diameter in cm
            "cup", 8.5          // Standard cup diameter in cm
        );
        
        for (LocalizedObjectAnnotation obj : objects) {
            String objName = obj.getName().toLowerCase();
            
            if (knownObjects.containsKey(objName)) {
                FoodDetection.BoundingBox bbox = createBoundingBoxFromNormalized(
                    obj.getBoundingPoly(), imageWidth, imageHeight);
                referenceObjects.put(objName, bbox);
                
                double realSize = knownObjects.get(objName);
                logger.info("  Reference detected: {} → {:.1f} cm (bbox: {}x{} pixels)", 
                           objName, realSize, bbox.getWidth(), bbox.getHeight());
            }
        }
        
        if (referenceObjects.isEmpty()) {
            logger.warn("  No reference objects detected for scale estimation");
        } else {
            logger.info("  Found {} reference objects for scale estimation", referenceObjects.size());
        }
        
        return referenceObjects;
    }

    /**
     * Map generic terms to specific foods with logging
     */
    private List<String> mapAndLogSpecificFoods(String genericTerm, List<EntityAnnotation> allLabels) {
        List<String> specificFoods = new ArrayList<>();
        
        logger.info("  Mapping generic term: '{}'", genericTerm);
        
        List<String> contextLabels = allLabels.stream()
            .map(annotation -> annotation.getDescription().toLowerCase())
            .collect(Collectors.toList());
        
        List<String> mapped = mapToSpecificFoods(genericTerm, allLabels);
        
        if (!mapped.isEmpty()) {
            logger.info("  ✓ Mapped to specific foods: {}", mapped);
        } else {
            logger.debug("  No specific mapping found for: '{}'", genericTerm);
        }
        
        return mapped;
    }

    /**
     * Create detected food from YOLO detection with USDA data and portion estimation
     */
    private FoodDetection.DetectedFood createDetectedFoodFromYolo(
            String foodName, double confidence, UsdaFoodDataService.UsdaFoodItem usdaFood,
            YoloFoodDetectionService.YoloDetection yoloDetection,
            Map<String, FoodDetection.BoundingBox> referenceObjects,
            int imageWidth, int imageHeight) {

        logger.info("=== STAGE 5: PORTION ESTIMATION FOR {} (YOLO) ===", foodName.toUpperCase());
        
        FoodDetection.DetectedFood food = new FoodDetection.DetectedFood();
        food.setName(toTitleCase(foodName));
        food.setConfidence((float) confidence);
        food.setCategory(getFoodCategory(foodName));
        food.setUsdaFdcId(usdaFood.fdcId);

        // Convert YOLO bounding box to our format
        FoodDetection.BoundingBox foodBox = new FoodDetection.BoundingBox(
            yoloDetection.getX1(), yoloDetection.getY1(), 
            yoloDetection.getWidth(), yoloDetection.getHeight());
        
        logger.info("  YOLO food bounding box: {}x{} pixels at ({}, {})", 
                   foodBox.getWidth(), foodBox.getHeight(), foodBox.getX(), foodBox.getY());

        // Calculate portion size with detailed logging
        PortionEstimationService.PortionEstimate portionEstimate;
        
        if (!referenceObjects.isEmpty()) {
            // Object-based estimation with reference objects
            portionEstimate = portionEstimationService.estimatePortionWithReference(
                foodBox, referenceObjects, foodName);
            
            logPortionEstimation(foodName, portionEstimate, true);
            
        } else {
            // Default estimation
            portionEstimate = createDefaultPortionEstimate(foodName);
            logPortionEstimation(foodName, portionEstimate, false);
        }

        // Calculate final calories
        double estimatedGrams = portionEstimate.estimatedGrams;
        double caloriesPerGram = usdaFood.caloriesPerHundredGrams / 100.0;
        double totalCalories = estimatedGrams * caloriesPerGram;

        logger.info("=== STAGE 6: FINAL CALORIE CALCULATION (YOLO) ===");
        logger.info("  Food: {}", foodName);
        logger.info("  Estimated portion: {}g", String.format("%.1f", estimatedGrams));
        logger.info("  USDA calories: {} kcal/100g", String.format("%.1f", usdaFood.caloriesPerHundredGrams));
        logger.info("  Final calories: {} kcal", String.format("%.1f", totalCalories));

        // Set the calculated values
        food.setEstimatedGrams(estimatedGrams);
        food.setEstimatedCalories(totalCalories);
        food.setEstimationMethod(portionEstimate.estimationMethod);

        // Set reference object info if available
        if (portionEstimate.referenceObject != null && !portionEstimate.referenceObject.equals("none")) {
            food.setReferenceObject(portionEstimate.referenceObject);
        }

        // Set YOLO bounding box
        food.setBoundingBox(foodBox);

        return food;
    }

    /**
     * Create detected food from Vision API detection with USDA data and portion estimation
     */
    private FoodDetection.DetectedFood createDetectedFoodFromVisionAPI(
            String foodName, double confidence, UsdaFoodDataService.UsdaFoodItem usdaFood,
            LocalizedObjectAnnotation visionObject,
            Map<String, FoodDetection.BoundingBox> referenceObjects,
            int imageWidth, int imageHeight) {

        logger.info("=== STAGE 4: PORTION ESTIMATION FOR {} (VISION API) ===", foodName.toUpperCase());
        
        FoodDetection.DetectedFood food = new FoodDetection.DetectedFood();
        food.setName(toTitleCase(foodName));
        food.setConfidence((float) confidence);
        food.setCategory(getFoodCategory(foodName));
        food.setUsdaFdcId(usdaFood.fdcId);

        // Convert Vision API bounding box to our format
        // Vision API returns normalized coordinates (0.0 to 1.0)
        com.google.cloud.vision.v1.BoundingPoly boundingPoly = visionObject.getBoundingPoly();
        
        // Get vertices to calculate bounding box
        List<com.google.cloud.vision.v1.Vertex> vertices = boundingPoly.getVerticesList();
        if (vertices.size() >= 2) {
            // Calculate min/max coordinates
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            
            for (com.google.cloud.vision.v1.Vertex vertex : vertices) {
                minX = Math.min(minX, vertex.getX());
                minY = Math.min(minY, vertex.getY());
                maxX = Math.max(maxX, vertex.getX());
                maxY = Math.max(maxY, vertex.getY());
            }
            
            // Convert to pixel coordinates and create bounding box
            FoodDetection.BoundingBox foodBox = new FoodDetection.BoundingBox(
                minX, minY, maxX - minX, maxY - minY);
            
            logger.info("  Vision API food bounding box: {}x{} pixels at ({}, {})", 
                       foodBox.getWidth(), foodBox.getHeight(), foodBox.getX(), foodBox.getY());

            // Calculate portion size with detailed logging
            PortionEstimationService.PortionEstimate portionEstimate;
            
            if (!referenceObjects.isEmpty()) {
                // Object-based estimation with reference objects
                portionEstimate = portionEstimationService.estimatePortionWithReference(
                    foodBox, referenceObjects, foodName);
                
                logPortionEstimation(foodName, portionEstimate, true);
                
            } else {
                // Default estimation
                portionEstimate = createDefaultPortionEstimate(foodName);
                logPortionEstimation(foodName, portionEstimate, false);
            }

            // Calculate final calories
            double estimatedGrams = portionEstimate.estimatedGrams;
            double caloriesPerGram = usdaFood.caloriesPerHundredGrams / 100.0;
            double totalCalories = estimatedGrams * caloriesPerGram;

            logger.info("=== STAGE 5: FINAL CALORIE CALCULATION (VISION API) ===");
            logger.info("  Food: {}", foodName);
            logger.info("  Estimated portion: {}g", String.format("%.1f", estimatedGrams));
            logger.info("  USDA calories: {} kcal/100g", String.format("%.1f", usdaFood.caloriesPerHundredGrams));
            logger.info("  Final calories: {} kcal", String.format("%.1f", totalCalories));

            // Set the calculated values
            food.setEstimatedGrams(estimatedGrams);
            food.setEstimatedCalories(totalCalories);
            food.setEstimationMethod(portionEstimate.estimationMethod);

            // Set reference object info if available
            if (portionEstimate.referenceObject != null && !portionEstimate.referenceObject.equals("none")) {
                food.setReferenceObject(portionEstimate.referenceObject);
            }

            // Set Vision API bounding box
            food.setBoundingBox(foodBox);
        } else {
            logger.warn("Invalid bounding polygon for Vision API object: {}", foodName);
            // Set default values if bounding box is invalid
            PortionEstimationService.PortionEstimate defaultEstimate = createDefaultPortionEstimate(foodName);
            food.setEstimatedGrams(defaultEstimate.estimatedGrams);
            food.setEstimatedCalories(defaultEstimate.estimatedGrams * usdaFood.caloriesPerHundredGrams / 100.0);
            food.setEstimationMethod("default");
        }

        return food;
    }

    /**
     * Enhanced createDetectedFoodFromUSDA with portion estimation logging
     */
    private FoodDetection.DetectedFood createDetectedFoodFromUSDA(
            String foodName, double confidence, UsdaFoodDataService.UsdaFoodItem usdaFood,
            LocalizedObjectAnnotation objectAnnotation, List<LocalizedObjectAnnotation> allObjects,
            Map<String, FoodDetection.BoundingBox> referenceObjects) {

        logger.info("=== STAGE 5: PORTION ESTIMATION FOR {} ===", foodName.toUpperCase());
        
        FoodDetection.DetectedFood food = new FoodDetection.DetectedFood();
        food.setName(toTitleCase(foodName));
        food.setConfidence((float) confidence);
        food.setCategory(getFoodCategory(foodName));
        food.setUsdaFdcId(usdaFood.fdcId);

        // Calculate portion size with detailed logging
        PortionEstimationService.PortionEstimate portionEstimate;
        
        if (objectAnnotation != null && !referenceObjects.isEmpty()) {
            // Object-based estimation with reference objects  
            // Use default image dimensions since they're not passed to this method
            int imageWidth = 1200;  // Default width
            int imageHeight = 800;  // Default height
            FoodDetection.BoundingBox foodBox = createBoundingBoxFromNormalized(
                objectAnnotation.getBoundingPoly(), imageWidth, imageHeight);
            logger.info("  Food bounding box: {}x{} pixels", foodBox.getWidth(), foodBox.getHeight());
            
            portionEstimate = portionEstimationService.estimatePortionWithReference(
                foodBox, referenceObjects, foodName);
            
            logPortionEstimation(foodName, portionEstimate, true);
            
        } else {
            // Default estimation
            portionEstimate = createDefaultPortionEstimate(foodName);
            logPortionEstimation(foodName, portionEstimate, false);
        }

        // Calculate final calories
        double estimatedGrams = portionEstimate.estimatedGrams;
        double caloriesPerGram = usdaFood.caloriesPerHundredGrams / 100.0;
        double totalCalories = estimatedGrams * caloriesPerGram;

        logger.info("=== STAGE 6: FINAL CALORIE CALCULATION ===");
        logger.info("  Food: {}", foodName);
        logger.info("  Estimated portion: {:.1f}g", estimatedGrams);
        logger.info("  USDA calories: {:.1f} kcal/100g", usdaFood.caloriesPerHundredGrams);
        logger.info("  Final calories: {:.1f} kcal", totalCalories);

        // Set the calculated values
        food.setEstimatedGrams(estimatedGrams);
        food.setEstimatedCalories(totalCalories);
        food.setEstimationMethod(portionEstimate.estimationMethod);

        // Set reference object info if available
        if (portionEstimate.referenceObject != null && !portionEstimate.referenceObject.equals("none")) {
            food.setReferenceObject(portionEstimate.referenceObject);
        }

        // Set portion estimation details - simplified for now
        if (objectAnnotation != null) {
            // Use default image dimensions since they're not passed to this method
            int imageWidth = 1200;  // Default width
            int imageHeight = 800;  // Default height
            food.setBoundingBox(createBoundingBoxFromNormalized(
                objectAnnotation.getBoundingPoly(), imageWidth, imageHeight));
        }

        return food;
    }

    /**
     * Log detailed portion estimation results
     */
    private void logPortionEstimation(String foodName, PortionEstimationService.PortionEstimate estimate, 
                                    boolean hasReference) {
        if (hasReference) {
            logger.info("  Pixels per cm: {}", String.format("%.2f", 1.0 / estimate.scaleFactor));
            logger.info("  Food area: {} cm²", String.format("%.1f", estimate.dimensions.area));
            logger.info("  Reference object: {}", estimate.referenceObject);
            logger.info("  Scale confidence: {}", String.format("%.2f", estimate.confidence));
        } else {
            logger.info("  Using default portion estimate (no reference object)");
        }
        logger.info("  Estimated weight: {}g", String.format("%.1f", estimate.estimatedGrams));
        logger.info("  Estimation method: {}", estimate.estimationMethod);
    }

    /**
     * Convert string to title case
     */
    private String toTitleCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split(" "))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }
}
