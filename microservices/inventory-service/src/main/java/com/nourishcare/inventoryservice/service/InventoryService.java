package com.nourishcare.inventoryservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nourishcare.inventoryservice.model.FoodItem;
import com.nourishcare.inventoryservice.model.FoodItem.ExpirationStatus;
import com.nourishcare.inventoryservice.repository.FoodItemRepository;

@Service
public class InventoryService {
    
    private final FoodItemRepository foodItemRepository;
    
    @Autowired
    public InventoryService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }
    
    /**
     * Add a new food item to inventory
     */
    public FoodItem addFoodItem(FoodItem foodItem) {
        foodItem.setCreatedAt(LocalDateTime.now());
        foodItem.setUpdatedAt(LocalDateTime.now());
        
        // Automatically calculate expiration status
        foodItem.setExpirationStatus(foodItem.getExpirationStatus());
        
        return foodItemRepository.save(foodItem);
    }
    
    /**
     * Get all food items for a user
     */
    public List<FoodItem> getFoodItemsByUserId(String userId) {
        return foodItemRepository.findByUserIdOrderByExpirationDateAsc(userId);
    }
    
    /**
     * Get food item by ID
     */
    public Optional<FoodItem> getFoodItemById(String id) {
        return foodItemRepository.findById(id);
    }
    
    /**
     * Update food item
     */
    public Optional<FoodItem> updateFoodItem(String id, FoodItem updatedItem) {
        return foodItemRepository.findById(id)
                .map(existingItem -> {
                    // Update fields
                    existingItem.setName(updatedItem.getName());
                    existingItem.setCategory(updatedItem.getCategory());
                    existingItem.setQuantity(updatedItem.getQuantity());
                    existingItem.setUnit(updatedItem.getUnit());
                    existingItem.setBrand(updatedItem.getBrand());
                    existingItem.setExpirationDate(updatedItem.getExpirationDate());
                    existingItem.setPurchaseDate(updatedItem.getPurchaseDate());
                    existingItem.setLocation(updatedItem.getLocation());
                    existingItem.setNotes(updatedItem.getNotes());
                    existingItem.setNutritionInfo(updatedItem.getNutritionInfo());
                    existingItem.setBarcode(updatedItem.getBarcode());
                    existingItem.setImageUrl(updatedItem.getImageUrl());
                    existingItem.setIsOpened(updatedItem.getIsOpened());
                    existingItem.setOpenedDate(updatedItem.getOpenedDate());
                    existingItem.setUpdatedAt(LocalDateTime.now());
                    
                    // Update expiration status
                    existingItem.setExpirationStatus(existingItem.getExpirationStatus());
                    
                    return foodItemRepository.save(existingItem);
                });
    }
    
    /**
     * Delete food item
     */
    public boolean deleteFoodItem(String id) {
        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Mark food item as consumed
     */
    public Optional<FoodItem> markAsConsumed(String id) {
        return foodItemRepository.findById(id)
                .map(item -> {
                    item.setIsConsumed(true);
                    item.setConsumedDate(LocalDateTime.now());
                    item.setUpdatedAt(LocalDateTime.now());
                    return foodItemRepository.save(item);
                });
    }
    
    /**
     * Mark food item as opened
     */
    public Optional<FoodItem> markAsOpened(String id) {
        return foodItemRepository.findById(id)
                .map(item -> {
                    item.setIsOpened(true);
                    item.setOpenedDate(LocalDate.now());
                    item.setUpdatedAt(LocalDateTime.now());
                    return foodItemRepository.save(item);
                });
    }
    
    /**
     * Get expired food items
     */
    public List<FoodItem> getExpiredItems(String userId) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        return userItems.stream()
                .filter(item -> !item.getIsConsumed() && item.getExpirationStatus() == ExpirationStatus.EXPIRED)
                .collect(Collectors.toList());
    }
    
    /**
     * Get food items expiring soon (within specified days)
     */
    public List<FoodItem> getItemsExpiringSoon(String userId, int days) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        return userItems.stream()
                .filter(item -> !item.getIsConsumed() && 
                               item.getExpirationStatus() == ExpirationStatus.EXPIRING_SOON &&
                               item.getDaysUntilExpiration() <= days)
                .collect(Collectors.toList());
    }
    
    /**
     * Get fresh food items
     */
    public List<FoodItem> getFreshItems(String userId) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        return userItems.stream()
                .filter(item -> !item.getIsConsumed() && item.getExpirationStatus() == ExpirationStatus.FRESH)
                .collect(Collectors.toList());
    }
    
    /**
     * Search food items
     */
    public List<FoodItem> searchFoodItems(String userId, String searchTerm) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        return userItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               (item.getBrand() != null && item.getBrand().toLowerCase().contains(searchTerm.toLowerCase())) ||
                               item.getCategory().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get food items by category
     */
    public List<FoodItem> getFoodItemsByCategory(String userId, String category) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        return userItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    /**
     * Get food items by location
     */
    public List<FoodItem> getFoodItemsByLocation(String userId, String location) {
        return foodItemRepository.findByUserIdAndLocationIgnoreCaseAndIsConsumedFalseOrderByExpirationDateAsc(userId, location);
    }
    
    /**
     * Get inventory statistics
     */
    public Map<String, Object> getInventoryStatistics(String userId) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        List<FoodItem> activeItems = userItems.stream()
                .filter(item -> !item.getIsConsumed())
                .collect(Collectors.toList());
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total counts
        stats.put("totalItems", activeItems.size());
        stats.put("consumedItems", userItems.size() - activeItems.size());
        
        // Expiration status counts
        long freshCount = activeItems.stream()
                .filter(item -> item.getExpirationStatus() == ExpirationStatus.FRESH)
                .count();
        long expiringSoonCount = activeItems.stream()
                .filter(item -> item.getExpirationStatus() == ExpirationStatus.EXPIRING_SOON)
                .count();
        long expiredCount = activeItems.stream()
                .filter(item -> item.getExpirationStatus() == ExpirationStatus.EXPIRED)
                .count();
        
        stats.put("freshItems", freshCount);
        stats.put("expiringSoonItems", expiringSoonCount);
        stats.put("expiredItems", expiredCount);
        
        // Category distribution
        Map<String, Long> categoryDistribution = activeItems.stream()
                .collect(Collectors.groupingBy(FoodItem::getCategory, Collectors.counting()));
        stats.put("categoryDistribution", categoryDistribution);
        
        // Location distribution
        Map<String, Long> locationDistribution = activeItems.stream()
                .collect(Collectors.groupingBy(FoodItem::getLocation, Collectors.counting()));
        stats.put("locationDistribution", locationDistribution);
        
        // Waste statistics (expired items)
        List<FoodItem> expiredItems = activeItems.stream()
                .filter(item -> item.getExpirationStatus() == ExpirationStatus.EXPIRED)
                .collect(Collectors.toList());
        
        Map<String, Long> wastedCategories = expiredItems.stream()
                .collect(Collectors.groupingBy(FoodItem::getCategory, Collectors.counting()));
        stats.put("wastedCategories", wastedCategories);
        
        double totalWasteValue = expiredItems.stream()
                .mapToDouble(item -> item.getPrice() != null ? item.getPrice() : 0.0)
                .sum();
        stats.put("totalWasteValue", totalWasteValue);
        
        return stats;
    }
    
    /**
     * Get shopping suggestions based on low inventory and consumption patterns
     */
    public List<String> getShoppingSuggestions(String userId) {
        List<FoodItem> userItems = getFoodItemsByUserId(userId);
        List<FoodItem> consumedItems = userItems.stream()
                .filter(item -> item.getIsConsumed())
                .collect(Collectors.toList());
        
        // Find frequently consumed categories with low current inventory
        Map<String, Long> consumedCategories = consumedItems.stream()
                .collect(Collectors.groupingBy(FoodItem::getCategory, Collectors.counting()));
        
        Map<String, Long> currentCategories = userItems.stream()
                .filter(item -> !item.getIsConsumed())
                .collect(Collectors.groupingBy(FoodItem::getCategory, Collectors.counting()));
        
        return consumedCategories.entrySet().stream()
                .filter(entry -> entry.getValue() >= 3) // Frequently consumed (3+ times)
                .filter(entry -> currentCategories.getOrDefault(entry.getKey(), 0L) < 2) // Low current stock
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Get items to use soon (expiring within 3 days)
     */
    public List<FoodItem> getItemsToUseSoon(String userId) {
        return getItemsExpiringSoon(userId, 3);
    }
    
    /**
     * Update quantity of food item
     */
    public Optional<FoodItem> updateQuantity(String id, Double newQuantity) {
        return foodItemRepository.findById(id)
                .map(item -> {
                    item.setQuantity(newQuantity.toString()); // Convert Double to String
                    item.setUpdatedAt(LocalDateTime.now());
                    
                    // If quantity is 0 or less, mark as consumed
                    if (newQuantity <= 0) {
                        item.setIsConsumed(true);
                        item.setConsumedDate(LocalDateTime.now());
                    }
                    
                    return foodItemRepository.save(item);
                });
    }
}
