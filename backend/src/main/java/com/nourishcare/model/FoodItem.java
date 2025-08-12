package com.nourishcare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Document(collection = "food_items")
public class FoodItem {
    @Id
    private String id;
    private String name;
    private String quantity;
    private LocalDate expirationDate;
    private String category;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public FoodItem() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public FoodItem(String name, String quantity, LocalDate expirationDate, String category) {
        this();
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    public long getDaysUntilExpiration() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.expirationDate);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}
