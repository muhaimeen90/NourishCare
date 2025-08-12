const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api';

// Generic API helper function
async function apiRequest(endpoint: string, options: RequestInit = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const config: RequestInit = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    // Handle empty responses (like DELETE operations)
    if (response.status === 204) {
      return null;
    }
    
    return await response.json();
  } catch (error) {
    console.error(`API request failed for ${endpoint}:`, error);
    throw error;
  }
}

// Recipe API functions
export const recipeApi = {
  getAll: () => apiRequest('/recipes'),
  getById: (id: string) => apiRequest(`/recipes/${id}`),
  create: (recipe: any) => apiRequest('/recipes', {
    method: 'POST',
    body: JSON.stringify(recipe),
  }),
  update: (id: string, recipe: any) => apiRequest(`/recipes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(recipe),
  }),
  delete: (id: string) => apiRequest(`/recipes/${id}`, {
    method: 'DELETE',
  }),
  searchByTitle: (title: string) => apiRequest(`/recipes/search?title=${encodeURIComponent(title)}`),
  getByDifficulty: (difficulty: string) => apiRequest(`/recipes/difficulty/${difficulty}`),
  getByIngredient: (ingredient: string) => apiRequest(`/recipes/ingredient/${ingredient}`),
  getByCalories: (maxCalories: number) => apiRequest(`/recipes/calories/${maxCalories}`),
  getByServings: (servings: number) => apiRequest(`/recipes/servings/${servings}`),
};

// Food Item (Inventory) API functions
export const inventoryApi = {
  getAll: () => apiRequest('/inventory'),
  getById: (id: string) => apiRequest(`/inventory/${id}`),
  create: (item: any) => apiRequest('/inventory', {
    method: 'POST',
    body: JSON.stringify(item),
  }),
  update: (id: string, item: any) => apiRequest(`/inventory/${id}`, {
    method: 'PUT',
    body: JSON.stringify(item),
  }),
  delete: (id: string) => apiRequest(`/inventory/${id}`, {
    method: 'DELETE',
  }),
  getByCategory: (category: string) => apiRequest(`/inventory/category/${category}`),
  getExpiringSoon: (days: number = 3) => apiRequest(`/inventory/expiring-soon?days=${days}`),
  getExpired: () => apiRequest('/inventory/expired'),
  search: (name: string) => apiRequest(`/inventory/search?name=${encodeURIComponent(name)}`),
};

// Meal Plan API functions
export const mealPlanApi = {
  getAll: () => apiRequest('/meal-plans'),
  getById: (id: string) => apiRequest(`/meal-plans/${id}`),
  getByDate: (date: string) => apiRequest(`/meal-plans/date/${date}`),
  create: (mealPlan: any) => apiRequest('/meal-plans', {
    method: 'POST',
    body: JSON.stringify(mealPlan),
  }),
  update: (id: string, mealPlan: any) => apiRequest(`/meal-plans/${id}`, {
    method: 'PUT',
    body: JSON.stringify(mealPlan),
  }),
  delete: (id: string) => apiRequest(`/meal-plans/${id}`, {
    method: 'DELETE',
  }),
  getWeek: (startDate: string) => apiRequest(`/meal-plans/week?startDate=${startDate}`),
  getFuture: () => apiRequest('/meal-plans/future'),
};

// Export all APIs
export const api = {
  recipes: recipeApi,
  inventory: inventoryApi,
  mealPlans: mealPlanApi,
};

export default api;
