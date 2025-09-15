package com.nourishcare.recipeservice.repository;

import com.nourishcare.recipeservice.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    
    /**
     * Find recipes by title containing keyword (case insensitive)
     */
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find recipes by category
     */
    List<Recipe> findByCategoryIgnoreCase(String category);
    
    /**
     * Find recipes by difficulty level
     */
    List<Recipe> findByDifficultyIgnoreCase(String difficulty);
    
    /**
     * Find recipes by cuisine type
     */
    List<Recipe> findByCuisineIgnoreCase(String cuisine);
    
    /**
     * Find recipes with calories less than or equal to max
     */
    List<Recipe> findByCaloriesLessThanEqual(int maxCalories);
    
    /**
     * Find recipes by serving size
     */
    List<Recipe> findByServings(int servings);
    
    /**
     * Find recipes containing specific ingredient
     */
    @Query("{ 'ingredients': { $regex: ?0, $options: 'i' } }")
    List<Recipe> findByIngredientsContaining(String ingredient);
    
    /**
     * Find recipes by cook time range
     */
    @Query("{ 'cookTime': { $regex: ?0 } }")
    List<Recipe> findByCookTimeContaining(String timeRange);
    
    /**
     * Find recipes by tags
     */
    List<Recipe> findByTagsContainingIgnoreCase(String tag);
    
    /**
     * Find recipes created by specific user
     */
    List<Recipe> findByCreatedBy(String userId);
    
    /**
     * Find recipes created after specific date
     */
    List<Recipe> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    
    /**
     * Find top rated recipes
     */
    List<Recipe> findByRatingGreaterThanEqualOrderByRatingDesc(double minRating);
    
    /**
     * Find most liked recipes
     */
    List<Recipe> findByLikesGreaterThanOrderByLikesDesc(int minLikes);
    
    /**
     * Find recipes from Spoonacular
     */
    List<Recipe> findByFromSpoonacularTrue();
    
    /**
     * Find recipes by Spoonacular ID
     */
    Optional<Recipe> findBySpoonacularId(Long spoonacularId);
    
    /**
     * Search recipes with text search
     */
    @Query("{ $text: { $search: ?0 } }")
    List<Recipe> findByTextSearch(String searchTerm);
    
    /**
     * Count recipes by category
     */
    long countByCategoryIgnoreCase(String category);
    
    /**
     * Find recipes with pagination
     */
    Page<Recipe> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description, Pageable pageable);
}
