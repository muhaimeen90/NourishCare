package com.nourishcare.controller;

import com.nourishcare.model.MealPlan;
import com.nourishcare.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meal-plans")
@CrossOrigin(origins = "http://localhost:3000")
public class MealPlanController {

    @Autowired
    private MealPlanService mealPlanService;

    @GetMapping
    public List<MealPlan> getAllMealPlans() {
        return mealPlanService.getAllMealPlans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlan> getMealPlanById(@PathVariable String id) {
        Optional<MealPlan> mealPlan = mealPlanService.getMealPlanById(id);
        return mealPlan.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<MealPlan> getMealPlanByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<MealPlan> mealPlan = mealPlanService.getMealPlanByDate(date);
        return mealPlan.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MealPlan createMealPlan(@RequestBody MealPlan mealPlan) {
        return mealPlanService.saveMealPlan(mealPlan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlan> updateMealPlan(@PathVariable String id, @RequestBody MealPlan mealPlan) {
        MealPlan updated = mealPlanService.updateMealPlan(id, mealPlan);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String id) {
        mealPlanService.deleteMealPlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/week")
    public List<MealPlan> getMealPlansForWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return mealPlanService.getMealPlansForWeek(startDate);
    }

    @GetMapping("/future")
    public List<MealPlan> getFutureMealPlans() {
        return mealPlanService.getFutureMealPlans();
    }
}
