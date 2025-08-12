package com.nourishcare.service;

import com.nourishcare.model.MealPlan;
import com.nourishcare.repository.MealPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealPlanService {

    @Autowired
    private MealPlanRepository mealPlanRepository;

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public Optional<MealPlan> getMealPlanById(String id) {
        return mealPlanRepository.findById(id);
    }

    public Optional<MealPlan> getMealPlanByDate(LocalDate date) {
        return mealPlanRepository.findByDate(date);
    }

    public MealPlan saveMealPlan(MealPlan mealPlan) {
        mealPlan.setUpdatedAt(LocalDate.now());
        return mealPlanRepository.save(mealPlan);
    }

    public void deleteMealPlan(String id) {
        mealPlanRepository.deleteById(id);
    }

    public List<MealPlan> getMealPlansForWeek(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return mealPlanRepository.findByDateBetween(startDate, endDate);
    }

    public List<MealPlan> getFutureMealPlans() {
        return mealPlanRepository.findByDateAfter(LocalDate.now().minusDays(1));
    }

    public MealPlan updateMealPlan(String id, MealPlan updatedMealPlan) {
        Optional<MealPlan> existingMealPlan = mealPlanRepository.findById(id);
        if (existingMealPlan.isPresent()) {
            MealPlan mealPlan = existingMealPlan.get();
            mealPlan.setDate(updatedMealPlan.getDate());
            mealPlan.setBreakfast(updatedMealPlan.getBreakfast());
            mealPlan.setLunch(updatedMealPlan.getLunch());
            mealPlan.setDinner(updatedMealPlan.getDinner());
            mealPlan.setUpdatedAt(LocalDate.now());
            return mealPlanRepository.save(mealPlan);
        }
        return null;
    }
}
