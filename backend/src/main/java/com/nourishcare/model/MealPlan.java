package com.nourishcare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "meal_plans")
public class MealPlan {
    @Id
    private String id;
    private LocalDate date;
    private Recipe breakfast;
    private Recipe lunch;
    private Recipe dinner;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public MealPlan() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public MealPlan(LocalDate date) {
        this();
        this.date = date;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Recipe getBreakfast() { return breakfast; }
    public void setBreakfast(Recipe breakfast) { this.breakfast = breakfast; }

    public Recipe getLunch() { return lunch; }
    public void setLunch(Recipe lunch) { this.lunch = lunch; }

    public Recipe getDinner() { return dinner; }
    public void setDinner(Recipe dinner) { this.dinner = dinner; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}
