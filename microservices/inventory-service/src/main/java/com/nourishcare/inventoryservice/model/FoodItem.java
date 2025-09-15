package com.nourishcare.inventoryservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "food_items")
public class FoodItem {
    
    @Id
    private String id;
    
    @NotBlank(message = "Name is required")
    @TextIndexed(weight = 2)
    private String name;
    
    @NotBlank(message = "Quantity is required")
    private String quantity;
    
    @NotNull(message = "Expiration date is required")
    private LocalDate expirationDate;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String brand;
    private String location; // Fridge, Pantry, Freezer
    private String notes;
    private String imageUrl;
    
    // Nutritional information
    private NutritionInfo nutritionInfo;
    
    // Purchase information
    private LocalDate purchaseDate;
    private Double purchasePrice;
    private String store;
    
    // Usage tracking
    private Double quantityUsed = 0.0;
    private String quantityUnit; // kg, lbs, pieces, etc.
    
    // Status flags
    private boolean isOpened = false;
    private boolean isConsumed = false;
    private LocalDate openedDate;
    private LocalDateTime consumedDate;
    private String barcode;
    
    // User information
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public FoodItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.purchaseDate = LocalDate.now();
    }

    public FoodItem(String name, String quantity, LocalDate expirationDate, String category) {
        this();
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    // Computed properties
    public long getDaysUntilExpiration() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }
    
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }
    
    public boolean isExpiringSoon(int days) {
        return getDaysUntilExpiration() <= days && getDaysUntilExpiration() >= 0;
    }
    
    public ExpirationStatus getExpirationStatus() {
        long daysUntilExp = getDaysUntilExpiration();
        if (daysUntilExp < 0) {
            return ExpirationStatus.EXPIRED;
        } else if (daysUntilExp <= 3) {
            return ExpirationStatus.EXPIRING_SOON;
        } else if (daysUntilExp <= 7) {
            return ExpirationStatus.EXPIRING_THIS_WEEK;
        } else {
            return ExpirationStatus.FRESH;
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public NutritionInfo getNutritionInfo() {
        return nutritionInfo;
    }

    public void setNutritionInfo(NutritionInfo nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getPrice() {
        return purchasePrice; // Alias for purchasePrice
    }

    public void setPrice(Double price) {
        this.purchasePrice = price;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Double quantityUsed) {
        this.quantityUsed = quantityUsed;
        this.updatedAt = LocalDateTime.now();
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUnit() {
        return quantityUnit; // Alias for quantityUnit
    }

    public void setUnit(String unit) {
        this.quantityUnit = unit;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsOpened() {
        return isOpened;
    }

    public void setIsOpened(Boolean isOpened) {
        this.isOpened = isOpened;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsConsumed() {
        return isConsumed;
    }

    public void setIsConsumed(Boolean isConsumed) {
        this.isConsumed = isConsumed;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getConsumedDate() {
        return consumedDate;
    }

    public void setConsumedDate(LocalDateTime consumedDate) {
        this.consumedDate = consumedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
        this.updatedAt = LocalDateTime.now();
    }

    public void setExpirationStatus(ExpirationStatus expirationStatus) {
        // This is a computed property, but we can add a setter for manual override if needed
        // Usually this would be computed from getExpirationStatus()
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

    // Nested classes
    public static class NutritionInfo {
        private int calories;
        private double protein;
        private double carbs;
        private double fat;
        private double fiber;
        private double sugar;
        private double sodium;
        
        public NutritionInfo() {}
        
        public NutritionInfo(int calories, double protein, double carbs, double fat, double fiber, double sugar, double sodium) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.fiber = fiber;
            this.sugar = sugar;
            this.sodium = sodium;
        }

        // Getters and Setters
        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }

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

    public enum ExpirationStatus {
        FRESH,
        EXPIRING_THIS_WEEK,
        EXPIRING_SOON,
        EXPIRED
    }
}
