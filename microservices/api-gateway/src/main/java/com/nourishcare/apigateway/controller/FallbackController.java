package com.nourishcare.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/vision")
    public ResponseEntity<Map<String, Object>> visionFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Vision service is currently unavailable. Please try again later.");
        response.put("status", "fallback");
        response.put("service", "vision-service");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/recipes")
    public ResponseEntity<Map<String, Object>> recipesFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Recipe service is currently unavailable. Please try again later.");
        response.put("status", "fallback");
        response.put("service", "recipe-service");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> inventoryFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inventory service is currently unavailable. Please try again later.");
        response.put("status", "fallback");
        response.put("service", "inventory-service");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<Map<String, Object>> mealPlansFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Meal planning service is currently unavailable. Please try again later.");
        response.put("status", "fallback");
        response.put("service", "meal-planning-service");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> usersFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User service is currently unavailable. Please try again later.");
        response.put("status", "fallback");
        response.put("service", "user-service");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
