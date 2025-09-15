package com.nourishcare.inventoryservice.repository;

import com.nourishcare.inventoryservice.model.FoodItem;
import com.nourishcare.inventoryservice.model.FoodItem.ExpirationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodItemRepository extends MongoRepository<FoodItem, String> {
    
    /**
     * Find food items by user ID
     */
    List<FoodItem> findByUserIdOrderByExpirationDateAsc(String userId);
    
    /**
     * Find food items by category
     */
    List<FoodItem> findByCategoryIgnoreCaseOrderByExpirationDateAsc(String category);
    
    /**
     * Find food items by name containing keyword
     */
    List<FoodItem> findByNameContainingIgnoreCaseOrderByExpirationDateAsc(String name);
    
    /**
     * Find food items by location
     */
    List<FoodItem> findByLocationIgnoreCaseOrderByExpirationDateAsc(String location);
    
    /**
     * Find expired food items
     */
    List<FoodItem> findByExpirationDateBeforeAndIsConsumedFalseOrderByExpirationDateAsc(LocalDate date);
    
    /**
     * Find food items expiring within specified days
     */
    List<FoodItem> findByExpirationDateBetweenAndIsConsumedFalseOrderByExpirationDateAsc(LocalDate start, LocalDate end);
    
    /**
     * Find consumed food items
     */
    List<FoodItem> findByIsConsumedTrueOrderByUpdatedAtDesc(String userId);
    
    /**
     * Find opened food items
     */
    List<FoodItem> findByIsOpenedTrueAndIsConsumedFalseOrderByOpenedDateAsc();
    
    /**
     * Find food items by brand
     */
    List<FoodItem> findByBrandContainingIgnoreCaseOrderByExpirationDateAsc(String brand);
    
    /**
     * Find food items purchased within date range
     */
    List<FoodItem> findByPurchaseDateBetweenOrderByPurchaseDateDesc(LocalDate start, LocalDate end);
    
    /**
     * Find food items created within date range
     */
    List<FoodItem> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    /**
     * Count total food items by user
     */
    long countByUserIdAndIsConsumedFalse(String userId);
    
    /**
     * Count expired food items by user
     */
    long countByUserIdAndExpirationDateBeforeAndIsConsumedFalse(String userId, LocalDate date);
    
    /**
     * Count food items expiring soon by user
     */
    long countByUserIdAndExpirationDateBetweenAndIsConsumedFalse(String userId, LocalDate start, LocalDate end);
    
    /**
     * Find food items by category and user
     */
    List<FoodItem> findByUserIdAndCategoryIgnoreCaseOrderByExpirationDateAsc(String userId, String category);
    
    /**
     * Find food items by user and expiration status
     */
    @Query("{ 'userId': ?0, 'isConsumed': false, 'expirationDate': { $gte: ?1, $lte: ?2 } }")
    List<FoodItem> findByUserIdAndExpirationDateRange(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Search food items with text search
     */
    @Query("{ 'userId': ?0, $text: { $search: ?1 } }")
    List<FoodItem> findByUserIdAndTextSearch(String userId, String searchTerm);
    
    /**
     * Find most wasted categories (expired items)
     */
    @Query(value = "{ 'userId': ?0, 'expirationDate': { $lt: ?1 }, 'isConsumed': false }", 
           fields = "{ 'category': 1 }")
    List<FoodItem> findWastedItemsByUserId(String userId, LocalDate date);
    
    /**
     * Find food items by user and location
     */
    List<FoodItem> findByUserIdAndLocationIgnoreCaseAndIsConsumedFalseOrderByExpirationDateAsc(String userId, String location);
}
