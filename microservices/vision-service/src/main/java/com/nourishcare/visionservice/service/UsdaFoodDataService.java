package com.nourishcare.visionservice.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsdaFoodDataService {

    private static final Logger logger = LoggerFactory.getLogger(UsdaFoodDataService.class);
    private static final String USDA_API_BASE_URL = "https://api.nal.usda.gov/fdc/v1";
    
    @Value("${USDA_API_KEY}")
    private String usdaApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Search for food item in USDA database and return nutrition info
     */
    public Optional<UsdaFoodItem> searchFoodItem(String foodName) {
        try {
            logger.info("    USDA Search: '{}'", foodName);
            
            // Clean and format the food name for search
            String cleanedFoodName = cleanFoodName(foodName);
            if (!foodName.equals(cleanedFoodName)) {
                logger.info("    Cleaned query: '{}' → '{}'", foodName, cleanedFoodName);
            }
            
            // Build the search URL with better parameters
            String searchUrl = UriComponentsBuilder
                .fromHttpUrl(USDA_API_BASE_URL + "/foods/search")
                .queryParam("api_key", usdaApiKey)
                .queryParam("query", cleanedFoodName)
                .queryParam("dataType", "Foundation,Survey (FNDDS),SR Legacy")
                .queryParam("pageSize", 25)
                .queryParam("pageNumber", 1)
                .queryParam("sortBy", "dataType.keyword")
                .queryParam("sortOrder", "asc")
                .build()
                .toUriString();

            logger.debug("    USDA API URL: {}", searchUrl);
            
            // Make the API call
            UsdaSearchResponse response = restTemplate.getForObject(searchUrl, UsdaSearchResponse.class);
            
            if (response != null && response.foods != null && !response.foods.isEmpty()) {
                logger.info("    USDA returned {} results", response.foods.size());
                
                // Log top 3 results for debugging
                for (int i = 0; i < Math.min(3, response.foods.size()); i++) {
                    UsdaFood food = response.foods.get(i);
                    logger.info("      {}. {} ({})", i + 1, food.description, food.dataType);
                }
                
                // Find the best match (prefer Foundation and SR Legacy data)
                UsdaFood bestFood = findBestFoodMatch(response.foods, cleanedFoodName);
                
                if (bestFood != null) {
                    logger.info("    Selected best match: '{}' (ID: {}, Type: {})", 
                               bestFood.description, bestFood.fdcId, bestFood.dataType);
                    
                    // Get detailed nutrition information
                    return getFoodNutrition(bestFood.fdcId, bestFood.description);
                } else {
                    logger.warn("    No suitable food match found in USDA database for: {}", foodName);
                    return Optional.empty();
                }
            } else {
                logger.warn("    No food found in USDA database for: {}", foodName);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            logger.error("    Error searching USDA database for food '{}': {}", foodName, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Search for food candidate descriptions only (no nutrition data fetching)
     * This is used for semantic matching before fetching nutrition data for best match
     */
    public List<UsdaFoodCandidate> searchFoodCandidates(String foodName, int maxResults) {
        try {
            logger.debug("    USDA Candidate Search: '{}' (max: {})", foodName, maxResults);
            
            // Clean and format the food name for search
            String cleanedFoodName = cleanFoodName(foodName);
            
            // Build the search URL with better parameters
            String searchUrl = UriComponentsBuilder
                .fromHttpUrl(USDA_API_BASE_URL + "/foods/search")
                .queryParam("api_key", usdaApiKey)
                .queryParam("query", cleanedFoodName)
                .queryParam("dataType", "Foundation,Survey (FNDDS),SR Legacy")
                .queryParam("pageSize", Math.min(maxResults * 2, 50)) // Get more to filter
                .queryParam("pageNumber", 1)
                .queryParam("sortBy", "dataType.keyword")
                .queryParam("sortOrder", "asc")
                .build()
                .toUriString();

            // Make the API request
            UsdaSearchResponse response = restTemplate.getForObject(searchUrl, UsdaSearchResponse.class);
            
            if (response != null && response.foods != null && !response.foods.isEmpty()) {
                logger.debug("    Found {} USDA candidates for '{}'", response.foods.size(), foodName);
                
                List<UsdaFoodCandidate> results = new ArrayList<>();
                
                // Convert to candidate objects (no nutrition data yet)
                for (UsdaFood food : response.foods) {
                    if (results.size() >= maxResults) break;
                    
                    UsdaFoodCandidate candidate = new UsdaFoodCandidate();
                    candidate.fdcId = food.fdcId;
                    candidate.description = food.description;
                    candidate.dataType = food.dataType;
                    results.add(candidate);
                }
                
                logger.debug("    Returning {} candidate descriptions", results.size());
                return results;
                
            } else {
                logger.debug("    No foods found in USDA database for: {}", foodName);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            logger.error("    Error searching USDA database for foods '{}': {}", foodName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Search for multiple food items in USDA database for semantic matching
     * @deprecated Use searchFoodCandidates + getFoodNutritionByCandidate for better performance
     */
    @Deprecated
    public List<UsdaFoodItem> searchFoodItems(String foodName, int maxResults) {
        try {
            logger.debug("    USDA Multi-Search: '{}' (max: {})", foodName, maxResults);
            
            // Clean and format the food name for search
            String cleanedFoodName = cleanFoodName(foodName);
            
            // Build the search URL with better parameters
            String searchUrl = UriComponentsBuilder
                .fromHttpUrl(USDA_API_BASE_URL + "/foods/search")
                .queryParam("api_key", usdaApiKey)
                .queryParam("query", cleanedFoodName)
                .queryParam("dataType", "Foundation,Survey (FNDDS),SR Legacy")
                .queryParam("pageSize", Math.min(maxResults * 2, 50)) // Get more to filter
                .queryParam("pageNumber", 1)
                .queryParam("sortBy", "dataType.keyword")
                .queryParam("sortOrder", "asc")
                .build()
                .toUriString();

            // Make the API request
            UsdaSearchResponse response = restTemplate.getForObject(searchUrl, UsdaSearchResponse.class);
            
            if (response != null && response.foods != null && !response.foods.isEmpty()) {
                logger.debug("    Found {} USDA candidates for '{}'", response.foods.size(), foodName);
                
                List<UsdaFoodItem> results = new ArrayList<>();
                
                // Convert and filter the results
                for (UsdaFood food : response.foods) {
                    if (results.size() >= maxResults) break;
                    
                    Optional<UsdaFoodItem> itemOpt = getFoodNutrition(food.fdcId, food.description);
                    if (itemOpt.isPresent() && itemOpt.get().caloriesPerHundredGrams > 0) {
                        results.add(itemOpt.get());
                    }
                }
                
                logger.debug("    Returning {} valid USDA candidates", results.size());
                return results;
                
            } else {
                logger.debug("    No foods found in USDA database for: {}", foodName);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            logger.error("    Error searching USDA database for foods '{}': {}", foodName, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get nutrition data for a specific candidate after semantic matching
     */
    public Optional<UsdaFoodItem> getFoodNutritionByCandidate(UsdaFoodCandidate candidate) {
        return getFoodNutrition(candidate.fdcId, candidate.description);
    }

    /**
     * Find the best matching food from search results
     */
    private UsdaFood findBestFoodMatch(List<UsdaFood> foods, String searchTerm) {
        // Priority order: Foundation > SR Legacy > Survey (FNDDS)
        // Also prefer simpler, more basic food descriptions
        
        UsdaFood bestMatch = null;
        int bestScore = -1;
        
        for (UsdaFood food : foods) {
            int score = calculateMatchScore(food, searchTerm);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = food;
            }
        }
        
        return bestMatch;
    }

    /**
     * Calculate match score for food item
     */
    private int calculateMatchScore(UsdaFood food, String searchTerm) {
        String description = food.description.toLowerCase();
        String dataType = food.dataType != null ? food.dataType.toLowerCase() : "";
        
        int score = 0;
        
        // Prefer Foundation and SR Legacy data (more accurate)
        if (dataType.contains("foundation")) {
            score += 100;
        } else if (dataType.contains("sr legacy")) {
            score += 80;
        } else if (dataType.contains("survey")) {
            score += 60;
        }
        
        // Prefer simpler descriptions (raw foods vs prepared dishes)
        if (!description.contains("with") && !description.contains("recipe") && 
            !description.contains("prepared") && !description.contains("cooked")) {
            score += 50;
        }
        
        // Exact word match bonus
        if (description.contains(searchTerm)) {
            score += 30;
        }
        
        // Shorter descriptions are often more basic foods
        if (description.length() < 50) {
            score += 20;
        }
        
        return score;
    }

    /**
     * Get detailed nutrition information for a specific food item
     */
    private Optional<UsdaFoodItem> getFoodNutrition(int fdcId, String description) {
        try {
            String nutritionUrl = UriComponentsBuilder
                .fromHttpUrl(USDA_API_BASE_URL + "/food/" + fdcId)
                .queryParam("api_key", usdaApiKey)
                .queryParam("format", "abridged")
                .queryParam("nutrients", "208,203,204,205,291,269") // Energy, Protein, Fat, Carbs, Fiber, Sugar
                .build()
                .toUriString();

            logger.debug("Getting nutrition data from: {}", nutritionUrl);
            
            // Get raw response first for debugging
            String rawResponse = restTemplate.getForObject(nutritionUrl, String.class);
            logger.debug("Raw USDA API response: {}", rawResponse);
            
            UsdaFoodDetail foodDetail = restTemplate.getForObject(nutritionUrl, UsdaFoodDetail.class);
            
            if (foodDetail != null) {
                // Log the parsed response structure
                logger.debug("Parsed USDA response - Description: {}, Nutrients count: {}", 
                           foodDetail.description, 
                           foodDetail.foodNutrients != null ? foodDetail.foodNutrients.size() : 0);
                
                double caloriesPerHundredGrams = extractCalories(foodDetail);
                
                UsdaFoodItem foodItem = new UsdaFoodItem();
                foodItem.fdcId = fdcId;
                foodItem.description = description;
                foodItem.caloriesPerHundredGrams = caloriesPerHundredGrams;
                
                logger.info("Retrieved nutrition data: {} - {} kcal/100g", description, caloriesPerHundredGrams);
                return Optional.of(foodItem);
            }
            
        } catch (Exception e) {
            logger.error("Error getting nutrition data for FDC ID {}: {}", fdcId, e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Extract calorie information from USDA food detail response
     */
    private double extractCalories(UsdaFoodDetail foodDetail) {
        if (foodDetail.foodNutrients != null) {
            logger.debug("    Examining {} nutrients for calorie data", foodDetail.foodNutrients.size());
            
            // Log all available nutrients for debugging
            for (UsdaFoodNutrient nutrient : foodDetail.foodNutrients) {
                if (nutrient.nutrient != null) {
                    logger.debug("    Available nutrient: Number={}, ID={}, Name='{}', Amount={}, Unit='{}'", 
                               nutrient.nutrient.number,
                               nutrient.nutrient.id, 
                               nutrient.nutrient.name,
                               nutrient.amount,
                               nutrient.nutrient.unitName);
                }
            }
            
            // Look for Energy (kcal) - nutrient number 208 (not id!)
            for (UsdaFoodNutrient nutrient : foodDetail.foodNutrients) {
                if (nutrient.nutrient != null && 
                    nutrient.nutrient.number != null &&
                    nutrient.nutrient.number.equals("208") &&
                    nutrient.amount != null) {
                    
                    String nutrientName = nutrient.nutrient.name != null ? nutrient.nutrient.name : "Energy";
                    String unit = nutrient.nutrient.unitName != null ? nutrient.nutrient.unitName : "kcal";
                    
                    logger.info("    ✓ Calories found: {} {} per 100g (nutrient: {})", 
                               nutrient.amount, unit, nutrientName);
                    return nutrient.amount;
                }
            }
            
            // Try alternative energy nutrient IDs or names
            for (UsdaFoodNutrient nutrient : foodDetail.foodNutrients) {
                if (nutrient.nutrient != null && nutrient.amount != null) {
                    String nutrientName = nutrient.nutrient.name != null ? nutrient.nutrient.name.toLowerCase() : "";
                    
                    // Check for energy by name
                    if (nutrientName.contains("energy") || nutrientName.contains("calorie")) {
                        logger.info("    ✓ Energy found by name: {} {} per 100g (nutrient: {}, ID: {})", 
                                   nutrient.amount, 
                                   nutrient.nutrient.unitName,
                                   nutrient.nutrient.name,
                                   nutrient.nutrient.id);
                        return nutrient.amount;
                    }
                }
            }
        }
        
        // If no calorie data found, return a reasonable estimate based on food type
        double estimate = estimateCaloriesFromDescription(foodDetail.description);
        logger.warn("    ⚠ No calorie data found, using estimate: {} kcal/100g", String.format("%.1f", estimate));
        return estimate;
    }

    /**
     * Estimate calories based on food description when exact data isn't available
     */
    private double estimateCaloriesFromDescription(String description) {
        String lowerDesc = description.toLowerCase();
        
        // Vegetables (low calorie)
        if (lowerDesc.contains("tomato") || lowerDesc.contains("cucumber") || 
            lowerDesc.contains("lettuce") || lowerDesc.contains("spinach") ||
            lowerDesc.contains("bell pepper") || lowerDesc.contains("broccoli")) {
            return 25.0;
        }
        
        // Fruits (medium-low calorie)
        if (lowerDesc.contains("apple") || lowerDesc.contains("orange") || 
            lowerDesc.contains("strawberry") || lowerDesc.contains("melon")) {
            return 50.0;
        }
        
        // Avocado (high calorie fruit)
        if (lowerDesc.contains("avocado")) {
            return 160.0;
        }
        
        // Beans and legumes (medium calorie)
        if (lowerDesc.contains("bean") || lowerDesc.contains("lentil") || 
            lowerDesc.contains("chickpea") || lowerDesc.contains("pea")) {
            return 120.0;
        }
        
        // Grains (medium-high calorie)
        if (lowerDesc.contains("rice") || lowerDesc.contains("quinoa") || 
            lowerDesc.contains("oats") || lowerDesc.contains("barley")) {
            return 130.0;
        }
        
        // Protein sources (medium-high calorie)
        if (lowerDesc.contains("chicken") || lowerDesc.contains("fish") || 
            lowerDesc.contains("tofu") || lowerDesc.contains("egg")) {
            return 150.0;
        }
        
        // Nuts and seeds (high calorie)
        if (lowerDesc.contains("nut") || lowerDesc.contains("seed") || 
            lowerDesc.contains("almond") || lowerDesc.contains("walnut")) {
            return 580.0;
        }
        
        // Default estimate
        return 100.0;
    }

    /**
     * Clean food name for better USDA API searching
     */
    private String cleanFoodName(String foodName) {
        String cleaned = foodName.toLowerCase().trim();
        
        // Remove common prefixes/suffixes that might confuse search
        cleaned = cleaned.replace("fresh ", "");
        cleaned = cleaned.replace("raw ", "");
        cleaned = cleaned.replace("organic ", "");
        cleaned = cleaned.replace(" leaf", "");
        cleaned = cleaned.replace(" leaves", "");
        
        // Handle specific mappings for better USDA search results
        if (cleaned.contains("cherry tomato")) {
            cleaned = "tomato cherry";
        } else if (cleaned.contains("black bean")) {
            cleaned = "beans black";
        } else if (cleaned.contains("white bean")) {
            cleaned = "beans white";
        } else if (cleaned.contains("kidney bean")) {
            cleaned = "beans kidney";
        }
        
        // Remove special characters and normalize whitespace
        cleaned = cleaned.replaceAll("[^a-zA-Z\\s]", "");
        cleaned = cleaned.replaceAll("\\s+", " ");
        
        return cleaned.trim();
    }

    // USDA API Response DTOs
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsdaSearchResponse {
        @JsonProperty("foods")
        public List<UsdaFood> foods;
        
        @JsonProperty("totalHits")
        public Integer totalHits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsdaFood {
        @JsonProperty("fdcId")
        public Integer fdcId;
        
        @JsonProperty("description")
        public String description;
        
        @JsonProperty("dataType")
        public String dataType;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsdaFoodDetail {
        @JsonProperty("fdcId")
        public Integer fdcId;
        
        @JsonProperty("description")
        public String description;
        
        @JsonProperty("foodNutrients")
        public List<UsdaFoodNutrient> foodNutrients;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsdaFoodNutrient {
        @JsonProperty("nutrient")
        public UsdaNutrient nutrient;
        
        @JsonProperty("amount")
        public Double amount;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsdaNutrient {
        @JsonProperty("id")
        public Integer id;
        
        @JsonProperty("number")
        public String number;
        
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("unitName")
        public String unitName;
    }

    // Result DTOs for internal use
    public static class UsdaFoodCandidate {
        public int fdcId;
        public String description;
        public String dataType;
    }
    
    public static class UsdaFoodItem {
        public int fdcId;
        public String description;
        public double caloriesPerHundredGrams;
    }
}