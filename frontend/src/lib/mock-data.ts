export interface FoodItem {
  id: string;
  name: string;
  quantity: string;
  expirationDate: string;
  category: string;
  daysUntilExpiration: number;
}

export interface Recipe {
  id: string;
  title: string;
  description: string;
  image: string;
  cookTime: string;
  servings: number;
  difficulty: string;
  ingredients: string[];
  instructions: string[];
  calories: number;
}

export interface MealPlan {
  id: string;
  date: string;
  breakfast?: Recipe;
  lunch?: Recipe;
  dinner?: Recipe;
}

export interface NutritionData {
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  fiber: number;
  sugar: number;
}

// Mock food items with static expiration days for consistent hydration
export const mockFoodItems: FoodItem[] = [
  {
    id: '1',
    name: 'Organic Spinach',
    quantity: '1 bag',
    expirationDate: '2025-08-15',
    category: 'Vegetables',
    daysUntilExpiration: 4,
  },
  {
    id: '2',
    name: 'Greek Yogurt',
    quantity: '500g',
    expirationDate: '2025-08-13',
    category: 'Dairy',
    daysUntilExpiration: 2,
  },
  {
    id: '3',
    name: 'Chicken Breast',
    quantity: '1kg',
    expirationDate: '2025-08-12',
    category: 'Meat',
    daysUntilExpiration: 1,
  },
  {
    id: '4',
    name: 'Whole Grain Bread',
    quantity: '1 loaf',
    expirationDate: '2025-08-18',
    category: 'Grains',
    daysUntilExpiration: 7,
  },
  {
    id: '5',
    name: 'Fresh Salmon',
    quantity: '500g',
    expirationDate: '2025-08-12',
    category: 'Fish',
    daysUntilExpiration: 1,
  },
  {
    id: '6',
    name: 'Banana',
    quantity: '6 pieces',
    expirationDate: '2025-08-14',
    category: 'Fruits',
    daysUntilExpiration: 3,
  },
  {
    id: '7',
    name: 'Eggs',
    quantity: '12 pieces',
    expirationDate: '2025-08-20',
    category: 'Dairy',
    daysUntilExpiration: 9,
  },
  {
    id: '8',
    name: 'Bell Peppers',
    quantity: '3 pieces',
    expirationDate: '2025-08-16',
    category: 'Vegetables',
    daysUntilExpiration: 5,
  },
];

export const mockRecipes: Recipe[] = [
  {
    id: '1',
    title: 'Mediterranean Quinoa Bowl',
    description: 'A healthy and colorful bowl packed with quinoa, vegetables, and feta cheese',
    image: '/api/placeholder/400/300',
    cookTime: '25 min',
    servings: 2,
    difficulty: 'Easy',
    ingredients: [
      '1 cup quinoa',
      '2 cups vegetable broth',
      '1 cucumber, diced',
      '1 cup cherry tomatoes, halved',
      '1/2 red onion, sliced',
      '1/2 cup feta cheese',
      '1/4 cup olive oil',
      '2 tbsp lemon juice',
      'Fresh herbs (parsley, mint)',
    ],
    instructions: [
      'Cook quinoa in vegetable broth according to package instructions',
      'Let quinoa cool to room temperature',
      'Dice cucumber and halve cherry tomatoes',
      'Slice red onion thinly',
      'Mix olive oil and lemon juice for dressing',
      'Combine all ingredients in a bowl',
      'Top with feta cheese and fresh herbs',
      'Serve chilled',
    ],
    calories: 420,
  },
  {
    id: '2',
    title: 'Grilled Salmon with Asparagus',
    description: 'Perfectly grilled salmon fillet served with fresh asparagus',
    image: '/api/placeholder/400/300',
    cookTime: '20 min',
    servings: 2,
    difficulty: 'Medium',
    ingredients: [
      '2 salmon fillets',
      '1 lb asparagus',
      '2 tbsp olive oil',
      '1 lemon',
      'Salt and pepper',
      '2 cloves garlic',
      'Fresh dill',
    ],
    instructions: [
      'Preheat grill to medium-high heat',
      'Season salmon with salt, pepper, and lemon juice',
      'Trim asparagus ends',
      'Brush asparagus with olive oil',
      'Grill salmon 4-5 minutes per side',
      'Grill asparagus 3-4 minutes',
      'Garnish with fresh dill and lemon wedges',
      'Serve immediately',
    ],
    calories: 380,
  },
  {
    id: '3',
    title: 'Vegetarian Stir-Fry',
    description: 'Quick and healthy vegetable stir-fry with tofu',
    image: '/api/placeholder/400/300',
    cookTime: '15 min',
    servings: 3,
    difficulty: 'Easy',
    ingredients: [
      '200g firm tofu',
      '2 bell peppers',
      '1 broccoli head',
      '1 carrot',
      '2 tbsp soy sauce',
      '1 tbsp sesame oil',
      '2 cloves garlic',
      '1 inch ginger',
      'Green onions',
    ],
    instructions: [
      'Cut tofu into cubes',
      'Slice all vegetables',
      'Heat oil in wok or large pan',
      'Stir-fry tofu until golden',
      'Add harder vegetables first',
      'Add softer vegetables',
      'Mix in sauce and aromatics',
      'Serve over rice',
    ],
    calories: 290,
  },
  {
    id: '4',
    title: 'Avocado Toast Supreme',
    description: 'Elevated avocado toast with poached egg and microgreens',
    image: '/api/placeholder/400/300',
    cookTime: '10 min',
    servings: 1,
    difficulty: 'Easy',
    ingredients: [
      '2 slices whole grain bread',
      '1 ripe avocado',
      '1 egg',
      'Microgreens',
      'Cherry tomatoes',
      'Everything bagel seasoning',
      'Lemon juice',
      'Salt and pepper',
    ],
    instructions: [
      'Toast bread to desired crispness',
      'Mash avocado with lemon juice',
      'Poach egg in simmering water',
      'Spread avocado on toast',
      'Top with poached egg',
      'Add microgreens and tomatoes',
      'Season with everything bagel seasoning',
      'Serve immediately',
    ],
    calories: 340,
  },
];

// Generate meal plan for current week
const generateMealPlan = (): MealPlan[] => {
  const today = new Date();
  const weekStart = new Date(today.setDate(today.getDate() - today.getDay()));
  
  return Array.from({ length: 7 }, (_, i) => {
    const date = new Date(weekStart);
    date.setDate(weekStart.getDate() + i);
    
    return {
      id: `meal-${i}`,
      date: date.toISOString().split('T')[0],
      breakfast: i % 2 === 0 ? mockRecipes[3] : undefined,
      lunch: i % 3 === 0 ? mockRecipes[0] : mockRecipes[2],
      dinner: mockRecipes[i % mockRecipes.length],
    };
  });
};

export const mockMealPlan = generateMealPlan();

export const mockNutritionData: NutritionData = {
  calories: 487,
  protein: 28,
  carbs: 45,
  fat: 22,
  fiber: 8,
  sugar: 12,
};

export const mockAnalyticsData = {
  totalItems: mockFoodItems.length,
  expiringSoon: mockFoodItems.filter(item => item.daysUntilExpiration <= 2).length,
  wasteReduced: 85,
  moneySaved: 127,
  co2Saved: 23,
  wasteByCategory: [
    { category: 'Fruits', saved: 12, wasted: 3 },
    { category: 'Vegetables', saved: 18, wasted: 2 },
    { category: 'Dairy', saved: 8, wasted: 1 },
    { category: 'Meat', saved: 6, wasted: 1 },
    { category: 'Grains', saved: 10, wasted: 0 },
  ],
};
