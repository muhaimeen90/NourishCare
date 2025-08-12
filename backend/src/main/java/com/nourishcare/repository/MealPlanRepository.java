package com.nourishcare.repository;

import com.nourishcare.model.MealPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends MongoRepository<MealPlan, String> {
    Optional<MealPlan> findByDate(LocalDate date);
    List<MealPlan> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<MealPlan> findByDateAfter(LocalDate date);
}
