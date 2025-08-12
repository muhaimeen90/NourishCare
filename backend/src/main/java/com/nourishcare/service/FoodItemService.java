package com.nourishcare.service;

import com.nourishcare.model.FoodItem;
import com.nourishcare.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItem> getFoodItemById(String id) {
        return foodItemRepository.findById(id);
    }

    public FoodItem saveFoodItem(FoodItem foodItem) {
        foodItem.setUpdatedAt(LocalDate.now());
        return foodItemRepository.save(foodItem);
    }

    public void deleteFoodItem(String id) {
        foodItemRepository.deleteById(id);
    }

    public List<FoodItem> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category);
    }

    public List<FoodItem> getExpiringSoonItems(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return foodItemRepository.findByExpirationDateBetween(LocalDate.now(), cutoffDate);
    }

    public List<FoodItem> getExpiredItems() {
        return foodItemRepository.findByExpirationDateBefore(LocalDate.now());
    }

    public List<FoodItem> searchFoodItems(String name) {
        return foodItemRepository.findByNameContainingIgnoreCase(name);
    }

    public FoodItem updateFoodItem(String id, FoodItem updatedFoodItem) {
        Optional<FoodItem> existingItem = foodItemRepository.findById(id);
        if (existingItem.isPresent()) {
            FoodItem item = existingItem.get();
            item.setName(updatedFoodItem.getName());
            item.setQuantity(updatedFoodItem.getQuantity());
            item.setExpirationDate(updatedFoodItem.getExpirationDate());
            item.setCategory(updatedFoodItem.getCategory());
            item.setUpdatedAt(LocalDate.now());
            return foodItemRepository.save(item);
        }
        return null;
    }
}
