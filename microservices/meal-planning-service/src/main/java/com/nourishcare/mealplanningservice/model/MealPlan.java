package com.nourishcare.mealplanningservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "meal_plans")
public class MealPlan {
    
    @Id
    private String id;
    
    @NotBlank(message = "User ID is required")
    @Indexed
    private String userId;
    
    @NotBlank(message = "Plan name is required")
    private String planName;
    
    private String description;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private PlanStatus status = PlanStatus.ACTIVE;
    
    private List<WeeklyMealPlan> weeklyPlans;
    
    private NutritionalGoals nutritionalGoals;
    
    private List<String> dietaryRestrictions;
    
    private List<String> preferredCuisines;
    
    @Min(value = 1, message = "Serving size must be at least 1")
    private Integer servingSize = 1;
    
    private Double budgetLimit;
    
    private Boolean autoGenerateShoppingList = true;
    
    private Boolean includeLeftovers = true;
    
    private List<String> excludedIngredients;
    
    private Map<String, Object> preferences;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public MealPlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MealPlan(String userId, String planName, LocalDate startDate, LocalDate endDate) {
        this();
        this.userId = userId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Enums
    public enum PlanStatus {
        ACTIVE, COMPLETED, PAUSED, ARCHIVED
    }
    
    // Nested Classes
    public static class WeeklyMealPlan {
        private LocalDate weekStartDate;
        private List<DailyMealPlan> dailyPlans;
        
        public WeeklyMealPlan() {}
        
        public WeeklyMealPlan(LocalDate weekStartDate, List<DailyMealPlan> dailyPlans) {
            this.weekStartDate = weekStartDate;
            this.dailyPlans = dailyPlans;
        }
        
        // Getters and Setters
        public LocalDate getWeekStartDate() { return weekStartDate; }
        public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }
        
        public List<DailyMealPlan> getDailyPlans() { return dailyPlans; }
        public void setDailyPlans(List<DailyMealPlan> dailyPlans) { this.dailyPlans = dailyPlans; }
    }
    
    public static class DailyMealPlan {
        private LocalDate date;
        private Map<MealType, PlannedMeal> meals;
        private Double targetCalories;
        private NutritionInfo dailyNutrition;
        private String notes;
        
        public DailyMealPlan() {}
        
        public DailyMealPlan(LocalDate date, Map<MealType, PlannedMeal> meals) {
            this.date = date;
            this.meals = meals;
        }
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Map<MealType, PlannedMeal> getMeals() { return meals; }
        public void setMeals(Map<MealType, PlannedMeal> meals) { this.meals = meals; }
        
        public Double getTargetCalories() { return targetCalories; }
        public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }
        
