package com.nourishcare.model;

import java.util.Objects;

public class DetectedFoodItem {
    private String name;
    private String category;
    private String estimatedWeight;
    private double confidence;
    private boolean selected;

    public DetectedFoodItem() {}

    public DetectedFoodItem(String name, String category, String estimatedWeight, double confidence) {
        this.name = name;
        this.category = category;
        this.estimatedWeight = estimatedWeight;
        this.confidence = confidence;
        this.selected = true; // Default to selected
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEstimatedWeight() {
        return estimatedWeight;
    }

    public void setEstimatedWeight(String estimatedWeight) {
        this.estimatedWeight = estimatedWeight;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetectedFoodItem that = (DetectedFoodItem) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                selected == that.selected &&
                Objects.equals(name, that.name) &&
                Objects.equals(category, that.category) &&
                Objects.equals(estimatedWeight, that.estimatedWeight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, estimatedWeight, confidence, selected);
    }

    @Override
    public String toString() {
        return "DetectedFoodItem{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", estimatedWeight='" + estimatedWeight + '\'' +
                ", confidence=" + confidence +
                ", selected=" + selected +
                '}';
    }
}
