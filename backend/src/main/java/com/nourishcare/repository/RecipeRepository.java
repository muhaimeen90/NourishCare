package com.nourishcare.repository;

import com.nourishcare.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    List<Recipe> findByDifficulty(String difficulty);
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    List<Recipe> findByIngredientsContainingIgnoreCase(String ingredient);
    List<Recipe> findByCaloriesLessThanEqual(int calories);
    List<Recipe> findByServings(int servings);
}
