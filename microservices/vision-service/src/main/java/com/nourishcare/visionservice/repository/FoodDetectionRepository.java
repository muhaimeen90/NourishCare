package com.nourishcare.visionservice.repository;

import com.nourishcare.visionservice.model.FoodDetection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodDetectionRepository extends MongoRepository<FoodDetection, String> {
    
    /**
     * Find food detections by creation date range
     */
    List<FoodDetection> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find food detections by original filename
     */
    List<FoodDetection> findByOriginalFilenameContainingIgnoreCase(String filename);
    
    /**
     * Find food detections containing specific food item
     */
    @Query("{ 'detectedFoods.name': { $regex: ?0, $options: 'i' } }")
    List<FoodDetection> findByDetectedFoodName(String foodName);
    
    /**
     * Find food detections containing specific food item (case insensitive)
     */
    @Query("{ 'detectedFoods.name': { $regex: ?0, $options: 'i' } }")
    List<FoodDetection> findByDetectedFoodsNameContainingIgnoreCase(String foodName);
    
    /**
     * Find food detections with confidence above threshold
     */
    @Query("{ 'detectedFoods.confidence': { $gte: ?0 } }")
    List<FoodDetection> findByMinConfidence(float minConfidence);
    
    /**
     * Find recent food detections (last N days)
     */
    List<FoodDetection> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    
    /**
     * Find recent food detections after date
     */
    List<FoodDetection> findByCreatedAtAfter(LocalDateTime since);
    
    /**
     * Count detections by date range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
