package com.nourishcare.recipeservice.service;

import com.nourishcare.recipeservice.model.Recipe;
import com.nourishcare.recipeservice.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private SpoonacularService spoonacularService;

    @Value("${spoonacular.api.mock:true}")
    private boolean useMockSpoonacular;

    /**
     * Get all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * Get recipe by ID
     */
    public Optional<Recipe> getRecipeById(String id) {
        return recipeRepository.findById(id);
    }

    /**
     * Create new recipe
     */
    public Recipe createRecipe(Recipe recipe) {
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    /**
     * Update existing recipe
     */
    public Recipe updateRecipe(String id, Recipe recipe) {
        Optional<Recipe> existingRecipe = recipeRepository.findById(id);
        if (existingRecipe.isPresent()) {
            Recipe updated = existingRecipe.get();
            updated.setTitle(recipe.getTitle());
            updated.setDescription(recipe.getDescription());
            updated.setImage(recipe.getImage());
            updated.setCookTime(recipe.getCookTime());
            updated.setServings(recipe.getServings());
            updated.setDifficulty(recipe.getDifficulty());
            updated.setIngredients(recipe.getIngredients());
            updated.setInstructions(recipe.getInstructions());
            updated.setCalories(recipe.getCalories());
            updated.setNutritionInfo(recipe.getNutritionInfo());
            updated.setCategory(recipe.getCategory());
            updated.setTags(recipe.getTags());
            updated.setCuisine(recipe.getCuisine());
            updated.setUpdatedAt(LocalDateTime.now());
            return recipeRepository.save(updated);
        }
        return null;
    }

    /**
     * Delete recipe
     */
    public void deleteRecipe(String id) {
        recipeRepository.deleteById(id);
    }

    /**
     * Search recipes by title
     */
    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search recipes with text search
     */
    public List<Recipe> searchRecipes(String searchTerm) {
        return recipeRepository.findByTextSearch(searchTerm);
    }

    /**
     * Get recipes by category
     */
    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategoryIgnoreCase(category);
    }

    /**
     * Get recipes by difficulty
     */
    public List<Recipe> getRecipesByDifficulty(String difficulty) {
        return recipeRepository.findByDifficultyIgnoreCase(difficulty);
    }

    /**
     * Get recipes by cuisine
     */
    public List<Recipe> getRecipesByCuisine(String cuisine) {
        return recipeRepository.findByCuisineIgnoreCase(cuisine);
    }

    /**
     * Get recipes by ingredient
     */
    public List<Recipe> getRecipesByIngredient(String ingredient) {
        return recipeRepository.findByIngredientsContaining(ingredient);
    }

    /**
     * Get recipes by max calories
     */
    public List<Recipe> getRecipesByMaxCalories(int maxCalories) {
        return recipeRepository.findByCaloriesLessThanEqual(maxCalories);
    }

    /**
     * Get recipes by servings
     */
    public List<Recipe> getRecipesByServings(int servings) {
        return recipeRepository.findByServings(servings);
    }

    /**
     * Get recipes by tag
     */
    public List<Recipe> getRecipesByTag(String tag) {
        return recipeRepository.findByTagsContainingIgnoreCase(tag);
    }

    /**
     * Get recipes created by user
     */
    public List<Recipe> getRecipesByUser(String userId) {
        return recipeRepository.findByCreatedBy(userId);
    }

    /**
     * Get recent recipes
     */
    public List<Recipe> getRecentRecipes(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return recipeRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since);
    }

    /**
     * Get top rated recipes
     */
    public List<Recipe> getTopRatedRecipes(double minRating, int limit) {
        return recipeRepository.findByRatingGreaterThanEqualOrderByRatingDesc(minRating)
            .stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Get most liked recipes
     */
    public List<Recipe> getMostLikedRecipes(int limit) {
        return recipeRepository.findByLikesGreaterThanOrderByLikesDesc(0)
            .stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Like a recipe
     */
    public Recipe likeRecipe(String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            Recipe r = recipe.get();
            r.setLikes(r.getLikes() + 1);
            return recipeRepository.save(r);
        }
        return null;
    }

    /**
     * Save/bookmark a recipe
     */
    public Recipe saveRecipe(String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            Recipe r = recipe.get();
            r.setSaves(r.getSaves() + 1);
            return recipeRepository.save(r);
        }
        return null;
    }

    /**
     * Rate a recipe
     */
    public Recipe rateRecipe(String id, double rating) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            Recipe r = recipe.get();
            // Simple average rating calculation
            double currentRating = r.getRating();
            int reviewCount = r.getReviewCount();
            double newRating = ((currentRating * reviewCount) + rating) / (reviewCount + 1);
            r.setRating(Math.round(newRating * 10.0) / 10.0); // Round to 1 decimal place
            r.setReviewCount(reviewCount + 1);
            return recipeRepository.save(r);
        }
        return null;
    }

    /**
     * Get recipes from Spoonacular API
     */
    public List<Recipe> getRecipesFromSpoonacular(String query, int number) {
        if (useMockSpoonacular) {
            return getMockSpoonacularRecipes();
        }
        return spoonacularService.searchRecipes(query, number);
    }

    /**
     * Import recipe from Spoonacular by ID
     */
    public Recipe importRecipeFromSpoonacular(Long spoonacularId) {
        if (useMockSpoonacular) {
            return createMockSpoonacularRecipe(spoonacularId);
        }
        return spoonacularService.getRecipeById(spoonacularId);
    }

    /**
     * Get recipe recommendations based on ingredients
     */
    public List<Recipe> getRecipeRecommendations(List<String> ingredients) {
        // Find recipes that contain any of the given ingredients
        Set<Recipe> recommendations = new HashSet<>();
        for (String ingredient : ingredients) {
            recommendations.addAll(getRecipesByIngredient(ingredient));
        }
        return new ArrayList<>(recommendations);
    }

    /**
     * Get recipe statistics
     */
    public Map<String, Object> getRecipeStats() {
        List<Recipe> allRecipes = recipeRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRecipes", allRecipes.size());
        stats.put("userCreatedRecipes", allRecipes.stream().filter(r -> !r.isFromSpoonacular()).count());
        stats.put("spoonacularRecipes", allRecipes.stream().filter(Recipe::isFromSpoonacular).count());
        
        // Category distribution
        Map<String, Long> categoryStats = allRecipes.stream()
            .filter(r -> r.getCategory() != null)
            .collect(Collectors.groupingBy(Recipe::getCategory, Collectors.counting()));
        stats.put("categoryDistribution", categoryStats);
        
        // Difficulty distribution
        Map<String, Long> difficultyStats = allRecipes.stream()
            .filter(r -> r.getDifficulty() != null)
            .collect(Collectors.groupingBy(Recipe::getDifficulty, Collectors.counting()));
        stats.put("difficultyDistribution", difficultyStats);
        
        // Average calories
        double avgCalories = allRecipes.stream()
            .filter(r -> r.getCalories() > 0)
            .mapToInt(Recipe::getCalories)
            .average()
            .orElse(0.0);
        stats.put("averageCalories", Math.round(avgCalories));
        
        return stats;
    }

    /**
     * Mock Spoonacular recipes for testing
     */
    private List<Recipe> getMockSpoonacularRecipes() {
        List<Recipe> mockRecipes = new ArrayList<>();
        
        Recipe recipe1 = new Recipe();
        recipe1.setTitle("Spicy Chicken Tacos");
        recipe1.setDescription("Delicious spicy chicken tacos with fresh vegetables");
        recipe1.setCookTime("25 min");
        recipe1.setServings(4);
        recipe1.setDifficulty("Medium");
        recipe1.setCalories(380);
        recipe1.setCategory("Main Course");
        recipe1.setCuisine("Mexican");
        recipe1.setIngredients(Arrays.asList("Chicken breast", "Taco shells", "Bell peppers", "Onions", "Spices"));
        recipe1.setInstructions(Arrays.asList("Cook chicken", "Prepare vegetables", "Assemble tacos"));
        recipe1.setFromSpoonacular(true);
        recipe1.setSpoonacularId(12345L);
        
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("Mediterranean Quinoa Salad");
        recipe2.setDescription("Fresh and healthy quinoa salad with Mediterranean flavors");
        recipe2.setCookTime("15 min");
        recipe2.setServings(6);
        recipe2.setDifficulty("Easy");
        recipe2.setCalories(290);
        recipe2.setCategory("Salad");
        recipe2.setCuisine("Mediterranean");
        recipe2.setIngredients(Arrays.asList("Quinoa", "Cucumber", "Tomatoes", "Feta cheese", "Olive oil"));
        recipe2.setInstructions(Arrays.asList("Cook quinoa", "Chop vegetables", "Mix ingredients"));
        recipe2.setFromSpoonacular(true);
        recipe2.setSpoonacularId(12346L);
        
        mockRecipes.add(recipe1);
        mockRecipes.add(recipe2);
        
        return mockRecipes;
    }

    /**
     * Create mock Spoonacular recipe for testing
     */
    private Recipe createMockSpoonacularRecipe(Long spoonacularId) {
        Recipe recipe = new Recipe();
        recipe.setTitle("Mock Spoonacular Recipe " + spoonacularId);
        recipe.setDescription("This is a mock recipe from Spoonacular API");
        recipe.setCookTime("30 min");
        recipe.setServings(4);
        recipe.setDifficulty("Medium");
        recipe.setCalories(350);
        recipe.setCategory("Main Course");
        recipe.setIngredients(Arrays.asList("Mock ingredient 1", "Mock ingredient 2", "Mock ingredient 3"));
        recipe.setInstructions(Arrays.asList("Mock instruction 1", "Mock instruction 2", "Mock instruction 3"));
        recipe.setFromSpoonacular(true);
        recipe.setSpoonacularId(spoonacularId);
        
        return recipeRepository.save(recipe);
    }
}
