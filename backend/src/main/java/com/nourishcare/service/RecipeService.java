package com.nourishcare.service;

import com.nourishcare.model.Recipe;
import com.nourishcare.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(String id) {
        return recipeRepository.findById(id);
    }

    public Recipe saveRecipe(Recipe recipe) {
        recipe.setUpdatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(String id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> getRecipesByDifficulty(String difficulty) {
        return recipeRepository.findByDifficulty(difficulty);
    }

    public List<Recipe> searchRecipesByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Recipe> getRecipesByIngredient(String ingredient) {
        return recipeRepository.findByIngredientsContainingIgnoreCase(ingredient);
    }

    public List<Recipe> getRecipesByCalories(int maxCalories) {
        return recipeRepository.findByCaloriesLessThanEqual(maxCalories);
    }

    public List<Recipe> getRecipesByServings(int servings) {
        return recipeRepository.findByServings(servings);
    }

    public Recipe updateRecipe(String id, Recipe updatedRecipe) {
        Optional<Recipe> existingRecipe = recipeRepository.findById(id);
        if (existingRecipe.isPresent()) {
            Recipe recipe = existingRecipe.get();
            recipe.setTitle(updatedRecipe.getTitle());
            recipe.setDescription(updatedRecipe.getDescription());
            recipe.setImage(updatedRecipe.getImage());
            recipe.setCookTime(updatedRecipe.getCookTime());
            recipe.setServings(updatedRecipe.getServings());
            recipe.setDifficulty(updatedRecipe.getDifficulty());
            recipe.setIngredients(updatedRecipe.getIngredients());
            recipe.setInstructions(updatedRecipe.getInstructions());
            recipe.setCalories(updatedRecipe.getCalories());
            recipe.setUpdatedAt(LocalDateTime.now());
            return recipeRepository.save(recipe);
        }
        return null;
    }
}
