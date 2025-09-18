package com.nourishcare.visionservice.service;

import ai.onnxruntime.*;
import ai.onnxruntime.OrtSession.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * YOLO Food Detection Service using ONNX Runtime
 * Detects food items from images using YOLOv8 model
 */
@Service
public class YoloFoodDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(YoloFoodDetectionService.class);
    
    private OrtEnvironment env;
    private OrtSession session;
    
    // YOLO model parameters
    private static final int INPUT_WIDTH = 640;
    private static final int INPUT_HEIGHT = 640;
    private static final float CONFIDENCE_THRESHOLD = 0.3f;
    private static final float NMS_THRESHOLD = 0.4f;
    
    // Food class names from test.yaml
    private static final String[] CLASS_NAMES = {
        "hot-dog", "Apple", "Artichoke", "Asparagus", "Bagel", "Baked-goods", "Banana", "Beer",
        "Bell-pepper", "Bread", "Broccoli", "Burrito", "Cabbage", "Cake", "Candy", "Cantaloupe",
        "Carrot", "Common-fig", "Cookie", "Dessert", "French-fries", "Grape", "Guacamole", "Hot-dog",
        "Ice-cream", "Muffin", "Orange", "Pancake", "Pear", "Popcorn", "Pretzel", "Strawberry",
        "Tomato", "Waffle", "food-drinks", "Cheese", "Cocktail", "Coffee", "Cooking-spray", "Crab",
        "Croissant", "Cucumber", "Doughnut", "Egg", "Fruit", "Grapefruit", "Hamburger", "Honeycomb",
        "Juice", "Lemon", "Lobster", "Mango", "Milk", "Mushroom", "Oyster", "Pasta", "Pastry",
        "Peach", "Pineapple", "Pizza", "Pomegranate", "Potato", "Pumpkin", "Radish", "Salad",
        "food", "Sandwich", "Shrimp", "Squash", "Squid", "Submarine-sandwich", "Sushi", "Taco",
        "Tart", "Tea", "Vegetable", "Watermelon", "Wine", "Winter-melon", "Zucchini", "Banh_mi",
        "Banh_trang_tron", "Banh_xeo", "Bun_bo_Hue", "Bun_dau", "Com_tam", "Goi_cuon", "Pho",
        "Hu_tieu", "Xoi"
    };
    
    @PostConstruct
    public void initialize() {
        try {
            logger.info("🚀 Initializing YOLO Food Detection Service...");
            
            // Initialize ONNX Runtime environment
            env = OrtEnvironment.getEnvironment();
            
            // Load YOLO model from resources - copy to temp file first
            ClassPathResource modelResource = new ClassPathResource("yolov8s.onnx");
            
            // Create temporary file
            java.io.File tempFile = java.io.File.createTempFile("yolov8s", ".onnx");
            tempFile.deleteOnExit();
            
            // Copy resource to temp file
            try (java.io.InputStream inputStream = modelResource.getInputStream();
                 java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            String modelPath = tempFile.getAbsolutePath();
            
            // Create ONNX session
            OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
            session = env.createSession(modelPath, sessionOptions);
            
            logger.info("✅ YOLO model loaded successfully from: {}", modelPath);
            logger.info("📊 Model supports {} food classes", CLASS_NAMES.length);
            
            // Log model input/output info
            logModelInfo();
            
        } catch (Exception e) {
            logger.error("❌ Failed to initialize YOLO Food Detection Service: {}", e.getMessage(), e);
            throw new RuntimeException("YOLO model loading failed", e);
        }
    }
    
    private void logModelInfo() {
        try {
            logger.info("🔍 YOLO Model Information:");
            
            // Input info
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            for (Map.Entry<String, NodeInfo> entry : inputInfo.entrySet()) {
                logger.info("  Input: {} -> {}", entry.getKey(), entry.getValue());
            }
            
            // Output info
            Map<String, NodeInfo> outputInfo = session.getOutputInfo();
            for (Map.Entry<String, NodeInfo> entry : outputInfo.entrySet()) {
                logger.info("  Output: {} -> {}", entry.getKey(), entry.getValue());
            }
            
        } catch (Exception e) {
            logger.warn("Could not retrieve model info: {}", e.getMessage());
        }
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) {
                session.close();
            }
            if (env != null) {
                env.close();
            }
            logger.info("🧹 YOLO model resources cleaned up");
        } catch (Exception e) {
            logger.error("Error cleaning up YOLO resources: {}", e.getMessage());
        }
    }
    
    /**
     * Detect food items in an image using YOLO model
     */
    public List<YoloDetection> detectFoodItems(BufferedImage image) {
        try {
            logger.info("🔍 Detecting food items with YOLO model...");
            
            // Preprocess image
            float[] inputArray = preprocessImage(image);
            
            // Create input tensor
            long[] inputShape = {1, 3, INPUT_HEIGHT, INPUT_WIDTH};
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputArray), inputShape);
            
            Map<String, OnnxTensor> inputs = Collections.singletonMap("images", inputTensor);
            
            // Run inference
            Result result = session.run(inputs);
            
            // Process outputs
            List<YoloDetection> detections = processOutputs(result, image.getWidth(), image.getHeight());
            
            // Cleanup
            inputTensor.close();
            result.close();
            
            logger.info("✅ YOLO detected {} food items", detections.size());
            return detections;
            
        } catch (Exception e) {
            logger.error("❌ YOLO food detection failed: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Preprocess image for YOLO model
     */
    private float[] preprocessImage(BufferedImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        
        logger.debug("📏 Original image size: {}x{}", originalWidth, originalHeight);
        
        // Resize image to model input size
        BufferedImage resizedImage = new BufferedImage(INPUT_WIDTH, INPUT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(image, 0, 0, INPUT_WIDTH, INPUT_HEIGHT, null);
        
        // Convert to normalized float array [batch, channels, height, width]
        float[] inputArray = new float[3 * INPUT_HEIGHT * INPUT_WIDTH];
        
        for (int y = 0; y < INPUT_HEIGHT; y++) {
            for (int x = 0; x < INPUT_WIDTH; x++) {
                int rgb = resizedImage.getRGB(x, y);
                
                // Extract RGB values
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Normalize to [0, 1] and arrange as CHW format
                int pixelIndex = y * INPUT_WIDTH + x;
                inputArray[pixelIndex] = r / 255.0f;                           // R channel
                inputArray[INPUT_HEIGHT * INPUT_WIDTH + pixelIndex] = g / 255.0f;     // G channel
                inputArray[2 * INPUT_HEIGHT * INPUT_WIDTH + pixelIndex] = b / 255.0f; // B channel
            }
        }
        
        return inputArray;
    }
    
    /**
     * Process YOLO model outputs
     */
    private List<YoloDetection> processOutputs(Result result, int originalWidth, int originalHeight) throws OrtException {
        List<YoloDetection> detections = new ArrayList<>();
        
        // Get output tensors
        OnnxTensor outputTensor = (OnnxTensor) result.get(0);
        float[][][] output = (float[][][]) outputTensor.getValue();
        
        logger.info("📊 YOLO output shape: batch={}, features={}, detections={}", 
                    output.length, output[0].length, output[0][0].length);
        
        // YOLOv8 output format: [batch, features, detections] where features = [x_center, y_center, width, height, class_scores...]
        int numDetections = output[0][0].length;
        int numFeatures = output[0].length;
        
        logger.info("🔍 Processing {} detections with {} features", numDetections, numFeatures);
        
        // Process each detection (iterate over the third dimension)
        for (int i = 0; i < numDetections; i++) {
            // Extract features for this detection
            float xCenter = output[0][0][i];     // x_center
            float yCenter = output[0][1][i];     // y_center 
            float width = output[0][2][i];       // width
            float height = output[0][3][i];      // height
            
            // YOLOv8 doesn't have objectness confidence, class scores start at index 4
            // Find best class from class scores (features 4 to 93 for 90 classes)
            int bestClassIndex = -1;
            float bestClassScore = 0;
            
            for (int j = 4; j < Math.min(numFeatures, 4 + CLASS_NAMES.length); j++) {
                float classScore = output[0][j][i];
                if (classScore > bestClassScore) {
                    bestClassScore = classScore;
                    bestClassIndex = j - 4; // Adjust for 0-based class index
                }
            }
            
            // Use class score as confidence (YOLOv8 doesn't have separate objectness)
            float finalConfidence = bestClassScore;
            
            if (i == 0) {
                logger.info("🎯 First detection debug: x_center={}, y_center={}, width={}, height={}, best_class={}, conf={}", 
                           xCenter, yCenter, width, height, bestClassIndex >= 0 ? CLASS_NAMES[bestClassIndex] : "none", finalConfidence);
            }
            
            // Filter by confidence threshold
            if (finalConfidence > CONFIDENCE_THRESHOLD && bestClassIndex >= 0) {
                // YOLO coordinates are relative to the 640x640 input image
                // We need to scale them back to the original image size
                float scaleX = (float) originalWidth / INPUT_WIDTH;
                float scaleY = (float) originalHeight / INPUT_HEIGHT;
                
                // Convert from center/width format to corner format and scale to original image
                float x1 = (xCenter - width / 2) * scaleX;
                float y1 = (yCenter - height / 2) * scaleY;
                float x2 = (xCenter + width / 2) * scaleX;
                float y2 = (yCenter + height / 2) * scaleY;
                
                logger.info("🎯 Processing detection: class={}, conf={}, yolo_coords=({},{},{},{}), scale=({},{})", 
                           CLASS_NAMES[bestClassIndex], finalConfidence, xCenter, yCenter, width, height,
                           scaleX, scaleY);
                logger.info("    Scaled coords: x1={}, y1={}, x2={}, y2={}", x1, y1, x2, y2);
                
                // Ensure coordinates are within image bounds
                x1 = Math.max(0, Math.min(x1, originalWidth));
                y1 = Math.max(0, Math.min(y1, originalHeight));
                x2 = Math.max(0, Math.min(x2, originalWidth));
                y2 = Math.max(0, Math.min(y2, originalHeight));
                
                logger.info("    Final coords: x1={}, y1={}, x2={}, y2={}", x1, y1, x2, y2);
                
                YoloDetection detection_obj = new YoloDetection(
                    CLASS_NAMES[bestClassIndex],
                    finalConfidence,
                    (int) x1, (int) y1, (int) x2, (int) y2
                );
                
                detections.add(detection_obj);
                
                logger.debug("🎯 Food detected: {} ({:.3f}) at [{:.0f},{:.0f},{:.0f},{:.0f}]",
                           CLASS_NAMES[bestClassIndex], finalConfidence, x1, y1, x2, y2);
            }
        }
        
        // Apply Non-Maximum Suppression
        return applyNMS(detections, NMS_THRESHOLD);
    }
    
    /**
     * Apply Non-Maximum Suppression to remove overlapping detections
     */
    private List<YoloDetection> applyNMS(List<YoloDetection> detections, float nmsThreshold) {
        if (detections.isEmpty()) return detections;
        
        // Sort by confidence (highest first)
        detections.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));
        
        List<YoloDetection> filteredDetections = new ArrayList<>();
        boolean[] suppressed = new boolean[detections.size()];
        
        for (int i = 0; i < detections.size(); i++) {
            if (suppressed[i]) continue;
            
            YoloDetection current = detections.get(i);
            filteredDetections.add(current);
            
            // Suppress overlapping detections
            for (int j = i + 1; j < detections.size(); j++) {
                if (suppressed[j]) continue;
                
                YoloDetection other = detections.get(j);
                float iou = calculateIoU(current, other);
                
                if (iou > nmsThreshold) {
                    suppressed[j] = true;
                }
            }
        }
        
        logger.info("📝 NMS: {} -> {} detections", detections.size(), filteredDetections.size());
        return filteredDetections;
    }
    
    /**
     * Calculate Intersection over Union (IoU) between two detections
     */
    private float calculateIoU(YoloDetection a, YoloDetection b) {
        // Calculate intersection area
        int x1 = Math.max(a.getX1(), b.getX1());
        int y1 = Math.max(a.getY1(), b.getY1());
        int x2 = Math.min(a.getX2(), b.getX2());
        int y2 = Math.min(a.getY2(), b.getY2());
        
        if (x2 <= x1 || y2 <= y1) return 0.0f;
        
        int intersectionArea = (x2 - x1) * (y2 - y1);
        
        // Calculate union area
        int areaA = a.getArea();
        int areaB = b.getArea();
        int unionArea = areaA + areaB - intersectionArea;
        
        return (float) intersectionArea / unionArea;
    }
    
    /**
     * YOLO Detection result
     */
    public static class YoloDetection {
        private final String className;
        private final float confidence;
        private final int x1, y1, x2, y2;
        
        public YoloDetection(String className, float confidence, int x1, int y1, int x2, int y2) {
            this.className = className;
            this.confidence = confidence;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public String getClassName() { return className; }
        public float getConfidence() { return confidence; }
        public int getX1() { return x1; }
        public int getY1() { return y1; }
        public int getX2() { return x2; }
        public int getY2() { return y2; }
        public int getWidth() { return x2 - x1; }
        public int getHeight() { return y2 - y1; }
        public int getArea() { return getWidth() * getHeight(); }
        
        @Override
        public String toString() {
            return String.format("YoloDetection{class='%s', conf=%.3f, bbox=[%d,%d,%d,%d]}", 
                               className, confidence, x1, y1, x2, y2);
        }
    }
}