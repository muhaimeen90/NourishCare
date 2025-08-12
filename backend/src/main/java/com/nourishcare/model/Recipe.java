package com.nourishcare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id;
    private String title;
    private String description;
    private String image;
    private String cookTime;
    private int servings;
    private String difficulty;
    private List<String> ingredients;
    private List<String> instructions;
    private int calories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Recipe() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Recipe(String title, String description, String image, String cookTime, 
                  int servings, String difficulty, List<String> ingredients, 
                  List<String> instructions, int calories) {
        this();
        this.title = title;
        this.description = description;
        this.image = image;
        this.cookTime = cookTime;
        this.servings = servings;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.calories = calories;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCookTime() { return cookTime; }
    public void setCookTime(String cookTime) { this.cookTime = cookTime; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
