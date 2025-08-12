package com.nourishcare.controller;

import com.nourishcare.model.DetectedFoodItem;
import com.nourishcare.model.FoodItem;
import com.nourishcare.service.FoodItemService;
import com.nourishcare.service.VisionApiService;
import com.nourishcare.service.MockVisionApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vision")
@CrossOrigin(origins = "http://localhost:3000")
public class VisionApiController {

    private static final Logger logger = LoggerFactory.getLogger(VisionApiController.class);

    @Autowired(required = false)
    private VisionApiService visionApiService;

    @Autowired(required = false)
    private MockVisionApiService mockVisionApiService;

    @Autowired
    private FoodItemService foodItemService;

    @Value("${vision.api.mock:false}")
    private boolean useMockApi;

    @PostMapping("/detect-food")
    public ResponseEntity<Map<String, Object>> detectFoodItems(@RequestParam("image") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select an image file");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Please upload a valid image file");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Processing image: {} ({})", file.getOriginalFilename(), file.getSize());

            // Convert file to byte array
            byte[] imageData = file.getBytes();

            // Call appropriate Vision API service
            List<DetectedFoodItem> detectedItems;
            if (useMockApi) {
                logger.info("Using mock Vision API service");
                detectedItems = mockVisionApiService.detectFoodItems(imageData);
            } else {
                logger.info("Using Google Vision API service");
                detectedItems = visionApiService.detectFoodItems(imageData);
            }

            response.put("success", true);
            response.put("message", "Food items detected successfully");
            response.put("detectedItems", detectedItems);
            response.put("totalItems", detectedItems.size());

            logger.info("Successfully detected {} food items", detectedItems.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing image: ", e);
            response.put("success", false);
            response.put("message", "Error processing image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/save-selected-items")
    public ResponseEntity<Map<String, Object>> saveSelectedItems(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selectedItems = (List<Map<String, Object>>) request.get("selectedItems");
            
            if (selectedItems == null || selectedItems.isEmpty()) {
                response.put("success", false);
                response.put("message", "No items selected to save");
                return ResponseEntity.badRequest().body(response);
            }

            List<FoodItem> savedItems = new ArrayList<>();

            for (Map<String, Object> itemData : selectedItems) {
                String name = (String) itemData.get("name");
                String category = (String) itemData.get("category");
                String quantity = (String) itemData.get("estimatedWeight");
                Boolean selected = (Boolean) itemData.get("selected");

                if (selected != null && selected) {
                    FoodItem foodItem = new FoodItem();
                    foodItem.setName(name);
                    foodItem.setCategory(category);
                    foodItem.setQuantity(quantity);
                    
                    // Set expiration date to 7 days from now (default)
                    foodItem.setExpirationDate(LocalDate.now().plusDays(7));

                    FoodItem saved = foodItemService.saveFoodItem(foodItem);
                    savedItems.add(saved);
                    
                    logger.info("Saved food item: {} ({})", name, quantity);
                }
            }

            response.put("success", true);
            response.put("message", savedItems.size() + " items saved successfully");
            response.put("savedItems", savedItems);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error saving selected items: ", e);
            response.put("success", false);
            response.put("message", "Error saving items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Vision API Controller");
        return ResponseEntity.ok(response);
    }
}
