package com.nourishcare.recipeservice.service;

import com.nourishcare.recipeservice.model.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpoonacularService {

    @Value("${spoonacular.api.url}")
    private String spoonacularApiUrl;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    @Value("${spoonacular.api.mock:true}")
    private boolean useMockService;

    private final WebClient webClient;

    public SpoonacularService() {
        this.webClient = WebClient.builder().build();
    }

    /**
     * Search recipes from Spoonacular API
     */
    public List<Recipe> searchRecipes(String query, int number) {
        if (useMockService) {
            return getMockSearchResults(query);
        }

        // Real Spoonacular API call would be implemented here
        // For now, returning mock data
        return getMockSearchResults(query);
    }

    /**
     * Get recipe details by ID from Spoonacular API
     */
    public Recipe getRecipeById(Long spoonacularId) {
        if (useMockService) {
            return createMockRecipe(spoonacularId);
        }

        // Real Spoonacular API call would be implemented here
        // For now, returning mock data
        return createMockRecipe(spoonacularId);
    }

    /**
     * Get random recipes from Spoonacular API
     */
    public List<Recipe> getRandomRecipes(int number) {
        if (useMockService) {
            return getMockRandomRecipes(number);
        }

        // Real Spoonacular API call would be implemented here
        return getMockRandomRecipes(number);
    }

    /**
     * Search recipes by ingredients
     */
    public List<Recipe> searchRecipesByIngredients(List<String> ingredients, int number) {
        if (useMockService) {
            return getMockRecipesByIngredients(ingredients);
        }

        // Real Spoonacular API call would be implemented here
        return getMockRecipesByIngredients(ingredients);
    }

    /**
     * Mock search results for testing
     */
    private List<Recipe> getMockSearchResults(String query) {
        List<Recipe> mockRecipes = new ArrayList<>();

        Recipe recipe1 = createMockRecipe(123L);
        recipe1.setTitle("Mock " + query + " Recipe 1");
        recipe1.setDescription("A delicious " + query + " recipe from our mock API");

        Recipe recipe2 = createMockRecipe(124L);
        recipe2.setTitle("Mock " + query + " Recipe 2");
        recipe2.setDescription("Another amazing " + query + " recipe");

        mockRecipes.add(recipe1);
        mockRecipes.add(recipe2);

        return mockRecipes;
    }

    /**
     * Create mock recipe with given ID
     */
    private Recipe createMockRecipe(Long spoonacularId) {
        Recipe recipe = new Recipe();
        recipe.setSpoonacularId(spoonacularId);
        recipe.setFromSpoonacular(true);
        recipe.setTitle("Mock Spoonacular Recipe " + spoonacularId);
        recipe.setDescription("This is a mock recipe from Spoonacular API for testing purposes");
        recipe.setImage("https://via.placeholder.com/400x300");
        recipe.setCookTime("30 min");
        recipe.setServings(4);
        recipe.setDifficulty("Medium");
        recipe.setCalories(350 + (int)(spoonacularId % 200)); // Vary calories
        recipe.setCategory("Main Course");
        recipe.setCuisine("International");
        
        recipe.setIngredients(Arrays.asList(
            "2 cups mock ingredient A",
            "1 lb mock ingredient B",
            "1 tsp mock spice C",
            "Salt and pepper to taste"
        ));
        
        recipe.setInstructions(Arrays.asList(
            "Prepare all ingredients",
            "Cook ingredient B in a large pan",
            "Add ingredient A and spice C",
            "Season with salt and pepper",
            "Cook for 15-20 minutes until done"
        ));

        recipe.setTags(Arrays.asList("spoonacular", "mock", "test"));
        
        // Mock nutrition info
        Recipe.NutritionInfo nutrition = new Recipe.NutritionInfo();
        nutrition.setProtein(25.5);
        nutrition.setCarbs(45.0);
        nutrition.setFat(12.0);
        nutrition.setFiber(8.0);
        nutrition.setSugar(6.0);
        nutrition.setSodium(800.0);
        recipe.setNutritionInfo(nutrition);

        return recipe;
    }

    /**
     * Get mock random recipes
     */
    private List<Recipe> getMockRandomRecipes(int number) {
        List<Recipe> mockRecipes = new ArrayList<>();
        
        String[] randomTitles = {
            "Classic Spaghetti Carbonara",
            "Grilled Salmon with Herbs",
            "Vegetarian Buddha Bowl",
            "Chocolate Chip Cookies",
            "Thai Green Curry",
            "BBQ Pulled Pork Sandwich",
            "Greek Salad with Feta",
            "Beef Stir Fry"
        };
        
        String[] categories = {"Main Course", "Appetizer", "Dessert", "Salad", "Soup"};
        String[] cuisines = {"Italian", "Asian", "American", "Mediterranean", "Mexican"};
        String[] difficulties = {"Easy", "Medium", "Hard"};
        
        for (int i = 0; i < Math.min(number, randomTitles.length); i++) {
            Recipe recipe = createMockRecipe((long) (200 + i));
            recipe.setTitle(randomTitles[i]);
            recipe.setCategory(categories[i % categories.length]);
            recipe.setCuisine(cuisines[i % cuisines.length]);
            recipe.setDifficulty(difficulties[i % difficulties.length]);
            mockRecipes.add(recipe);
        }
        
        return mockRecipes;
    }

    /**
     * Get mock recipes by ingredients
     */
    private List<Recipe> getMockRecipesByIngredients(List<String> ingredients) {
        List<Recipe> mockRecipes = new ArrayList<>();
        
        for (int i = 0; i < Math.min(3, ingredients.size()); i++) {
            String ingredient = ingredients.get(i);
            Recipe recipe = createMockRecipe((long) (300 + i));
            recipe.setTitle("Recipe with " + ingredient);
            recipe.setDescription("A delicious recipe featuring " + ingredient + " as the main ingredient");
            
            // Add the searched ingredient to the recipe
            List<String> recipeIngredients = new ArrayList<>(recipe.getIngredients());
            recipeIngredients.add(0, "2 cups " + ingredient);
            recipe.setIngredients(recipeIngredients);
            
            mockRecipes.add(recipe);
        }
        
        return mockRecipes;
    }
}
