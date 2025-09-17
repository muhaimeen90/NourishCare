package com.nourishcare.inventoryservice.controller;

import com.nourishcare.inventoryservice.model.FoodItem;
import com.nourishcare.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * Add a new food item to inventory
     */
    @PostMapping("/items")
    public ResponseEntity<FoodItem> addFoodItem(@Valid @RequestBody FoodItem foodItem) {
        try {
            FoodItem savedItem = inventoryService.addFoodItem(foodItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all food items for a user
     */
    @GetMapping("/users/{userId}/items")
    public ResponseEntity<List<FoodItem>> getFoodItemsByUserId(@PathVariable String userId) {
        try {
            List<FoodItem> items = inventoryService.getFoodItemsByUserId(userId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get food item by ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<FoodItem> getFoodItemById(@PathVariable String id) {
        Optional<FoodItem> item = inventoryService.getFoodItemById(id);
        return item.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update food item
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(@PathVariable String id, 
                                                  @Valid @RequestBody FoodItem foodItem) {
        Optional<FoodItem> updatedItem = inventoryService.updateFoodItem(id, foodItem);
        return updatedItem.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete food item
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable String id) {
        boolean deleted = inventoryService.deleteFoodItem(id);
        return deleted ? ResponseEntity.noContent().build() 
                       : ResponseEntity.notFound().build();
    }
    
    /**
     * Mark food item as consumed
     */
    @PutMapping("/items/{id}/consume")
    public ResponseEntity<FoodItem> markAsConsumed(@PathVariable String id) {
        Optional<FoodItem> updatedItem = inventoryService.markAsConsumed(id);
        return updatedItem.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Mark food item as opened
     */
    @PutMapping("/items/{id}/open")
    public ResponseEntity<FoodItem> markAsOpened(@PathVariable String id) {
        Optional<FoodItem> updatedItem = inventoryService.markAsOpened(id);
        return updatedItem.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update quantity of food item
     */
    @PutMapping("/items/{id}/quantity")
    public ResponseEntity<FoodItem> updateQuantity(@PathVariable String id, 
                                                  @RequestBody Map<String, Double> request) {
        Double quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<FoodItem> updatedItem = inventoryService.updateQuantity(id, quantity);
        return updatedItem.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get expired food items for a user
     */
    @GetMapping("/users/{userId}/items/expired")
    public ResponseEntity<List<FoodItem>> getExpiredItems(@PathVariable String userId) {
        try {
            List<FoodItem> expiredItems = inventoryService.getExpiredItems(userId);
            return ResponseEntity.ok(expiredItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get food items expiring soon for a user
     */
    @GetMapping("/users/{userId}/items/expiring-soon")
    public ResponseEntity<List<FoodItem>> getItemsExpiringSoon(
            @PathVariable String userId,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<FoodItem> expiringSoonItems = inventoryService.getItemsExpiringSoon(userId, days);
            return ResponseEntity.ok(expiringSoonItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get fresh food items for a user
     */
    @GetMapping("/users/{userId}/items/fresh")
    public ResponseEntity<List<FoodItem>> getFreshItems(@PathVariable String userId) {
        try {
            List<FoodItem> freshItems = inventoryService.getFreshItems(userId);
            return ResponseEntity.ok(freshItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get items to use soon (expiring within 3 days)
     */
    @GetMapping("/users/{userId}/items/use-soon")
    public ResponseEntity<List<FoodItem>> getItemsToUseSoon(@PathVariable String userId) {
        try {
            List<FoodItem> itemsToUseSoon = inventoryService.getItemsToUseSoon(userId);
            return ResponseEntity.ok(itemsToUseSoon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search food items
     */
    @GetMapping("/users/{userId}/items/search")
    public ResponseEntity<List<FoodItem>> searchFoodItems(
            @PathVariable String userId,
            @RequestParam String q) {
        try {
            List<FoodItem> results = inventoryService.searchFoodItems(userId, q);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get food items by category
     */
    @GetMapping("/users/{userId}/items/category/{category}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByCategory(
            @PathVariable String userId,
            @PathVariable String category) {
        try {
            List<FoodItem> items = inventoryService.getFoodItemsByCategory(userId, category);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get food items by location
     */
    @GetMapping("/users/{userId}/items/location/{location}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByLocation(
            @PathVariable String userId,
            @PathVariable String location) {
        try {
            List<FoodItem> items = inventoryService.getFoodItemsByLocation(userId, location);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get inventory statistics for a user
     */
    @GetMapping("/users/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getInventoryStatistics(@PathVariable String userId) {
        try {
            Map<String, Object> stats = inventoryService.getInventoryStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get shopping suggestions for a user
     */
    @GetMapping("/users/{userId}/shopping-suggestions")
    public ResponseEntity<List<String>> getShoppingSuggestions(@PathVariable String userId) {
        try {
            List<String> suggestions = inventoryService.getShoppingSuggestions(userId);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "inventory-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
