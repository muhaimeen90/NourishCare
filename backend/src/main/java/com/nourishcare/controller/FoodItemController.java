package com.nourishcare.controller;

import com.nourishcare.model.FoodItem;
import com.nourishcare.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping
    public List<FoodItem> getAllFoodItems() {
        return foodItemService.getAllFoodItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodItemById(@PathVariable String id) {
        Optional<FoodItem> foodItem = foodItemService.getFoodItemById(id);
        return foodItem.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FoodItem createFoodItem(@RequestBody FoodItem foodItem) {
        return foodItemService.saveFoodItem(foodItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(@PathVariable String id, @RequestBody FoodItem foodItem) {
        FoodItem updated = foodItemService.updateFoodItem(id, foodItem);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable String id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public List<FoodItem> getFoodItemsByCategory(@PathVariable String category) {
        return foodItemService.getFoodItemsByCategory(category);
    }

    @GetMapping("/expiring-soon")
    public List<FoodItem> getExpiringSoonItems(@RequestParam(defaultValue = "3") int days) {
        return foodItemService.getExpiringSoonItems(days);
    }

    @GetMapping("/expired")
    public List<FoodItem> getExpiredItems() {
        return foodItemService.getExpiredItems();
    }

    @GetMapping("/search")
    public List<FoodItem> searchFoodItems(@RequestParam String name) {
        return foodItemService.searchFoodItems(name);
    }
}
