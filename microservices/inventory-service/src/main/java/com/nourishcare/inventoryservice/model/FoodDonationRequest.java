package com.nourishcare.inventoryservice.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class FoodDonationRequest {
    
    @NotBlank(message = "Donor name is required")
    private String donorName;
    
    @NotBlank(message = "Phone number is required")
    private String donorPhone;
    
    private String donorEmail;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String pickupInstructions;
    
    @NotEmpty(message = "At least one food item ID is required")
    private List<String> foodItemIds;
    
    private String description;
    
    // Constructors
    public FoodDonationRequest() {}
    
    public FoodDonationRequest(String donorName, String donorPhone, String donorEmail, String address, 
                              String city, List<String> foodItemIds) {
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.donorEmail = donorEmail;
        this.address = address;
        this.city = city;
        this.foodItemIds = foodItemIds;
    }
    
    // Getters and Setters
    public String getDonorName() {
        return donorName;
    }
    
    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }
    
    public String getDonorPhone() {
        return donorPhone;
    }
    
    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }
    
    public String getDonorEmail() {
        return donorEmail;
    }
    
    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getPickupInstructions() {
        return pickupInstructions;
    }
    
    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }
    
    public List<String> getFoodItemIds() {
        return foodItemIds;
    }
    
    public void setFoodItemIds(List<String> foodItemIds) {
        this.foodItemIds = foodItemIds;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}