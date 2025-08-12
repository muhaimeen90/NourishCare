package com.nourishcare.controller;

import com.nourishcare.model.Recipe;
import com.nourishcare.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "http://localhost:3000")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        return recipe.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe recipe) {
        Recipe updated = recipeService.updateRecipe(id, recipe);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<Recipe> getRecipesByDifficulty(@PathVariable String difficulty) {
        return recipeService.getRecipesByDifficulty(difficulty);
    }

    @GetMapping("/search")
    public List<Recipe> searchRecipesByTitle(@RequestParam String title) {
        return recipeService.searchRecipesByTitle(title);
    }

    @GetMapping("/ingredient/{ingredient}")
    public List<Recipe> getRecipesByIngredient(@PathVariable String ingredient) {
        return recipeService.getRecipesByIngredient(ingredient);
    }

    @GetMapping("/calories/{maxCalories}")
    public List<Recipe> getRecipesByCalories(@PathVariable int maxCalories) {
        return recipeService.getRecipesByCalories(maxCalories);
    }

    @GetMapping("/servings/{servings}")
    public List<Recipe> getRecipesByServings(@PathVariable int servings) {
        return recipeService.getRecipesByServings(servings);
    }
}
