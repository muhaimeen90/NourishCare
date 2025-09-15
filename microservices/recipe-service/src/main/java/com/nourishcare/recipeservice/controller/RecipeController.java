package com.nourishcare.recipeservice.controller;

import com.nourishcare.recipeservice.model.Recipe;
import com.nourishcare.recipeservice.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "recipe-service");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all recipes
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipe by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        if (recipe.isPresent()) {
            return ResponseEntity.ok(recipe.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new recipe
     */
    @PostMapping
    public ResponseEntity<?> createRecipe(@Valid @RequestBody Recipe recipe) {
        try {
            Recipe savedRecipe = recipeService.createRecipe(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Update recipe
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable String id, @Valid @RequestBody Recipe recipe) {
        try {
            Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);
            if (updatedRecipe != null) {
                return ResponseEntity.ok(updatedRecipe);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Recipe not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Delete recipe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable String id) {
        try {
            Optional<Recipe> recipe = recipeService.getRecipeById(id);
            if (recipe.isPresent()) {
                recipeService.deleteRecipe(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Recipe deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Recipe not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Search recipes by title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String title) {
        List<Recipe> recipes = recipeService.searchRecipesByTitle(title);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(@PathVariable String category) {
        List<Recipe> recipes = recipeService.getRecipesByCategory(category);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by difficulty
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Recipe>> getRecipesByDifficulty(@PathVariable String difficulty) {
        List<Recipe> recipes = recipeService.getRecipesByDifficulty(difficulty);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by cuisine
     */
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Recipe>> getRecipesByCuisine(@PathVariable String cuisine) {
        List<Recipe> recipes = recipeService.getRecipesByCuisine(cuisine);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by ingredient
     */
    @GetMapping("/ingredient/{ingredient}")
    public ResponseEntity<List<Recipe>> getRecipesByIngredient(@PathVariable String ingredient) {
        List<Recipe> recipes = recipeService.getRecipesByIngredient(ingredient);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by max calories
     */
    @GetMapping("/calories/{maxCalories}")
    public ResponseEntity<List<Recipe>> getRecipesByCalories(@PathVariable int maxCalories) {
        List<Recipe> recipes = recipeService.getRecipesByMaxCalories(maxCalories);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by servings
     */
    @GetMapping("/servings/{servings}")
    public ResponseEntity<List<Recipe>> getRecipesByServings(@PathVariable int servings) {
        List<Recipe> recipes = recipeService.getRecipesByServings(servings);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recipes by tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Recipe>> getRecipesByTag(@PathVariable String tag) {
        List<Recipe> recipes = recipeService.getRecipesByTag(tag);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get recent recipes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Recipe>> getRecentRecipes(@RequestParam(defaultValue = "7") int days) {
        List<Recipe> recipes = recipeService.getRecentRecipes(days);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get top rated recipes
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Recipe>> getTopRatedRecipes(
            @RequestParam(defaultValue = "4.0") double minRating,
            @RequestParam(defaultValue = "10") int limit) {
        List<Recipe> recipes = recipeService.getTopRatedRecipes(minRating, limit);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Get most liked recipes
     */
    @GetMapping("/most-liked")
    public ResponseEntity<List<Recipe>> getMostLikedRecipes(@RequestParam(defaultValue = "10") int limit) {
        List<Recipe> recipes = recipeService.getMostLikedRecipes(limit);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Like a recipe
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeRecipe(@PathVariable String id) {
        Recipe recipe = recipeService.likeRecipe(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Save/bookmark a recipe
     */
    @PostMapping("/{id}/save")
    public ResponseEntity<?> saveRecipe(@PathVariable String id) {
        Recipe recipe = recipeService.saveRecipe(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Rate a recipe
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateRecipe(@PathVariable String id, @RequestParam double rating) {
        if (rating < 1.0 || rating > 5.0) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rating must be between 1.0 and 5.0");
            return ResponseEntity.badRequest().body(error);
        }
        
        Recipe recipe = recipeService.rateRecipe(id, rating);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get recipes from Spoonacular
     */
    @GetMapping("/spoonacular/search")
    public ResponseEntity<List<Recipe>> searchSpoonacularRecipes(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int number) {
        List<Recipe> recipes = recipeService.getRecipesFromSpoonacular(query, number);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Import recipe from Spoonacular
     */
    @PostMapping("/spoonacular/import/{spoonacularId}")
    public ResponseEntity<?> importSpoonacularRecipe(@PathVariable Long spoonacularId) {
        try {
            Recipe recipe = recipeService.importRecipeFromSpoonacular(spoonacularId);
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to import recipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get recipe recommendations based on ingredients
     */
    @PostMapping("/recommendations")
    public ResponseEntity<List<Recipe>> getRecommendations(@RequestBody List<String> ingredients) {
        List<Recipe> recommendations = recipeService.getRecipeRecommendations(ingredients);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get recipe statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRecipeStats() {
        Map<String, Object> stats = recipeService.getRecipeStats();
        return ResponseEntity.ok(stats);
    }
}
