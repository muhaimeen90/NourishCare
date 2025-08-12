package com.nourishcare.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.nourishcare.model.DetectedFoodItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisionApiService {

    private static final Logger logger = LoggerFactory.getLogger(VisionApiService.class);

    @Autowired
    private ImageAnnotatorSettings imageAnnotatorSettings;

    // Food categories mapping
    private static final Map<String, String> FOOD_CATEGORIES = new HashMap<>();
    static {
        // Fruits
        FOOD_CATEGORIES.put("apple", "Fruits");
        FOOD_CATEGORIES.put("banana", "Fruits");
        FOOD_CATEGORIES.put("orange", "Fruits");
        FOOD_CATEGORIES.put("strawberry", "Fruits");
        FOOD_CATEGORIES.put("blueberry", "Fruits");
        FOOD_CATEGORIES.put("avocado", "Fruits");
        FOOD_CATEGORIES.put("grape", "Fruits");
        FOOD_CATEGORIES.put("watermelon", "Fruits");
        FOOD_CATEGORIES.put("pineapple", "Fruits");
        FOOD_CATEGORIES.put("mango", "Fruits");
        FOOD_CATEGORIES.put("peach", "Fruits");
        FOOD_CATEGORIES.put("pear", "Fruits");
        FOOD_CATEGORIES.put("cherry", "Fruits");
        FOOD_CATEGORIES.put("kiwi", "Fruits");
        FOOD_CATEGORIES.put("lemon", "Fruits");
        FOOD_CATEGORIES.put("lime", "Fruits");
        
        // Vegetables
        FOOD_CATEGORIES.put("tomato", "Vegetables");
        FOOD_CATEGORIES.put("carrot", "Vegetables");
        FOOD_CATEGORIES.put("broccoli", "Vegetables");
        FOOD_CATEGORIES.put("potato", "Vegetables");
        FOOD_CATEGORIES.put("onion", "Vegetables");
        FOOD_CATEGORIES.put("garlic", "Vegetables");
        FOOD_CATEGORIES.put("lettuce", "Vegetables");
        FOOD_CATEGORIES.put("spinach", "Vegetables");
        FOOD_CATEGORIES.put("mushroom", "Vegetables");
        FOOD_CATEGORIES.put("bell pepper", "Vegetables");
        FOOD_CATEGORIES.put("pepper", "Vegetables");
        FOOD_CATEGORIES.put("cucumber", "Vegetables");
        FOOD_CATEGORIES.put("corn", "Vegetables");
        FOOD_CATEGORIES.put("cabbage", "Vegetables");
        FOOD_CATEGORIES.put("celery", "Vegetables");
        FOOD_CATEGORIES.put("asparagus", "Vegetables");
        FOOD_CATEGORIES.put("eggplant", "Vegetables");
        FOOD_CATEGORIES.put("zucchini", "Vegetables");
        
        // Proteins
        FOOD_CATEGORIES.put("chicken", "Meat");
        FOOD_CATEGORIES.put("beef", "Meat");
        FOOD_CATEGORIES.put("pork", "Meat");
        FOOD_CATEGORIES.put("lamb", "Meat");
        FOOD_CATEGORIES.put("turkey", "Meat");
        FOOD_CATEGORIES.put("fish", "Seafood");
        FOOD_CATEGORIES.put("salmon", "Seafood");
        FOOD_CATEGORIES.put("tuna", "Seafood");
        FOOD_CATEGORIES.put("shrimp", "Seafood");
        FOOD_CATEGORIES.put("crab", "Seafood");
        FOOD_CATEGORIES.put("lobster", "Seafood");
        FOOD_CATEGORIES.put("egg", "Dairy");
        FOOD_CATEGORIES.put("eggs", "Dairy");
        
        // Grains
        FOOD_CATEGORIES.put("bread", "Grains");
        FOOD_CATEGORIES.put("rice", "Grains");
        FOOD_CATEGORIES.put("pasta", "Grains");
        FOOD_CATEGORIES.put("noodles", "Grains");
        FOOD_CATEGORIES.put("oats", "Grains");
        FOOD_CATEGORIES.put("quinoa", "Grains");
        FOOD_CATEGORIES.put("barley", "Grains");
        FOOD_CATEGORIES.put("wheat", "Grains");
        
        // Dairy
        FOOD_CATEGORIES.put("cheese", "Dairy");
        FOOD_CATEGORIES.put("milk", "Dairy");
        FOOD_CATEGORIES.put("yogurt", "Dairy");
        FOOD_CATEGORIES.put("butter", "Dairy");
        FOOD_CATEGORIES.put("cream", "Dairy");
        
        // Nuts and legumes
        FOOD_CATEGORIES.put("almond", "Nuts");
        FOOD_CATEGORIES.put("walnut", "Nuts");
        FOOD_CATEGORIES.put("peanut", "Nuts");
        FOOD_CATEGORIES.put("cashew", "Nuts");
        FOOD_CATEGORIES.put("bean", "Legumes");
        FOOD_CATEGORIES.put("beans", "Legumes");
        FOOD_CATEGORIES.put("lentil", "Legumes");
        FOOD_CATEGORIES.put("chickpea", "Legumes");
    }

    // Common food weight estimates (in grams)
    private static final Map<String, Integer> WEIGHT_ESTIMATES = new HashMap<>();
    static {
        // Fruits
        WEIGHT_ESTIMATES.put("apple", 180);
        WEIGHT_ESTIMATES.put("banana", 120);
        WEIGHT_ESTIMATES.put("orange", 150);
        WEIGHT_ESTIMATES.put("strawberry", 15);
        WEIGHT_ESTIMATES.put("blueberry", 10);
        WEIGHT_ESTIMATES.put("avocado", 150);
        WEIGHT_ESTIMATES.put("grape", 5);
        WEIGHT_ESTIMATES.put("watermelon", 300);
        WEIGHT_ESTIMATES.put("pineapple", 200);
        WEIGHT_ESTIMATES.put("mango", 200);
        WEIGHT_ESTIMATES.put("peach", 150);
        WEIGHT_ESTIMATES.put("pear", 180);
        WEIGHT_ESTIMATES.put("cherry", 8);
        WEIGHT_ESTIMATES.put("kiwi", 80);
        WEIGHT_ESTIMATES.put("lemon", 100);
        WEIGHT_ESTIMATES.put("lime", 60);
        
        // Vegetables
        WEIGHT_ESTIMATES.put("tomato", 100);
        WEIGHT_ESTIMATES.put("carrot", 80);
        WEIGHT_ESTIMATES.put("broccoli", 150);
        WEIGHT_ESTIMATES.put("potato", 200);
        WEIGHT_ESTIMATES.put("onion", 100);
        WEIGHT_ESTIMATES.put("lettuce", 80);
        WEIGHT_ESTIMATES.put("spinach", 50);
        WEIGHT_ESTIMATES.put("mushroom", 50);
        WEIGHT_ESTIMATES.put("bell pepper", 120);
        WEIGHT_ESTIMATES.put("pepper", 120);
        WEIGHT_ESTIMATES.put("cucumber", 150);
        WEIGHT_ESTIMATES.put("corn", 100);
        WEIGHT_ESTIMATES.put("cabbage", 200);
        WEIGHT_ESTIMATES.put("celery", 40);
        WEIGHT_ESTIMATES.put("asparagus", 20);
        WEIGHT_ESTIMATES.put("eggplant", 300);
        WEIGHT_ESTIMATES.put("zucchini", 200);
        
        // Proteins
        WEIGHT_ESTIMATES.put("chicken", 200);
        WEIGHT_ESTIMATES.put("beef", 200);
        WEIGHT_ESTIMATES.put("pork", 200);
        WEIGHT_ESTIMATES.put("lamb", 200);
        WEIGHT_ESTIMATES.put("turkey", 200);
        WEIGHT_ESTIMATES.put("fish", 180);
        WEIGHT_ESTIMATES.put("salmon", 180);
        WEIGHT_ESTIMATES.put("tuna", 180);
        WEIGHT_ESTIMATES.put("shrimp", 20);
        WEIGHT_ESTIMATES.put("egg", 60);
        WEIGHT_ESTIMATES.put("eggs", 60);
        
        // Grains
        WEIGHT_ESTIMATES.put("bread", 30);
        WEIGHT_ESTIMATES.put("rice", 150);
        WEIGHT_ESTIMATES.put("pasta", 100);
        WEIGHT_ESTIMATES.put("noodles", 100);
        WEIGHT_ESTIMATES.put("oats", 40);
        
        // Dairy
        WEIGHT_ESTIMATES.put("cheese", 50);
        WEIGHT_ESTIMATES.put("milk", 250);
        WEIGHT_ESTIMATES.put("yogurt", 150);
        WEIGHT_ESTIMATES.put("butter", 10);
        
        // Nuts
        WEIGHT_ESTIMATES.put("almond", 5);
        WEIGHT_ESTIMATES.put("walnut", 5);
        WEIGHT_ESTIMATES.put("peanut", 5);
        WEIGHT_ESTIMATES.put("cashew", 5);
    }

    public List<DetectedFoodItem> detectFoodItems(byte[] imageData) throws IOException {
        logger.info("Starting Google Cloud Vision API food detection for image size: {} bytes", imageData.length);
        List<DetectedFoodItem> detectedItems = new ArrayList<>();

        try {
            logger.debug("Creating ImageAnnotatorClient with configured settings...");
            try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
                logger.debug("ImageAnnotatorClient created successfully");
                
                ByteString imgBytes = ByteString.copyFrom(imageData);
                Image img = Image.newBuilder().setContent(imgBytes).build();
                
                // Use multiple detection types for better food identification
                Feature labelFeature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
                Feature objectFeature = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
                Feature textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
                
                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(labelFeature)
                        .addFeatures(objectFeature)
                        .addFeatures(textFeature)
                        .setImage(img)
                        .build();

                logger.debug("Sending request to Vision API with multiple detection types...");
                BatchAnnotateImagesResponse response = vision.batchAnnotateImages(
                        Arrays.asList(request));
                
                logger.debug("Received response from Vision API");
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        logger.error("Error in Vision API response: {}", res.getError().getMessage());
                        throw new IOException("Vision API error: " + res.getError().getMessage());
                    }

                    // Process object localizations first (most specific)
                    logger.info("Processing {} object localizations", res.getLocalizedObjectAnnotationsList().size());
                    for (LocalizedObjectAnnotation obj : res.getLocalizedObjectAnnotationsList()) {
                        String objectName = obj.getName().toLowerCase();
                        float confidence = obj.getScore();
                        
                        logger.debug("Detected object: {} with confidence: {}", objectName, confidence);
                        
                        DetectedFoodItem foodItem = createFoodItemFromLabel(objectName, confidence);
                        if (foodItem != null) {
                            detectedItems.add(foodItem);
                            logger.debug("Added food item from object detection: {}", foodItem.getName());
                        }
                    }

                    // Process label annotations (broader categories)
                    logger.info("Processing {} label annotations", res.getLabelAnnotationsList().size());
                    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                        String description = annotation.getDescription().toLowerCase();
                        float confidence = annotation.getScore();
                        
                        logger.debug("Detected label: {} with confidence: {}", description, confidence);

                        // Check if this is a food item
                        DetectedFoodItem foodItem = createFoodItemFromLabel(description, confidence);
                        if (foodItem != null) {
                            detectedItems.add(foodItem);
                            logger.debug("Added food item from label detection: {}", foodItem.getName());
                        }
                    }

                    // Process text annotations (for packaged foods)
                    if (res.hasFullTextAnnotation()) {
                        String fullText = res.getFullTextAnnotation().getText().toLowerCase();
                        logger.debug("Detected text: {}", fullText);
                        
                        // Look for food names in the detected text
                        for (String foodKeyword : FOOD_CATEGORIES.keySet()) {
                            if (fullText.contains(foodKeyword)) {
                                DetectedFoodItem foodItem = createFoodItemFromLabel(foodKeyword, 0.8f);
                                if (foodItem != null) {
                                    detectedItems.add(foodItem);
                                    logger.debug("Added food item from text detection: {}", foodItem.getName());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException in Vision API call: ", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in Vision API call: ", e);
            throw new IOException("Unexpected error in Vision API: " + e.getMessage(), e);
        }

        // Remove duplicates and sort by confidence
        detectedItems = detectedItems.stream()
                .collect(Collectors.toMap(
                    DetectedFoodItem::getName,
                    item -> item,
                    (existing, replacement) -> existing.getConfidence() > replacement.getConfidence() 
                        ? existing : replacement))
                .values()
                .stream()
                .sorted((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()))
                .collect(Collectors.toList());

        logger.info("Detected {} unique food items", detectedItems.size());
        return detectedItems;
    }

    private DetectedFoodItem createFoodItemFromLabel(String description, float confidence) {
        logger.debug("Analyzing label: {} with confidence: {}", description, confidence);
        
        // Direct match first
        if (FOOD_CATEGORIES.containsKey(description)) {
            String category = FOOD_CATEGORIES.get(description);
            String estimatedWeight = getEstimatedWeight(description);
            String displayName = capitalizeFirstLetter(description);
            logger.debug("Direct match found: {}", displayName);
            return new DetectedFoodItem(displayName, category, estimatedWeight, confidence);
        }
        
        // Check if the description contains any known food keywords
        for (String foodKeyword : FOOD_CATEGORIES.keySet()) {
            if (description.contains(foodKeyword) || foodKeyword.contains(description)) {
                String category = FOOD_CATEGORIES.get(foodKeyword);
                String estimatedWeight = getEstimatedWeight(foodKeyword);
                String displayName = capitalizeFirstLetter(foodKeyword);
                logger.debug("Keyword match found: {} from description: {}", displayName, description);
                return new DetectedFoodItem(displayName, category, estimatedWeight, confidence);
            }
        }

        // Smart mapping for generic labels to specific foods
        DetectedFoodItem smartMatch = smartFoodMapping(description, confidence);
        if (smartMatch != null) {
            logger.debug("Smart match found: {} from description: {}", smartMatch.getName(), description);
            return smartMatch;
        }

        // Check for common food-related terms that might indicate food
        if (isFoodRelated(description) && confidence > 0.7) {
            String category = categorizeGenericFood(description);
            String estimatedWeight = "100g"; // Default weight
            String displayName = capitalizeFirstLetter(description);
            logger.debug("Generic food match: {} from description: {}", displayName, description);
            return new DetectedFoodItem(displayName, category, estimatedWeight, confidence);
        }

        return null;
    }

    private DetectedFoodItem smartFoodMapping(String description, float confidence) {
        // Map generic vision API labels to specific foods based on context
        Map<String, String[]> smartMappings = new HashMap<>();
        smartMappings.put("fruit", new String[]{"apple", "banana", "orange"});
        smartMappings.put("citrus", new String[]{"orange", "lemon", "lime"});
        smartMappings.put("berry", new String[]{"strawberry", "blueberry"});
        smartMappings.put("vegetable", new String[]{"tomato", "carrot", "broccoli"});
        smartMappings.put("root vegetable", new String[]{"carrot", "potato", "onion"});
        smartMappings.put("leafy vegetable", new String[]{"lettuce", "spinach", "cabbage"});
        smartMappings.put("green vegetable", new String[]{"broccoli", "spinach", "lettuce"});
        smartMappings.put("natural foods", new String[]{"apple", "banana", "carrot"});
        smartMappings.put("whole food", new String[]{"apple", "banana", "carrot"});
        smartMappings.put("ingredient", new String[]{"onion", "garlic", "tomato"});
        smartMappings.put("produce", new String[]{"apple", "carrot", "lettuce"});
        smartMappings.put("food", new String[]{"apple", "banana", "bread"});
        
        for (Map.Entry<String, String[]> entry : smartMappings.entrySet()) {
            if (description.contains(entry.getKey())) {
                // Return the first food item from the mapping with reduced confidence
                String foodName = entry.getValue()[0];
                String category = FOOD_CATEGORIES.get(foodName);
                String estimatedWeight = getEstimatedWeight(foodName);
                String displayName = capitalizeFirstLetter(foodName);
                
                // Reduce confidence for generic mappings
                float adjustedConfidence = confidence * 0.7f;
                
                return new DetectedFoodItem(displayName, category, estimatedWeight, adjustedConfidence);
            }
        }
        
        return null;
    }

    private boolean isFoodRelated(String description) {
        String[] foodTerms = {
            "food", "meal", "dish", "plate", "bowl", "salad", "soup", "sandwich", 
            "vegetable", "fruit", "meat", "dairy", "grain", "snack", "dessert",
            "breakfast", "lunch", "dinner", "cuisine", "ingredient", "recipe"
        };
        
        for (String term : foodTerms) {
            if (description.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private String categorizeGenericFood(String description) {
        if (description.contains("vegetable") || description.contains("green")) {
            return "Vegetables";
        } else if (description.contains("fruit") || description.contains("sweet")) {
            return "Fruits";
        } else if (description.contains("meat") || description.contains("protein")) {
            return "Meat";
        } else if (description.contains("dairy") || description.contains("cheese") || description.contains("milk")) {
            return "Dairy";
        } else if (description.contains("grain") || description.contains("bread") || description.contains("rice")) {
            return "Grains";
        }
        return "Other";
    }

    private String getEstimatedWeight(String foodName) {
        Integer weight = WEIGHT_ESTIMATES.get(foodName.toLowerCase());
        return weight != null ? weight + "g" : "100g";
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
