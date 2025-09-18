package com.nourishcare.visionservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "food_detections")
public class FoodDetection {
    
    @Id
    private String id;
    
    private String imageUrl;
    private String originalFilename;
    private String contentType;
    private long fileSize;
    
    private List<DetectedFood> detectedFoods;
    private Map<String, Object> metadata;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public FoodDetection() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FoodDetection(String imageUrl, String originalFilename, String contentType, long fileSize) {
        this();
        this.imageUrl = imageUrl;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public List<DetectedFood> getDetectedFoods() {
        return detectedFoods;
    }

    public void setDetectedFoods(List<DetectedFood> detectedFoods) {
        this.detectedFoods = detectedFoods;
        this.updatedAt = LocalDateTime.now();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Inner class for detected food items
    public static class DetectedFood {
        private String name;
        private float confidence;
        private String category;
        private NutritionInfo nutritionInfo;
        
        // New fields for USDA integration and portion estimation
        private int usdaFdcId;
        private double estimatedGrams;
        private double estimatedCalories;
        private String estimationMethod;
        private String referenceObject;
        private PortionEstimation portionEstimation;
        private BoundingBox boundingBox;
        
        // Constructors
        public DetectedFood() {}
        
        public DetectedFood(String name, float confidence) {
            this.name = name;
            this.confidence = confidence;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public NutritionInfo getNutritionInfo() {
            return nutritionInfo;
        }

        public void setNutritionInfo(NutritionInfo nutritionInfo) {
            this.nutritionInfo = nutritionInfo;
        }

        public int getUsdaFdcId() {
            return usdaFdcId;
        }

        public void setUsdaFdcId(int usdaFdcId) {
            this.usdaFdcId = usdaFdcId;
        }

        public double getEstimatedGrams() {
            return estimatedGrams;
        }

        public void setEstimatedGrams(double estimatedGrams) {
            this.estimatedGrams = estimatedGrams;
        }

        public double getEstimatedCalories() {
            return estimatedCalories;
        }

        public void setEstimatedCalories(double estimatedCalories) {
            this.estimatedCalories = estimatedCalories;
        }

        public String getEstimationMethod() {
            return estimationMethod;
        }

        public void setEstimationMethod(String estimationMethod) {
            this.estimationMethod = estimationMethod;
        }

        public String getReferenceObject() {
            return referenceObject;
        }

        public void setReferenceObject(String referenceObject) {
            this.referenceObject = referenceObject;
        }

        public PortionEstimation getPortionEstimation() {
            return portionEstimation;
        }

        public void setPortionEstimation(PortionEstimation portionEstimation) {
            this.portionEstimation = portionEstimation;
        }

        public BoundingBox getBoundingBox() {
            return boundingBox;
        }

        public void setBoundingBox(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
        }
    }
    
    // Inner class for nutrition information
    public static class NutritionInfo {
        private int calories;
        private double protein;
        private double carbs;
        private double fat;
        private double fiber;
        private double sugar;
        
        // Constructors
        public NutritionInfo() {}
        
        public NutritionInfo(int calories, double protein, double carbs, double fat, double fiber, double sugar) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.fiber = fiber;
            this.sugar = sugar;
        }

        // Getters and Setters
        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

        public double getProtein() {
            return protein;
        }

        public void setProtein(double protein) {
            this.protein = protein;
        }

        public double getCarbs() {
            return carbs;
        }

        public void setCarbs(double carbs) {
            this.carbs = carbs;
        }

        public double getFat() {
            return fat;
        }

        public void setFat(double fat) {
            this.fat = fat;
        }

        public double getFiber() {
            return fiber;
        }

        public void setFiber(double fiber) {
            this.fiber = fiber;
        }

        public double getSugar() {
            return sugar;
        }

        public void setSugar(double sugar) {
            this.sugar = sugar;
        }
    }

    // Inner class for portion estimation details
    public static class PortionEstimation {
        private double widthCm;
        private double heightCm;
        private double scaleFactor;
        private double confidence;
        private String method;

        public PortionEstimation() {}

        public PortionEstimation(double widthCm, double heightCm, double scaleFactor, double confidence, String method) {
            this.widthCm = widthCm;
            this.heightCm = heightCm;
            this.scaleFactor = scaleFactor;
            this.confidence = confidence;
            this.method = method;
        }

        // Getters and Setters
        public double getWidthCm() {
            return widthCm;
        }

        public void setWidthCm(double widthCm) {
            this.widthCm = widthCm;
        }

        public double getHeightCm() {
            return heightCm;
        }

        public void setHeightCm(double heightCm) {
            this.heightCm = heightCm;
        }

        public double getScaleFactor() {
            return scaleFactor;
        }

        public void setScaleFactor(double scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    // Inner class for bounding box coordinates
    public static class BoundingBox {
        private int x;
        private int y;
        private int width;
        private int height;

        public BoundingBox() {}

        public BoundingBox(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // Getters and Setters
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
