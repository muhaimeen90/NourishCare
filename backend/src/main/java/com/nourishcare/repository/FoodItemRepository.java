package com.nourishcare.repository;

import com.nourishcare.model.FoodItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodItemRepository extends MongoRepository<FoodItem, String> {
    List<FoodItem> findByCategory(String category);
    List<FoodItem> findByExpirationDateBefore(LocalDate date);
    List<FoodItem> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);
    List<FoodItem> findByNameContainingIgnoreCase(String name);
}
