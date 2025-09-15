package com.nourishcare.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @NotBlank(message = "Username is required")
    @Indexed(unique = true)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    private Gender gender;
    
    private Double height; // in cm
    
    private Double weight; // in kg
    
    private ActivityLevel activityLevel;
    
    private List<String> dietaryRestrictions;
    
    private List<String> allergies;
    
    private List<String> preferredCuisines;
    
    private NutritionalGoals nutritionalGoals;
    
    private HealthProfile healthProfile;
    
    private UserPreferences preferences;
    
    private Set<UserRole> roles;
    
    private AccountStatus status = AccountStatus.ACTIVE;
    
    private String profileImageUrl;
    
    private Address address;
    
    private String timezone = "UTC";
    
    private String language = "en";
    
    private Boolean emailVerified = false;
    
    private Boolean phoneVerified = false;
    
    private String emailVerificationToken;
    
    private String phoneVerificationToken;
    
    private String resetPasswordToken;
    
    private LocalDateTime resetPasswordTokenExpiry;
    
    private LocalDateTime lastLoginAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String password, String firstName, String lastName) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }
    
    public enum ActivityLevel {
        SEDENTARY("Sedentary (little or no exercise)"),
        LIGHTLY_ACTIVE("Lightly active (light exercise/sports 1-3 days/week)"),
        MODERATELY_ACTIVE("Moderately active (moderate exercise/sports 3-5 days/week)"),
        VERY_ACTIVE("Very active (hard exercise/sports 6-7 days a week)"),
        EXTREMELY_ACTIVE("Extremely active (very hard exercise/sports & physical job)");
        
        private final String description;
        
        ActivityLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum UserRole {
        USER, ADMIN, NUTRITIONIST, PREMIUM_USER
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
    
    // Nested Classes
    public static class NutritionalGoals {
        private Double dailyCalories;
        private Double dailyProteinGrams;
        private Double dailyCarbsGrams;
        private Double dailyFatGrams;
        private Double dailyFiberGrams;
        private Double dailySugarGrams;
        private Double dailySodiumMg;
        private String goalType; // WEIGHT_LOSS, WEIGHT_GAIN, MAINTENANCE, MUSCLE_GAIN
        private Double targetWeightKg;
        private LocalDate targetDate;
        
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
        
        public Double getTargetWeightKg() { return targetWeightKg; }
        public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }
        
        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    }
    
    public static class HealthProfile {
        private List<String> medicalConditions;
        private List<String> medications;
        private String diabetesType; // NONE, TYPE1, TYPE2, GESTATIONAL
        private Boolean hasHighBloodPressure = false;
        private Boolean hasHighCholesterol = false;
        private Boolean hasHeartDisease = false;
        private Boolean hasKidneyDisease = false;
        private Boolean hasLiverDisease = false;
        private String smokingStatus; // NEVER, FORMER, CURRENT
        private String alcoholConsumption; // NONE, LIGHT, MODERATE, HEAVY
        private Integer sleepHoursPerNight;
        private String stressLevel; // LOW, MODERATE, HIGH
        
        public HealthProfile() {}
        
        // Getters and Setters
        public List<String> getMedicalConditions() { return medicalConditions; }
        public void setMedicalConditions(List<String> medicalConditions) { this.medicalConditions = medicalConditions; }
        
        public List<String> getMedications() { return medications; }
        public void setMedications(List<String> medications) { this.medications = medications; }
        
        public String getDiabetesType() { return diabetesType; }
        public void setDiabetesType(String diabetesType) { this.diabetesType = diabetesType; }
        
        public Boolean getHasHighBloodPressure() { return hasHighBloodPressure; }
        public void setHasHighBloodPressure(Boolean hasHighBloodPressure) { this.hasHighBloodPressure = hasHighBloodPressure; }
        
        public Boolean getHasHighCholesterol() { return hasHighCholesterol; }
        public void setHasHighCholesterol(Boolean hasHighCholesterol) { this.hasHighCholesterol = hasHighCholesterol; }
        
        public Boolean getHasHeartDisease() { return hasHeartDisease; }
        public void setHasHeartDisease(Boolean hasHeartDisease) { this.hasHeartDisease = hasHeartDisease; }
        
        public Boolean getHasKidneyDisease() { return hasKidneyDisease; }
        public void setHasKidneyDisease(Boolean hasKidneyDisease) { this.hasKidneyDisease = hasKidneyDisease; }
        
        public Boolean getHasLiverDisease() { return hasLiverDisease; }
        public void setHasLiverDisease(Boolean hasLiverDisease) { this.hasLiverDisease = hasLiverDisease; }
        
        public String getSmokingStatus() { return smokingStatus; }
        public void setSmokingStatus(String smokingStatus) { this.smokingStatus = smokingStatus; }
        
        public String getAlcoholConsumption() { return alcoholConsumption; }
        public void setAlcoholConsumption(String alcoholConsumption) { this.alcoholConsumption = alcoholConsumption; }
        
        public Integer getSleepHoursPerNight() { return sleepHoursPerNight; }
        public void setSleepHoursPerNight(Integer sleepHoursPerNight) { this.sleepHoursPerNight = sleepHoursPerNight; }
        
        public String getStressLevel() { return stressLevel; }
        public void setStressLevel(String stressLevel) { this.stressLevel = stressLevel; }
    }
    
    public static class UserPreferences {
        private Boolean enableNotifications = true;
        private Boolean enableEmailNotifications = true;
        private Boolean enablePushNotifications = true;
        private Boolean enableSMSNotifications = false;
        private String measurementUnit = "METRIC"; // METRIC or IMPERIAL
        private String dateFormat = "DD/MM/YYYY";
        private String timeFormat = "24H"; // 12H or 24H
        private Boolean shareDataForResearch = false;
        private Boolean publicProfile = false;
        private Map<String, Object> customSettings;
        
        public UserPreferences() {}
        
        // Getters and Setters
        public Boolean getEnableNotifications() { return enableNotifications; }
        public void setEnableNotifications(Boolean enableNotifications) { this.enableNotifications = enableNotifications; }
        
        public Boolean getEnableEmailNotifications() { return enableEmailNotifications; }
        public void setEnableEmailNotifications(Boolean enableEmailNotifications) { this.enableEmailNotifications = enableEmailNotifications; }
        
        public Boolean getEnablePushNotifications() { return enablePushNotifications; }
        public void setEnablePushNotifications(Boolean enablePushNotifications) { this.enablePushNotifications = enablePushNotifications; }
        
        public Boolean getEnableSMSNotifications() { return enableSMSNotifications; }
        public void setEnableSMSNotifications(Boolean enableSMSNotifications) { this.enableSMSNotifications = enableSMSNotifications; }
        
        public String getMeasurementUnit() { return measurementUnit; }
        public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
        
        public String getDateFormat() { return dateFormat; }
        public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
        
        public String getTimeFormat() { return timeFormat; }
        public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }
        
        public Boolean getShareDataForResearch() { return shareDataForResearch; }
        public void setShareDataForResearch(Boolean shareDataForResearch) { this.shareDataForResearch = shareDataForResearch; }
        
        public Boolean getPublicProfile() { return publicProfile; }
        public void setPublicProfile(Boolean publicProfile) { this.publicProfile = publicProfile; }
        
        public Map<String, Object> getCustomSettings() { return customSettings; }
        public void setCustomSettings(Map<String, Object> customSettings) { this.customSettings = customSettings; }
    }
    
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String zipCode;
        
        public Address() {}
        
        public Address(String street, String city, String state, String country, String zipCode) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.country = country;
            this.zipCode = zipCode;
        }
        
        // Getters and Setters
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }
    
    // Utility Methods
    public double calculateBMI() {
        if (height != null && weight != null && height > 0 && weight > 0) {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0.0;
    }
    
    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal weight";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
    
    public int getAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return 0;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public ActivityLevel getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevel activityLevel) { this.activityLevel = activityLevel; }
    
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public List<String> getPreferredCuisines() { return preferredCuisines; }
    public void setPreferredCuisines(List<String> preferredCuisines) { this.preferredCuisines = preferredCuisines; }
    
    public NutritionalGoals getNutritionalGoals() { return nutritionalGoals; }
    public void setNutritionalGoals(NutritionalGoals nutritionalGoals) { this.nutritionalGoals = nutritionalGoals; }
    
    public HealthProfile getHealthProfile() { return healthProfile; }
    public void setHealthProfile(HealthProfile healthProfile) { this.healthProfile = healthProfile; }
    
    public UserPreferences getPreferences() { return preferences; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }
    
    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
    
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public Boolean getPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(Boolean phoneVerified) { this.phoneVerified = phoneVerified; }
    
    public String getEmailVerificationToken() { return emailVerificationToken; }
    public void setEmailVerificationToken(String emailVerificationToken) { this.emailVerificationToken = emailVerificationToken; }
    
    public String getPhoneVerificationToken() { return phoneVerificationToken; }
    public void setPhoneVerificationToken(String phoneVerificationToken) { this.phoneVerificationToken = phoneVerificationToken; }
    
    public String getResetPasswordToken() { return resetPasswordToken; }
    public void setResetPasswordToken(String resetPasswordToken) { this.resetPasswordToken = resetPasswordToken; }
    
    public LocalDateTime getResetPasswordTokenExpiry() { return resetPasswordTokenExpiry; }
    public void setResetPasswordTokenExpiry(LocalDateTime resetPasswordTokenExpiry) { this.resetPasswordTokenExpiry = resetPasswordTokenExpiry; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
