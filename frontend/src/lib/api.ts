const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

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
  getAll: () => apiRequest('/api/recipes'),
  getById: (id: string) => apiRequest(`/api/recipes/${id}`),
  create: (recipe: any) => apiRequest('/api/recipes', {
    method: 'POST',
    body: JSON.stringify(recipe),
  }),
  update: (id: string, recipe: any) => apiRequest(`/api/recipes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(recipe),
  }),
  delete: (id: string) => apiRequest(`/api/recipes/${id}`, {
    method: 'DELETE',
  }),
  searchByTitle: (title: string) => apiRequest(`/api/recipes/search?title=${encodeURIComponent(title)}`),
  getByDifficulty: (difficulty: string) => apiRequest(`/api/recipes/difficulty/${difficulty}`),
  getByIngredient: (ingredient: string) => apiRequest(`/api/recipes/ingredient/${ingredient}`),
  getByCalories: (maxCalories: number) => apiRequest(`/api/recipes/calories/${maxCalories}`),
  getByServings: (servings: number) => apiRequest(`/api/recipes/servings/${servings}`),
};

// Food Item (Inventory) API functions
export const inventoryApi = {
  getAll: () => apiRequest('/api/inventory'),
  getById: (id: string) => apiRequest(`/api/inventory/${id}`),
  create: (item: any) => apiRequest('/api/inventory', {
    method: 'POST',
    body: JSON.stringify(item),
  }),
  update: (id: string, item: any) => apiRequest(`/api/inventory/${id}`, {
    method: 'PUT',
    body: JSON.stringify(item),
  }),
  delete: (id: string) => apiRequest(`/api/inventory/${id}`, {
    method: 'DELETE',
  }),
  getByCategory: (category: string) => apiRequest(`/api/inventory/category/${category}`),
  getExpiringSoon: (days: number = 3) => apiRequest(`/api/inventory/expiring-soon?days=${days}`),
  getExpired: () => apiRequest('/api/inventory/expired'),
  search: (name: string) => apiRequest(`/api/inventory/search?name=${encodeURIComponent(name)}`),
};

// Meal Plan API functions
export const mealPlanApi = {
  getAll: () => apiRequest('/api/meal-plans'),
  getById: (id: string) => apiRequest(`/api/meal-plans/${id}`),
  getByDate: (date: string) => apiRequest(`/api/meal-plans/date/${date}`),
  create: (mealPlan: any) => apiRequest('/api/meal-plans', {
    method: 'POST',
    body: JSON.stringify(mealPlan),
  }),
  update: (id: string, mealPlan: any) => apiRequest(`/api/meal-plans/${id}`, {
    method: 'PUT',
    body: JSON.stringify(mealPlan),
  }),
  delete: (id: string) => apiRequest(`/api/meal-plans/${id}`, {
    method: 'DELETE',
  }),
  getWeek: (startDate: string) => apiRequest(`/api/meal-plans/week?startDate=${startDate}`),
  getFuture: () => apiRequest('/api/meal-plans/future'),
};

// Vision API functions
export const visionApi = {
  detectFoodItems: (imageFile: File) => {
    const formData = new FormData();
    formData.append('image', imageFile);
    
    return fetch(`${API_BASE_URL}/api/vision/detect-food-items`, {
      method: 'POST',
      body: formData,
    }).then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    });
  },
  getAllDetections: () => apiRequest('/api/vision/detections'),
  getDetectionById: (id: string) => apiRequest(`/api/vision/detections/${id}`),
  searchDetections: (foodName: string) => apiRequest(`/api/vision/detections/search?foodName=${encodeURIComponent(foodName)}`),
  getRecentDetections: (days: number = 7) => apiRequest(`/api/vision/detections/recent?days=${days}`),
  getStats: () => apiRequest('/api/vision/stats'),
};

// Vision API functions
export const visionApi = {
  detectFood: (imageFile: File) => {
    const formData = new FormData();
    formData.append('image', imageFile);
    
    return fetch(`${API_BASE_URL}/vision/detect-food`, {
      method: 'POST',
      body: formData, // Don't set Content-Type header for FormData
    }).then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    });
  },
  
  saveSelectedItems: (selectedItems: any[]) => apiRequest('/vision/save-selected-items', {
    method: 'POST',
    body: JSON.stringify({ selectedItems }),
  }),
};

// Export all APIs
export const api = {
  recipes: recipeApi,
  inventory: inventoryApi,
  mealPlans: mealPlanApi,
  vision: visionApi,
};

export default api;
