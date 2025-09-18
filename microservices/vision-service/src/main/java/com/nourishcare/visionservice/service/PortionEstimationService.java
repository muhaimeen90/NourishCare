package com.nourishcare.visionservice.service;

import com.google.cloud.vision.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PortionEstimationService {

    private static final Logger logger = LoggerFactory.getLogger(PortionEstimationService.class);

    // Known real-world dimensions of common reference objects (in cm)
    private static final Map<String, Double> REFERENCE_OBJECT_SIZES = createReferenceObjectSizes();
    
    private static Map<String, Double> createReferenceObjectSizes() {
        Map<String, Double> sizes = new HashMap<>();
        sizes.put("coin", 2.4);           // Quarter diameter
        sizes.put("penny", 1.9);          // Penny diameter
        sizes.put("spoon", 15.0);         // Standard spoon length
        sizes.put("fork", 18.0);          // Standard fork length
        sizes.put("knife", 20.0);         // Standard knife length
        sizes.put("cup", 8.5);            // Standard cup diameter
        sizes.put("plate", 25.0);         // Standard dinner plate diameter
        sizes.put("phone", 14.0);         // Average smartphone length
        sizes.put("hand", 18.0);          // Average adult hand length
        sizes.put("finger", 7.5);         // Average finger length
        return sizes;
    }

    // Food density estimates (grams per cubic cm) for volume-to-weight conversion
    private static final Map<String, Double> FOOD_DENSITIES = createFoodDensities();
    
    private static Map<String, Double> createFoodDensities() {
        Map<String, Double> densities = new HashMap<>();
        densities.put("apple", 0.8);          // Typical fruit density
        densities.put("banana", 0.9);
        densities.put("orange", 0.87);
        densities.put("grape", 0.85);
        densities.put("cantaloupe", 0.85);
        densities.put("melon", 0.85);
        densities.put("pumpkin", 0.8);
        densities.put("bread", 0.3);          // Light foods
        densities.put("cake", 0.5);
        densities.put("meat", 1.0);           // Dense foods
        densities.put("chicken", 1.0);
        densities.put("beef", 1.0);
        densities.put("fish", 1.0);
        densities.put("egg", 1.0);            // Eggs are dense
        densities.put("cheese", 1.1);
        densities.put("rice", 0.75);          // Cooked grains
        densities.put("pasta", 0.65);
        densities.put("potato", 0.8);         // Vegetables
        densities.put("carrot", 0.9);
        densities.put("broccoli", 0.3);       // Leafy vegetables
        return densities;
    }

    /**
     * Estimate portion size using reference objects and bounding boxes
     */
    public PortionEstimate estimatePortionSize(
            LocalizedObjectAnnotation foodObject,
            List<LocalizedObjectAnnotation> allObjects,
            String foodName) {

        logger.info("Estimating portion size for: {}", foodName);

        // Find reference objects in the image
        Optional<ReferenceObject> referenceObject = findBestReferenceObject(allObjects);

        if (referenceObject.isPresent()) {
            return estimateWithReferenceObject(foodObject, referenceObject.get(), foodName);
        } else {
            logger.info("No reference object found, using default estimation for: {}", foodName);
            return estimateWithoutReference(foodObject, foodName);
        }
    }

    /**
     * Estimate portion size using reference objects and bounding boxes (with Map input)
     */
    public PortionEstimate estimatePortionWithReference(
            com.nourishcare.visionservice.model.FoodDetection.BoundingBox foodBox,
            Map<String, com.nourishcare.visionservice.model.FoodDetection.BoundingBox> referenceObjects,
            String foodName) {

        logger.info("      Estimating portion for: {}", foodName);
        logger.info("      Food bounding box: {}x{} pixels", foodBox.getWidth(), foodBox.getHeight());

        if (referenceObjects.isEmpty()) {
            logger.info("      No reference objects available, using default estimation");
            return createDefaultEstimate(foodName);
        }

        // Use the first available reference object
        Map.Entry<String, com.nourishcare.visionservice.model.FoodDetection.BoundingBox> refEntry = 
            referenceObjects.entrySet().iterator().next();
        
        String refType = refEntry.getKey();
        com.nourishcare.visionservice.model.FoodDetection.BoundingBox refBox = refEntry.getValue();
        
        logger.info("      Using reference: {} ({}x{} pixels)", refType, refBox.getWidth(), refBox.getHeight());

        // Get real-world size of reference object
        double realRefSize = REFERENCE_OBJECT_SIZES.getOrDefault(refType, 2.4); // default to coin
        logger.info("      Real-world reference size: {} cm", String.format("%.1f", realRefSize));

        // Calculate scale (pixels per cm) - be smarter about which dimension to use
        double refSizePx;
        if (refType.equals("fork") || refType.equals("knife") || refType.equals("spoon")) {
            // For elongated objects, use the longer dimension (length)
            refSizePx = Math.max(refBox.getWidth(), refBox.getHeight());
        } else if (refType.equals("coin") || refType.equals("penny")) {
            // For circular objects, use the larger dimension (diameter)
            refSizePx = Math.max(refBox.getWidth(), refBox.getHeight());
        } else {
            // For other objects, use average of both dimensions for more stability
            refSizePx = (refBox.getWidth() + refBox.getHeight()) / 2.0;
        }
        
        double pixelsPerCm = refSizePx / realRefSize;
        logger.info("      Scale factor: {} pixels/cm", String.format("%.2f", pixelsPerCm));

        // Sanity check on scale factor - typical values should be 10-50 pixels/cm
        if (pixelsPerCm < 5 || pixelsPerCm > 100) {
            logger.warn("      ⚠️ Unusual scale factor detected: {:.2f} px/cm. Using conservative estimate.", pixelsPerCm);
            pixelsPerCm = Math.max(5, Math.min(100, pixelsPerCm)); // Clamp to reasonable range
        }

        // Calculate food dimensions in cm
        double foodWidthCm = foodBox.getWidth() / pixelsPerCm;
        double foodHeightCm = foodBox.getHeight() / pixelsPerCm;
        double foodAreaCm2 = foodWidthCm * foodHeightCm;
        
        logger.info("      Food dimensions: {}x{} cm ({} cm²)", 
                   String.format("%.1f", foodWidthCm), String.format("%.1f", foodHeightCm), String.format("%.1f", foodAreaCm2));

        // Sanity check on food dimensions - most food items should be 2-30 cm
        if (foodWidthCm > 50 || foodHeightCm > 50) {
            logger.warn("      ⚠️ Unusually large food dimensions detected. Scaling down by factor of 2.");
            foodWidthCm /= 2.0;
            foodHeightCm /= 2.0;
            foodAreaCm2 /= 4.0;
            pixelsPerCm *= 2.0; // Adjust scale factor accordingly
        }

        // Estimate volume and weight
        double estimatedVolume = estimateFoodVolume(foodWidthCm, foodHeightCm, foodName);
        double foodDensity = getFoodDensity(foodName);
        double estimatedWeight = estimatedVolume * foodDensity;
        
        logger.info("      Volume estimate: {} cm³", String.format("%.1f", estimatedVolume));
        logger.info("      Food density: {} g/cm³", String.format("%.2f", foodDensity));
        logger.info("      Weight estimate: {}g", String.format("%.1f", estimatedWeight));

        // Sanity check on weight estimates
        double maxReasonableWeight = getMaxReasonableWeight(foodName);
        if (estimatedWeight > maxReasonableWeight) {
            logger.warn("      ⚠️ Unusually heavy weight estimate ({}g > {}g max). Using maximum reasonable weight.", 
                       String.format("%.1f", estimatedWeight), String.format("%.1f", maxReasonableWeight));
            estimatedWeight = maxReasonableWeight;
        }

        PortionEstimate estimate = new PortionEstimate();
        estimate.estimatedGrams = Math.round(estimatedWeight);
        estimate.estimationMethod = "reference_object";
        estimate.referenceObject = refType;
        estimate.confidence = 0.8;
        estimate.scaleFactor = 1.0 / pixelsPerCm; // cm per pixel
        estimate.dimensions = new Dimensions(foodAreaCm2, estimatedVolume);

        return estimate;
    }

    /**
     * Create default estimate when no reference objects available
     */
    private PortionEstimate createDefaultEstimate(String foodName) {
        PortionEstimate estimate = new PortionEstimate();
        estimate.estimatedGrams = getDefaultPortionSize(foodName);
        estimate.estimationMethod = "default";
        estimate.referenceObject = "none";
        estimate.confidence = 0.6;
        estimate.scaleFactor = 0.0;
        estimate.dimensions = new Dimensions(0, 0);
        
        logger.info("      Default portion: {:.1f}g", estimate.estimatedGrams);
        return estimate;
    }

    /**
     * Find the best reference object for scale calculation
     */
    private Optional<ReferenceObject> findBestReferenceObject(List<LocalizedObjectAnnotation> objects) {
        for (LocalizedObjectAnnotation obj : objects) {
            String objectName = obj.getName().toLowerCase();
            
            for (Map.Entry<String, Double> entry : REFERENCE_OBJECT_SIZES.entrySet()) {
                if (objectName.contains(entry.getKey())) {
                    ReferenceObject refObj = new ReferenceObject();
                    refObj.name = entry.getKey();
                    refObj.realWorldSize = entry.getValue();
                    refObj.boundingBox = obj.getBoundingPoly();
                    refObj.confidence = obj.getScore();
                    
                    logger.info("Found reference object: {} (confidence: {})", 
                               refObj.name, refObj.confidence);
                    return Optional.of(refObj);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Estimate portion size using a reference object for scale
     */
    private PortionEstimate estimateWithReferenceObject(
            LocalizedObjectAnnotation foodObject,
            ReferenceObject referenceObject,
            String foodName) {

        // Calculate scale factor (pixels to cm)
        double refPixelSize = calculateBoundingBoxDiameter(referenceObject.boundingBox);
        double scaleFactor = referenceObject.realWorldSize / refPixelSize; // cm per pixel

        // Calculate food dimensions in pixels
        BoundingPoly foodBounds = foodObject.getBoundingPoly();
        double foodWidthPixels = calculateBoundingBoxWidth(foodBounds);
        double foodHeightPixels = calculateBoundingBoxHeight(foodBounds);

        // Convert to real-world dimensions (cm)
        double foodWidthCm = foodWidthPixels * scaleFactor;
        double foodHeightCm = foodHeightPixels * scaleFactor;

        // Estimate volume and weight
        double estimatedVolume = estimateFoodVolume(foodWidthCm, foodHeightCm, foodName);
        double estimatedWeight = estimatedVolume * getFoodDensity(foodName);

        PortionEstimate estimate = new PortionEstimate();
        estimate.estimatedGrams = Math.round(estimatedWeight);
        estimate.estimationMethod = "reference_object";
        estimate.referenceObject = referenceObject.name;
        estimate.confidence = Math.min(foodObject.getScore(), referenceObject.confidence);
        estimate.scaleFactor = scaleFactor;
        estimate.dimensions = new Dimensions(foodWidthCm, foodHeightCm);

        logger.info("Portion estimate with reference: {} grams (using {})", 
                   estimate.estimatedGrams, referenceObject.name);

        return estimate;
    }

    /**
     * Estimate portion size without reference object (less accurate)
     */
    private PortionEstimate estimateWithoutReference(LocalizedObjectAnnotation foodObject, String foodName) {
        // Use bounding box area relative to image size for rough estimation
        BoundingPoly bounds = foodObject.getBoundingPoly();
        double relativeArea = calculateRelativeBoundingBoxArea(bounds);

        // Default portion sizes for common foods (in grams)
        double baseWeight = getDefaultPortionSize(foodName);
        
        // Scale based on relative size in image
        double estimatedWeight = baseWeight * Math.sqrt(relativeArea);

        PortionEstimate estimate = new PortionEstimate();
        estimate.estimatedGrams = Math.round(estimatedWeight);
        estimate.estimationMethod = "default_scaling";
        estimate.referenceObject = "none";
        estimate.confidence = foodObject.getScore() * 0.6; // Lower confidence without reference
        estimate.scaleFactor = 0.0;
        estimate.dimensions = new Dimensions(0, 0);

        logger.info("Portion estimate without reference: {} grams", estimate.estimatedGrams);

        return estimate;
    }

    /**
     * Calculate bounding box width in pixels
     */
    private double calculateBoundingBoxWidth(BoundingPoly bounds) {
        List<Vertex> vertices = bounds.getVerticesList();
        if (vertices.size() < 2) return 0;

        int minX = vertices.stream().mapToInt(Vertex::getX).min().orElse(0);
        int maxX = vertices.stream().mapToInt(Vertex::getX).max().orElse(0);
        return maxX - minX;
    }

    /**
     * Calculate bounding box height in pixels
     */
    private double calculateBoundingBoxHeight(BoundingPoly bounds) {
        List<Vertex> vertices = bounds.getVerticesList();
        if (vertices.size() < 2) return 0;

        int minY = vertices.stream().mapToInt(Vertex::getY).min().orElse(0);
        int maxY = vertices.stream().mapToInt(Vertex::getY).max().orElse(0);
        return maxY - minY;
    }

    /**
     * Calculate bounding box diameter (average of width and height)
     */
    private double calculateBoundingBoxDiameter(BoundingPoly bounds) {
        double width = calculateBoundingBoxWidth(bounds);
        double height = calculateBoundingBoxHeight(bounds);
        return (width + height) / 2.0;
    }

    /**
     * Calculate relative area of bounding box compared to image
     */
    private double calculateRelativeBoundingBoxArea(BoundingPoly bounds) {
        double width = calculateBoundingBoxWidth(bounds);
        double height = calculateBoundingBoxHeight(bounds);
        
        // Assume image is roughly 1000x1000 pixels for relative calculation
        double imageArea = 1000000; // 1000 * 1000
        double boundingBoxArea = width * height;
        
        return Math.min(boundingBoxArea / imageArea, 1.0);
    }

    /**
     * Estimate food volume based on dimensions and food type
     */
    private double estimateFoodVolume(double widthCm, double heightCm, String foodName) {
        // More realistic volume estimation for common foods
        String lowerFoodName = foodName.toLowerCase();
        
        if (lowerFoodName.contains("apple") || lowerFoodName.contains("orange")) {
            // Spherical fruits - use average diameter
            double diameter = (widthCm + heightCm) / 2.0;
            double radius = diameter / 2.0;
            return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3); // Sphere volume
        } else if (lowerFoodName.contains("banana")) {
            // Cylindrical approximation
            double radius = Math.min(widthCm, heightCm) / 2.0;
            double length = Math.max(widthCm, heightCm);
            return Math.PI * Math.pow(radius, 2) * length; // Cylinder volume
        } else if (lowerFoodName.contains("egg")) {
            // Ellipsoid approximation for eggs (more realistic than rectangular)
            double a = widthCm / 2.0;   // semi-major axis
            double b = heightCm / 2.0;  // semi-minor axis  
            double c = Math.min(a, b);  // assume roughly circular cross-section
            return (4.0 / 3.0) * Math.PI * a * b * c; // Ellipsoid volume
        } else if (lowerFoodName.contains("grape") || lowerFoodName.contains("berry")) {
            // Small spherical fruits
            double diameter = Math.min(widthCm, heightCm); // Use smaller dimension
            double radius = diameter / 2.0;
            return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
        } else if (lowerFoodName.contains("bread") || lowerFoodName.contains("slice")) {
            // Thin rectangular items - use smaller depth
            double depth = Math.min(widthCm, heightCm) * 0.3; // Assume 30% of smaller dimension
            return widthCm * heightCm * depth;
        } else {
            // Default: rectangular prism approximation with conservative depth
            double depth = Math.min(widthCm, heightCm) * 0.5; // Assume 50% of smaller dimension (was 70%)
            return widthCm * heightCm * depth;
        }
    }

    /**
     * Get maximum reasonable weight for a food item (prevents absurd estimates)
     */
    private double getMaxReasonableWeight(String foodName) {
        String lowerFoodName = foodName.toLowerCase();
        
        // Maximum reasonable weights in grams for common foods
        if (lowerFoodName.contains("grape") || lowerFoodName.contains("berry")) return 10;
        if (lowerFoodName.contains("egg")) return 80;  // Large egg is ~70g
        if (lowerFoodName.contains("apple")) return 300;
        if (lowerFoodName.contains("banana")) return 200;
        if (lowerFoodName.contains("orange")) return 300;
        if (lowerFoodName.contains("pumpkin")) return 500; // For a piece/slice
        if (lowerFoodName.contains("cantaloupe") || lowerFoodName.contains("melon")) return 1000; // For a slice
        if (lowerFoodName.contains("bread")) return 100;
        if (lowerFoodName.contains("meat") || lowerFoodName.contains("chicken") || lowerFoodName.contains("beef")) return 400;
        
        // Default maximum for unknown foods
        return 500;
    }

    /**
     * Get food density for weight calculation
     */
    private double getFoodDensity(String foodName) {
        String lowerFoodName = foodName.toLowerCase();
        
        for (Map.Entry<String, Double> entry : FOOD_DENSITIES.entrySet()) {
            if (lowerFoodName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return 0.8; // Default density
    }

    /**
     * Get default portion size for food without reference
     */
    private double getDefaultPortionSize(String foodName) {
        String lowerFoodName = foodName.toLowerCase();
        
        // Default portion sizes in grams
        if (lowerFoodName.contains("apple")) return 150;
        if (lowerFoodName.contains("banana")) return 120;
        if (lowerFoodName.contains("orange")) return 180;
        if (lowerFoodName.contains("bread")) return 30;
        if (lowerFoodName.contains("slice")) return 50;
        if (lowerFoodName.contains("cup")) return 200;
        
        return 100; // Default 100g portion
    }

    // Data classes
    public static class PortionEstimate {
        public double estimatedGrams;
        public String estimationMethod;
        public String referenceObject;
        public double confidence;
        public double scaleFactor;
        public Dimensions dimensions;
    }

    public static class ReferenceObject {
        public String name;
        public double realWorldSize;
        public BoundingPoly boundingBox;
        public float confidence;
    }

    public static class Dimensions {
        public final double area;
        public final double volume;

        public Dimensions(double area, double volume) {
            this.area = area;
            this.volume = volume;
        }
    }
}