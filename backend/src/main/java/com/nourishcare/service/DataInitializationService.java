package com.nourishcare.service;

import com.nourishcare.model.FoodItem;
import com.nourishcare.model.Recipe;
import com.nourishcare.model.MealPlan;
import com.nourishcare.repository.FoodItemRepository;
import com.nourishcare.repository.RecipeRepository;
import com.nourishcare.repository.MealPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (foodItemRepository.count() == 0) {
            initializeFoodItems();
        }
        if (recipeRepository.count() == 0) {
            initializeRecipes();
        }
        if (mealPlanRepository.count() == 0) {
            initializeMealPlans();
        }
    }

    private void initializeFoodItems() {
        List<FoodItem> foodItems = Arrays.asList(
            new FoodItem("Organic Spinach", "1 bag", LocalDate.now().plusDays(4), "Vegetables"),
            new FoodItem("Greek Yogurt", "500g", LocalDate.now().plusDays(2), "Dairy"),
            new FoodItem("Chicken Breast", "1kg", LocalDate.now().plusDays(1), "Meat"),
            new FoodItem("Whole Grain Bread", "1 loaf", LocalDate.now().plusDays(7), "Grains"),
            new FoodItem("Fresh Salmon", "500g", LocalDate.now().plusDays(1), "Fish"),
            new FoodItem("Banana", "6 pieces", LocalDate.now().plusDays(3), "Fruits"),
            new FoodItem("Eggs", "12 pieces", LocalDate.now().plusDays(9), "Dairy"),
            new FoodItem("Bell Peppers", "3 pieces", LocalDate.now().plusDays(5), "Vegetables")
        );
        foodItemRepository.saveAll(foodItems);
    }

    private void initializeRecipes() {
        List<Recipe> recipes = Arrays.asList(
            new Recipe(
                "Mediterranean Quinoa Bowl",
                "A healthy and colorful bowl packed with quinoa, vegetables, and feta cheese",
                "/api/placeholder/400/300",
                "25 min",
                2,
                "Easy",
                Arrays.asList(
                    "1 cup quinoa",
                    "2 cups vegetable broth",
                    "1 cucumber, diced",
                    "1 cup cherry tomatoes, halved",
                    "1/2 red onion, sliced",
                    "1/2 cup feta cheese",
                    "1/4 cup olive oil",
                    "2 tbsp lemon juice",
                    "Fresh herbs (parsley, mint)"
                ),
                Arrays.asList(
                    "Cook quinoa in vegetable broth according to package instructions",
                    "Let quinoa cool to room temperature",
                    "Dice cucumber and halve cherry tomatoes",
                    "Slice red onion thinly",
                    "Mix olive oil and lemon juice for dressing",
                    "Combine all ingredients in a bowl",
                    "Top with feta cheese and fresh herbs",
                    "Serve chilled"
                ),
                420
            ),
            new Recipe(
                "Grilled Salmon with Asparagus",
                "Perfectly grilled salmon fillet served with fresh asparagus",
                "/api/placeholder/400/300",
                "20 min",
                2,
                "Medium",
                Arrays.asList(
                    "2 salmon fillets",
                    "1 lb asparagus",
                    "2 tbsp olive oil",
                    "1 lemon",
                    "Salt and pepper",
                    "2 cloves garlic",
                    "Fresh dill"
                ),
                Arrays.asList(
                    "Preheat grill to medium-high heat",
                    "Season salmon with salt, pepper, and lemon juice",
                    "Trim asparagus ends",
                    "Brush asparagus with olive oil",
                    "Grill salmon 4-5 minutes per side",
                    "Grill asparagus 3-4 minutes",
                    "Garnish with fresh dill and lemon wedges",
                    "Serve immediately"
                ),
                380
            ),
            new Recipe(
                "Vegetarian Stir-Fry",
                "Quick and healthy vegetable stir-fry with tofu",
                "/api/placeholder/400/300",
                "15 min",
                3,
                "Easy",
                Arrays.asList(
                    "200g firm tofu",
                    "2 bell peppers",
                    "1 broccoli head",
                    "1 carrot",
                    "2 tbsp soy sauce",
                    "1 tbsp sesame oil",
                    "2 cloves garlic",
                    "1 inch ginger",
                    "Green onions"
                ),
                Arrays.asList(
                    "Cut tofu into cubes",
                    "Slice all vegetables",
                    "Heat oil in wok or large pan",
                    "Stir-fry tofu until golden",
                    "Add harder vegetables first",
                    "Add softer vegetables",
                    "Mix in sauce and aromatics",
                    "Serve over rice"
                ),
                290
            ),
            new Recipe(
                "Avocado Toast Supreme",
                "Elevated avocado toast with poached egg and microgreens",
                "/api/placeholder/400/300",
                "10 min",
                1,
                "Easy",
                Arrays.asList(
                    "2 slices whole grain bread",
                    "1 ripe avocado",
                    "1 egg",
                    "Microgreens",
                    "Cherry tomatoes",
                    "Everything bagel seasoning",
                    "Lemon juice",
                    "Salt and pepper"
                ),
                Arrays.asList(
                    "Toast bread to desired crispness",
                    "Mash avocado with lemon juice",
                    "Poach egg in simmering water",
                    "Spread avocado on toast",
                    "Top with poached egg",
                    "Add microgreens and tomatoes",
                    "Season with everything bagel seasoning",
                    "Serve immediately"
                ),
                340
            )
        );
        recipeRepository.saveAll(recipes);
    }

    private void initializeMealPlans() {
        List<Recipe> recipes = recipeRepository.findAll();
        if (recipes.size() >= 4) {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            
            for (int i = 0; i < 7; i++) {
                LocalDate date = weekStart.plusDays(i);
                MealPlan mealPlan = new MealPlan(date);
                
                if (i % 2 == 0) {
                    mealPlan.setBreakfast(recipes.get(3)); // Avocado Toast
                }
                mealPlan.setLunch(i % 3 == 0 ? recipes.get(0) : recipes.get(2)); // Quinoa Bowl or Stir-Fry
                mealPlan.setDinner(recipes.get(i % recipes.size()));
                
                mealPlanRepository.save(mealPlan);
            }
        }
    }
}
