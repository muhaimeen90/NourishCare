package com.nourishcare.visionservice.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.nourishcare.visionservice.model.FoodDetection;
import com.nourishcare.visionservice.repository.FoodDetectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisionService {

    @Autowired
    private FoodDetectionRepository foodDetectionRepository;

    @Value("${vision.api.mock:false}")
    private boolean useMockService;

    private final Set<String> FOOD_KEYWORDS = Set.of(
        "food", "fruit", "vegetable", "meat", "bread", "pasta", "rice", "salad",
        "pizza", "burger", "sandwich", "soup", "chicken", "beef", "fish", "cheese",
        "apple", "banana", "orange", "tomato", "potato", "carrot", "broccoli"
    );

    /**
     * Detect food items from uploaded image
     */
    public FoodDetection detectFoodItems(MultipartFile file) throws IOException {
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
        
        // Save to database
        return foodDetectionRepository.save(detection);
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
        return foodDetectionRepository.findByDetectedFoodName(foodName);
    }

    /**
     * Get recent detections
     */
    public List<FoodDetection> getRecentDetections(int days) {
        return foodDetectionRepository.findByCreatedAtAfterOrderByCreatedAtDesc(
            java.time.LocalDateTime.now().minusDays(days)
        );
    }

    /**
     * Detect food items using Google Cloud Vision API
     */
    private List<FoodDetection.DetectedFood> detectFoodItemsWithVisionAPI(MultipartFile file) throws IOException {
        List<FoodDetection.DetectedFood> detectedFoods = new ArrayList<>();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(
                Collections.singletonList(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    continue;
                }

                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    String description = annotation.getDescription().toLowerCase();
                    float score = annotation.getScore();

                    // Filter for food-related labels
                    if (isFoodRelated(description) && score > 0.5f) {
                        FoodDetection.DetectedFood food = new FoodDetection.DetectedFood();
                        food.setName(capitalize(description));
                        food.setConfidence(score);
                        food.setCategory(getFoodCategory(description));
                        food.setNutritionInfo(estimateNutrition(description));
                        
                        detectedFoods.add(food);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling Vision API: " + e.getMessage());
            // Fallback to mock data
            return mockFoodDetection(file.getOriginalFilename());
        }

        return detectedFoods;
    }

    /**
     * Mock food detection for testing
     */
    private List<FoodDetection.DetectedFood> mockFoodDetection(String filename) {
        List<FoodDetection.DetectedFood> mockFoods = new ArrayList<>();
        
        // Mock data based on filename or random selection
        String[] mockFoodNames = {"Apple", "Banana", "Chicken Breast", "Broccoli", "Rice"};
        String[] mockCategories = {"Fruit", "Fruit", "Meat", "Vegetable", "Grain"};
        
        for (int i = 0; i < 3; i++) {
            FoodDetection.DetectedFood food = new FoodDetection.DetectedFood();
            food.setName(mockFoodNames[i]);
            food.setConfidence(0.85f - (i * 0.1f));
            food.setCategory(mockCategories[i]);
            food.setNutritionInfo(getMockNutrition(mockFoodNames[i]));
            
            mockFoods.add(food);
        }
        
        return mockFoods;
    }

    /**
     * Check if detected label is food-related
     */
    private boolean isFoodRelated(String description) {
        return FOOD_KEYWORDS.stream()
            .anyMatch(keyword -> description.contains(keyword));
    }

    /**
     * Get food category based on description
     */
    private String getFoodCategory(String description) {
        if (description.contains("fruit") || Arrays.asList("apple", "banana", "orange", "grape").contains(description)) {
            return "Fruit";
        } else if (description.contains("vegetable") || Arrays.asList("broccoli", "carrot", "tomato", "lettuce").contains(description)) {
            return "Vegetable";
        } else if (description.contains("meat") || Arrays.asList("chicken", "beef", "fish", "pork").contains(description)) {
            return "Meat";
        } else if (description.contains("dairy") || Arrays.asList("cheese", "milk", "yogurt").contains(description)) {
            return "Dairy";
        } else if (Arrays.asList("bread", "rice", "pasta", "cereal").contains(description)) {
            return "Grain";
        }
        return "Other";
    }

    /**
     * Estimate nutrition based on food type (simplified)
     */
    private FoodDetection.NutritionInfo estimateNutrition(String foodName) {
        // This is a simplified estimation - in a real app, you'd use a nutrition database
        Map<String, int[]> nutritionData = new HashMap<>();
        nutritionData.put("apple", new int[]{95, 0, 25, 0, 4, 19});
        nutritionData.put("banana", new int[]{105, 1, 27, 0, 3, 14});
        nutritionData.put("chicken", new int[]{165, 31, 0, 4, 0, 0});
        nutritionData.put("broccoli", new int[]{55, 4, 11, 1, 5, 2});
        nutritionData.put("rice", new int[]{130, 3, 28, 0, 0, 0});
        
        int[] nutrition = nutritionData.getOrDefault(foodName.toLowerCase(), new int[]{100, 2, 20, 1, 2, 5});
        
        return new FoodDetection.NutritionInfo(
            nutrition[0], // calories
            nutrition[1], // protein
            nutrition[2], // carbs
            nutrition[3], // fat
            nutrition[4], // fiber
            nutrition[5]  // sugar
        );
    }

    /**
     * Get mock nutrition data
     */
    private FoodDetection.NutritionInfo getMockNutrition(String foodName) {
        return estimateNutrition(foodName);
    }

    /**
     * Capitalize first letter
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