        public NutritionInfo getDailyNutrition() { return dailyNutrition; }
        public void setDailyNutrition(NutritionInfo dailyNutrition) { this.dailyNutrition = dailyNutrition; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    public static class PlannedMeal {
        private String recipeId;
        private String recipeName;
        private String recipeImageUrl;
        private Integer servings;
        private Integer prepTimeMinutes;
        private Integer cookTimeMinutes;
        private List<String> ingredients;
        private NutritionInfo nutrition;
        private Boolean isLeftover = false;
        private String leftoverFromDate;
        private String notes;
        
        public PlannedMeal() {}
        
        public PlannedMeal(String recipeId, String recipeName, Integer servings) {
            this.recipeId = recipeId;
            this.recipeName = recipeName;
            this.servings = servings;
        }
        
        // Getters and Setters
        public String getRecipeId() { return recipeId; }
        public void setRecipeId(String recipeId) { this.recipeId = recipeId; }
        
        public String getRecipeName() { return recipeName; }
        public void setRecipeName(String recipeName) { this.recipeName = recipeName; }
        
        public String getRecipeImageUrl() { return recipeImageUrl; }
        public void setRecipeImageUrl(String recipeImageUrl) { this.recipeImageUrl = recipeImageUrl; }
        
        public Integer getServings() { return servings; }
        public void setServings(Integer servings) { this.servings = servings; }
        
        public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
        public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }
        
        public Integer getCookTimeMinutes() { return cookTimeMinutes; }
        public void setCookTimeMinutes(Integer cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }
        
        public List<String> getIngredients() { return ingredients; }
        public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
        
        public NutritionInfo getNutrition() { return nutrition; }
        public void setNutrition(NutritionInfo nutrition) { this.nutrition = nutrition; }
        
        public Boolean getIsLeftover() { return isLeftover; }
        public void setIsLeftover(Boolean isLeftover) { this.isLeftover = isLeftover; }
        
        public String getLeftoverFromDate() { return leftoverFromDate; }
        public void setLeftoverFromDate(String leftoverFromDate) { this.leftoverFromDate = leftoverFromDate; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    public static class NutritionalGoals {
        private Double dailyCalories;
        private Double dailyProteinGrams;
        private Double dailyCarbsGrams;
        private Double dailyFatGrams;
        private Double dailyFiberGrams;
        private Double dailySugarGrams;
        private Double dailySodiumMg;
        private String goalType; // WEIGHT_LOSS, WEIGHT_GAIN, MAINTENANCE, MUSCLE_GAIN
        
        public NutritionalGoals() {}
        
        // Getters and Setters
        public Double getDailyCalories() { return dailyCalories; }
        public void setDailyCalories(Double dailyCalories) { this.dailyCalories = dailyCalories; }
        
        public Double getDailyProteinGrams() { return dailyProteinGrams; }
        public void setDailyProteinGrams(Double dailyProteinGrams) { this.dailyProteinGrams = dailyProteinGrams; }
        
        public Double getDailyCarbsGrams() { return dailyCarbsGrams; }
        public void setDailyCarbsGrams(Double dailyCarbsGrams) { this.dailyCarbsGrams = dailyCarbsGrams; }
        
        public Double getDailyFatGrams() { return dailyFatGrams; }
        public void setDailyFatGrams(Double dailyFatGrams) { this.dailyFatGrams = dailyFatGrams; }
        
        public Double getDailyFiberGrams() { return dailyFiberGrams; }
        public void setDailyFiberGrams(Double dailyFiberGrams) { this.dailyFiberGrams = dailyFiberGrams; }
        
        public Double getDailySugarGrams() { return dailySugarGrams; }
        public void setDailySugarGrams(Double dailySugarGrams) { this.dailySugarGrams = dailySugarGrams; }
        
        public Double getDailySodiumMg() { return dailySodiumMg; }
        public void setDailySodiumMg(Double dailySodiumMg) { this.dailySodiumMg = dailySodiumMg; }
        
        public String getGoalType() { return goalType; }
        public void setGoalType(String goalType) { this.goalType = goalType; }
    }
    
    public static class NutritionInfo {
        private Double calories;
        private Double proteinGrams;
        private Double carbsGrams;
        private Double fatGrams;
        private Double fiberGrams;
        private Double sugarGrams;
        private Double sodiumMg;
        
        public NutritionInfo() {}
        
        // Getters and Setters
        public Double getCalories() { return calories; }
        public void setCalories(Double calories) { this.calories = calories; }
        
        public Double getProteinGrams() { return proteinGrams; }
        public void setProteinGrams(Double proteinGrams) { this.proteinGrams = proteinGrams; }
        
        public Double getCarbsGrams() { return carbsGrams; }
        public void setCarbsGrams(Double carbsGrams) { this.carbsGrams = carbsGrams; }
        
        public Double getFatGrams() { return fatGrams; }
        public void setFatGrams(Double fatGrams) { this.fatGrams = fatGrams; }
        
        public Double getFiberGrams() { return fiberGrams; }
        public void setFiberGrams(Double fiberGrams) { this.fiberGrams = fiberGrams; }
        
        public Double getSugarGrams() { return sugarGrams; }
        public void setSugarGrams(Double sugarGrams) { this.sugarGrams = sugarGrams; }
        
        public Double getSodiumMg() { return sodiumMg; }
        public void setSodiumMg(Double sodiumMg) { this.sodiumMg = sodiumMg; }
    }
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK_AM, SNACK_PM, DESSERT
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public PlanStatus getStatus() { return status; }
    public void setStatus(PlanStatus status) { this.status = status; }
    
    public List<WeeklyMealPlan> getWeeklyPlans() { return weeklyPlans; }
    public void setWeeklyPlans(List<WeeklyMealPlan> weeklyPlans) { this.weeklyPlans = weeklyPlans; }
    
    public NutritionalGoals getNutritionalGoals() { return nutritionalGoals; }
    public void setNutritionalGoals(NutritionalGoals nutritionalGoals) { this.nutritionalGoals = nutritionalGoals; }
    
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    
    public List<String> getPreferredCuisines() { return preferredCuisines; }
    public void setPreferredCuisines(List<String> preferredCuisines) { this.preferredCuisines = preferredCuisines; }
    
    public Integer getServingSize() { return servingSize; }
    public void setServingSize(Integer servingSize) { this.servingSize = servingSize; }
    
    public Double getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(Double budgetLimit) { this.budgetLimit = budgetLimit; }
    
    public Boolean getAutoGenerateShoppingList() { return autoGenerateShoppingList; }
    public void setAutoGenerateShoppingList(Boolean autoGenerateShoppingList) { this.autoGenerateShoppingList = autoGenerateShoppingList; }
    
    public Boolean getIncludeLeftovers() { return includeLeftovers; }
    public void setIncludeLeftovers(Boolean includeLeftovers) { this.includeLeftovers = includeLeftovers; }
    
    public List<String> getExcludedIngredients() { return excludedIngredients; }
    public void setExcludedIngredients(List<String> excludedIngredients) { this.excludedIngredients = excludedIngredients; }
    
    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
