package com.nourishcare.recipeservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "recipes")
public class Recipe {
    
    @Id
    private String id;
    
    @NotBlank(message = "Title is required")
    @TextIndexed(weight = 2)
    private String title;
    
    @TextIndexed
    private String description;
    
    private String image;
    
    @NotNull(message = "Cook time is required")
    private String cookTime;
    
    @Positive(message = "Servings must be positive")
    private int servings;
    
    @NotBlank(message = "Difficulty is required")
    private String difficulty;
    
    @NotNull(message = "Ingredients are required")
    private List<String> ingredients;
    
    @NotNull(message = "Instructions are required")
    private List<String> instructions;
    
    private int calories;
    
    private NutritionInfo nutritionInfo;
    
    private String category;
    private List<String> tags;
    private String cuisine;
    
    // Spoonacular API fields
    private Long spoonacularId;
    private boolean fromSpoonacular;
    
    // User interaction fields
    private int likes;
    private int saves;
    private double rating;
    private int reviewCount;
    
    // Metadata
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Recipe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.fromSpoonacular = false;
        this.likes = 0;
        this.saves = 0;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    public Recipe(String title, String description, List<String> ingredients, List<String> instructions) {
        this();
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
        this.updatedAt = LocalDateTime.now();
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
        this.updatedAt = LocalDateTime.now();
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
        this.updatedAt = LocalDateTime.now();
    }

    public NutritionInfo getNutritionInfo() {
        return nutritionInfo;
    }

    public void setNutritionInfo(NutritionInfo nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getSpoonacularId() {
        return spoonacularId;
    }

    public void setSpoonacularId(Long spoonacularId) {
        this.spoonacularId = spoonacularId;
    }

    public boolean isFromSpoonacular() {
        return fromSpoonacular;
    }

    public void setFromSpoonacular(boolean fromSpoonacular) {
        this.fromSpoonacular = fromSpoonacular;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Nested class for nutrition information
    public static class NutritionInfo {
        private double protein;
        private double carbs;
        private double fat;
        private double fiber;
        private double sugar;
        private double sodium;
        
        public NutritionInfo() {}
        
        public NutritionInfo(double protein, double carbs, double fat, double fiber, double sugar, double sodium) {
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.fiber = fiber;
            this.sugar = sugar;
            this.sodium = sodium;
        }

        // Getters and Setters
        public double getProtein() {
            return protein;
        }

        public void setProtein(double protein) {
            this.protein = protein;
        }

        public double getCarbs() {
            return carbs;
        }

        public void setCarbs(double carbs) {
            this.carbs = carbs;
        }

        public double getFat() {
            return fat;
        }

        public void setFat(double fat) {
            this.fat = fat;
        }

        public double getFiber() {
            return fiber;
        }

        public void setFiber(double fiber) {
            this.fiber = fiber;
        }

        public double getSugar() {
            return sugar;
        }

        public void setSugar(double sugar) {
            this.sugar = sugar;
        }

        public double getSodium() {
            return sodium;
        }

        public void setSodium(double sodium) {
            this.sodium = sodium;
        }
    }
}
