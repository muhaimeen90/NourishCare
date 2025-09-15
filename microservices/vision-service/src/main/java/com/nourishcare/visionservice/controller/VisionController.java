package com.nourishcare.visionservice.controller;

import com.nourishcare.visionservice.model.FoodDetection;
import com.nourishcare.visionservice.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    @Autowired
    private VisionService visionService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "vision-service");
        return ResponseEntity.ok(response);
    }

    /**
     * Upload image and detect food items
     */
    @PostMapping("/detect-food")
    public ResponseEntity<?> detectFoodItems(@RequestParam("image") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Please select an image file");
                return ResponseEntity.badRequest().body(error);
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Please upload a valid image file");
                return ResponseEntity.badRequest().body(error);
            }

            // Process image
            FoodDetection detection = visionService.detectFoodItems(file);
            
            // Format response to match frontend expectations
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Food items detected successfully");
            response.put("detectedItems", detection.getDetectedFoods());
            response.put("totalItems", detection.getDetectedFoods().size());
            response.put("detectionId", detection.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to process image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all food detections
     */
    @GetMapping("/detections")
    public ResponseEntity<List<FoodDetection>> getAllDetections() {
        List<FoodDetection> detections = visionService.getAllDetections();
        return ResponseEntity.ok(detections);
    }

    /**
     * Get food detection by ID
     */
    @GetMapping("/detections/{id}")
    public ResponseEntity<?> getDetectionById(@PathVariable String id) {
        Optional<FoodDetection> detection = visionService.getDetectionById(id);
        if (detection.isPresent()) {
            return ResponseEntity.ok(detection.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Detection not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search detections by food name
     */
    @GetMapping("/detections/search")
    public ResponseEntity<List<FoodDetection>> searchDetections(@RequestParam String foodName) {
        List<FoodDetection> detections = visionService.searchByFoodName(foodName);
        return ResponseEntity.ok(detections);
    }

    /**
     * Get recent detections
     */
    @GetMapping("/detections/recent")
    public ResponseEntity<List<FoodDetection>> getRecentDetections(
            @RequestParam(defaultValue = "7") int days) {
        List<FoodDetection> detections = visionService.getRecentDetections(days);
        return ResponseEntity.ok(detections);
    }

    /**
     * Get service statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        List<FoodDetection> allDetections = visionService.getAllDetections();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDetections", allDetections.size());
        stats.put("recentDetections", visionService.getRecentDetections(7).size());
        
        // Calculate average confidence
        double avgConfidence = allDetections.stream()
            .flatMap(detection -> detection.getDetectedFoods().stream())
            .mapToDouble(FoodDetection.DetectedFood::getConfidence)
            .average()
            .orElse(0.0);
        
        stats.put("averageConfidence", Math.round(avgConfidence * 100.0) / 100.0);
        
        // Top detected foods
        Map<String, Long> foodCounts = allDetections.stream()
            .flatMap(detection -> detection.getDetectedFoods().stream())
            .collect(java.util.stream.Collectors.groupingBy(
                FoodDetection.DetectedFood::getName,
                java.util.stream.Collectors.counting()
            ));
        
        stats.put("topFoods", foodCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                java.util.LinkedHashMap::new
            )));
        
        return ResponseEntity.ok(stats);
    }
}
