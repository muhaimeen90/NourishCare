'use client';

import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Calendar, ChevronLeft, ChevronRight, Plus, Clock, Users, ShoppingCart, Check } from 'lucide-react';
import { mockRecipes, Recipe } from '@/lib/mock-data';
import { ChatBot } from '@/components/ChatBot';

export default function MealPlanner() {
  const [currentWeek, setCurrentWeek] = useState(new Date());
  const [plannedMeals, setPlannedMeals] = useState<Record<string, Recipe>>({});
  const [showShoppingList, setShowShoppingList] = useState(false);

  const getWeekDates = (startDate: Date) => {
    const week = [];
    const start = new Date(startDate);
    start.setDate(start.getDate() - start.getDay()); // Start from Sunday
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(start);
      date.setDate(start.getDate() + i);
      week.push(date);
    }
    return week;
  };

  const weekDates = getWeekDates(currentWeek);
  const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  const mealTypes = ['Breakfast', 'Lunch', 'Dinner', 'Snack'];

  const navigateWeek = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentWeek);
    newDate.setDate(currentWeek.getDate() + (direction === 'next' ? 7 : -7));
    setCurrentWeek(newDate);
  };

  const addMealToDay = (date: string, mealType: string) => {
    // Mock adding a random recipe
    const randomRecipe = mockRecipes[Math.floor(Math.random() * mockRecipes.length)];
    const key = `${date}-${mealType}`;
    setPlannedMeals(prev => ({
      ...prev,
      [key]: randomRecipe
    }));
  };

  const generateShoppingList = () => {
    // Mock shopping list based on planned meals
    return [
      { item: 'Fresh vegetables', category: 'Produce', checked: false },
      { item: 'Chicken breast', category: 'Meat', checked: false },
      { item: 'Whole grain pasta', category: 'Pantry', checked: false },
      { item: 'Olive oil', category: 'Pantry', checked: false },
      { item: 'Fresh herbs', category: 'Produce', checked: false },
      { item: 'Greek yogurt', category: 'Dairy', checked: false },
      { item: 'Seasonal fruits', category: 'Produce', checked: false },
      { item: 'Quinoa', category: 'Pantry', checked: false },
    ];
  };

  const [shoppingList, setShoppingList] = useState(generateShoppingList());

  const toggleShoppingItem = (index: number) => {
    setShoppingList(prev => prev.map((item, i) => 
      i === index ? { ...item, checked: !item.checked } : item
    ));
  };

  const getTotalNutrition = () => {
    const planned = Object.values(plannedMeals);
    return {
      calories: planned.reduce((sum: number, meal: Recipe) => sum + (meal?.calories || 0), 0),
      meals: planned.length,
      prep_time: planned.reduce((sum: number, meal: Recipe) => {
        const timeStr = meal?.cookTime || '0';
        const time = parseInt(timeStr.replace(/\D/g, '')) || 0;
        return sum + time;
      }, 0)
    };
  };

  const nutrition = getTotalNutrition();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <Calendar className="h-8 w-8 text-green-600" />
                Meal Planner
              </h1>
              <p className="text-gray-600 mt-1">Plan your weekly meals and generate shopping lists</p>
            </div>
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => setShowShoppingList(!showShoppingList)}
                className="flex items-center gap-2"
              >
                <ShoppingCart className="h-4 w-4" />
                {showShoppingList ? 'Hide' : 'Show'} Shopping List
              </Button>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Main Planner */}
          <div className="lg:col-span-3">
            {/* Week Navigation */}
            <Card className="mb-6">
              <CardHeader>
                <div className="flex items-center justify-between">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => navigateWeek('prev')}
                    className="flex items-center gap-1"
                  >
                    <ChevronLeft className="h-4 w-4" />
                    Previous
                  </Button>
                  <div className="text-center">
                    <h2 className="text-lg font-semibold">
                      {weekDates[0].toLocaleDateString('en-US', { month: 'long', day: 'numeric' })} - {' '}
                      {weekDates[6].toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
                    </h2>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => navigateWeek('next')}
                    className="flex items-center gap-1"
                  >
                    Next
                    <ChevronRight className="h-4 w-4" />
                  </Button>
                </div>
              </CardHeader>
            </Card>

            {/* Weekly Calendar Grid */}
            <div className="grid grid-cols-1 md:grid-cols-7 gap-4">
              {weekDates.map((date, dayIndex) => (
                <Card key={date.toDateString()} className="min-h-[400px]">
                  <CardHeader className="pb-3">
                    <CardTitle className="text-sm font-medium text-center">
                      {dayNames[dayIndex]}
                    </CardTitle>
                    <CardDescription className="text-center text-lg font-semibold">
                      {date.getDate()}
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-2">
                    {mealTypes.map((mealType) => {
                      const key = `${date.toDateString()}-${mealType}`;
                      const plannedMeal = plannedMeals[key];
                      
                      return (
                        <div key={mealType} className="border border-gray-200 rounded-lg p-2">
                          <div className="flex items-center justify-between mb-1">
                            <span className="text-xs font-medium text-gray-600">{mealType}</span>
                            <Button
                              size="sm"
                              variant="ghost"
                              className="h-6 w-6 p-0"
                              onClick={() => addMealToDay(date.toDateString(), mealType)}
                            >
                              <Plus className="h-3 w-3" />
                            </Button>
                          </div>
                          {plannedMeal ? (
                            <div className="space-y-1">
                              <p className="text-xs font-medium truncate">{plannedMeal.title}</p>
                              <div className="flex items-center gap-1 text-xs text-gray-500">
                                <Clock className="h-3 w-3" />
                                {plannedMeal.cookTime}
                              </div>
                              <Badge variant="secondary" className="text-xs">
                                {plannedMeal.calories} cal
                              </Badge>
                            </div>
                          ) : (
                            <p className="text-xs text-gray-400">No meal planned</p>
                          )}
                        </div>
                      );
                    })}
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Week Summary */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Week Summary</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Total Calories</span>
                  <span className="font-semibold">{nutrition.calories.toLocaleString()}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Planned Meals</span>
                  <span className="font-semibold">{nutrition.meals}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Total Prep Time</span>
                  <span className="font-semibold">{nutrition.prep_time}m</span>
                </div>
                <Separator />
                <Button className="w-full bg-green-600 hover:bg-green-700">
                  Optimize Plan
                </Button>
              </CardContent>
            </Card>

            {/* Shopping List */}
            {showShoppingList && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <ShoppingCart className="h-5 w-5" />
                    Shopping List
                  </CardTitle>
                  <CardDescription>
                    Based on your planned meals
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {['Produce', 'Meat', 'Dairy', 'Pantry'].map(category => {
                      const categoryItems = shoppingList.filter(item => item.category === category);
                      if (categoryItems.length === 0) return null;
                      
                      return (
                        <div key={category}>
                          <h4 className="font-medium text-sm text-gray-900 mb-2">{category}</h4>
                          <div className="space-y-1">
                            {categoryItems.map((item, index) => {
                              const globalIndex = shoppingList.indexOf(item);
                              return (
                                <div
                                  key={index}
                                  className="flex items-center gap-2 text-sm"
                                >
                                  <button
                                    onClick={() => toggleShoppingItem(globalIndex)}
                                    className={`w-4 h-4 rounded border ${
                                      item.checked
                                        ? 'bg-green-600 border-green-600 text-white'
                                        : 'border-gray-300'
                                    } flex items-center justify-center`}
                                  >
                                    {item.checked && <Check className="h-2 w-2" />}
                                  </button>
                                  <span className={item.checked ? 'line-through text-gray-500' : ''}>
                                    {item.item}
                                  </span>
                                </div>
                              );
                            })}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                  <Separator className="my-4" />
                  <Button variant="outline" className="w-full">
                    Export List
                  </Button>
                </CardContent>
              </Card>
            )}

            {/* Quick Actions */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Quick Actions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                <Button variant="outline" className="w-full justify-start">
                  <Plus className="h-4 w-4 mr-2" />
                  Add Recipe
                </Button>
                <Button variant="outline" className="w-full justify-start">
                  <Calendar className="h-4 w-4 mr-2" />
                  Template Week
                </Button>
                <Button variant="outline" className="w-full justify-start">
                  <Users className="h-4 w-4 mr-2" />
                  Family Settings
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>

      <ChatBot />
    </div>
  );
}
