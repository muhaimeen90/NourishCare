package com.nourishcare.inventoryservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "food_donations")
public class FoodDonation {
    
    @Id
    private String id;
    private String donorId;
    private String donorName;
    private String donorPhone;
    private String donorEmail;
    private String address;
    private String city;
    private String pickupInstructions;
    private List<FoodItem> foodItems; // Actual food items being donated
    private DonationStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum DonationStatus {
        AVAILABLE,
        TAKEN,
        CANCELLED,
        EXPIRED
    }
    
    // Constructors
    public FoodDonation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = DonationStatus.AVAILABLE;
    }
    
    public FoodDonation(String donorId, String donorName, String donorPhone, String donorEmail, 
                       String address, String city, List<FoodItem> foodItems) {
        this();
        this.donorId = donorId;
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorEmail = donorEmail;
        this.address = address;
        this.city = city;
        this.foodItems = foodItems;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDonorId() {
        return donorId;
    }
    
    public void setDonorId(String donorId) {
        this.donorId = donorId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDonorName() {
        return donorName;
    }
    
    public void setDonorName(String donorName) {
        this.donorName = donorName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDonorPhone() {
        return donorPhone;
    }
    
    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDonorEmail() {
        return donorEmail;
    }
    
    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPickupInstructions() {
        return pickupInstructions;
    }
    
    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }
    
    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
        this.updatedAt = LocalDateTime.now();
    }
    
    public DonationStatus getStatus() {
        return status;
    }
    
    public void setStatus(DonationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
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
    
    // Helper methods
    public int getTotalItems() {
        return foodItems != null ? foodItems.size() : 0;
    }
    
    public boolean hasExpiredItems() {
        if (foodItems == null) return false;
        return foodItems.stream().anyMatch(FoodItem::isExpired);
    }
    
    public boolean hasExpiringSoonItems(int days) {
        if (foodItems == null) return false;
        return foodItems.stream().anyMatch(item -> item.isExpiringSoon(days));
    }
}