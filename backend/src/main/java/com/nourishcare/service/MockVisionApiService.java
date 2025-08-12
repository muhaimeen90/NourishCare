package com.nourishcare.service;

import com.nourishcare.model.DetectedFoodItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MockVisionApiService {

    private static final Logger logger = LoggerFactory.getLogger(MockVisionApiService.class);

    public List<DetectedFoodItem> detectFoodItems(byte[] imageData) throws IOException {
        logger.info("Using MOCK Vision API service - processing {} bytes", imageData.length);
        
        // Simulate processing delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mock detected food items based on common foods
        List<DetectedFoodItem> mockItems = Arrays.asList(
            new DetectedFoodItem("Orange", "Fruits", "150g", 0.95),
            new DetectedFoodItem("Broccoli", "Vegetables", "200g", 0.89),
            new DetectedFoodItem("Egg", "Dairy", "60g", 0.87),
            new DetectedFoodItem("Carrot", "Vegetables", "80g", 0.82),
            new DetectedFoodItem("Apple", "Fruits", "180g", 0.91)
        );

        logger.info("Mock detection completed - found {} items", mockItems.size());
        return mockItems;
    }
}
