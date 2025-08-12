'use client';

import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { ArrowLeft, Clock, Users, ChefHat, Heart, Share, Plus } from 'lucide-react';
import { mockRecipes } from '@/lib/mock-data';
import { Navigation } from '@/components/Navigation';
import { ChatBot } from '@/components/ChatBot';

export default function RecipeDetail() {
  const params = useParams();
  const router = useRouter();
  const recipeId = params.id as string;
  
  const recipe = mockRecipes.find(r => r.id === recipeId);

  if (!recipe) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <div className="container mx-auto px-4 py-8">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Recipe Not Found</h1>
            <Button onClick={() => router.back()}>Go Back</Button>
          </div>
        </div>
      </div>
    );
  }

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'Easy': return 'bg-green-100 text-green-800';
      case 'Medium': return 'bg-yellow-100 text-yellow-800';
      case 'Hard': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const nutritionInfo = [
    { label: 'Calories', value: recipe.calories, unit: 'cal' },
    { label: 'Protein', value: Math.round(recipe.calories * 0.15 / 4), unit: 'g' },
    { label: 'Carbs', value: Math.round(recipe.calories * 0.55 / 4), unit: 'g' },
    { label: 'Fat', value: Math.round(recipe.calories * 0.30 / 9), unit: 'g' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Back Button */}
        <Button 
          variant="ghost" 
          onClick={() => router.back()}
          className="mb-4"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Recipes
        </Button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Recipe Header */}
            <Card>
              <div className="relative aspect-video overflow-hidden rounded-t-lg">
                <div className="w-full h-full bg-gradient-to-br from-green-100 to-green-200 flex items-center justify-center">
                  <ChefHat className="h-16 w-16 text-green-600" />
                </div>
                <div className="absolute top-4 right-4 flex gap-2">
                  <Badge className={getDifficultyColor(recipe.difficulty)}>
                    {recipe.difficulty}
                  </Badge>
                </div>
              </div>
              
              <CardHeader>
                <div className="flex justify-between items-start">
                  <div>
                    <CardTitle className="text-2xl mb-2">{recipe.title}</CardTitle>
                    <CardDescription className="text-base">
                      {recipe.description}
                    </CardDescription>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="outline" size="icon">
                      <Heart className="h-4 w-4" />
                    </Button>
                    <Button variant="outline" size="icon">
                      <Share className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
                
                <div className="flex items-center gap-6 text-sm text-gray-600 pt-4">
                  <div className="flex items-center gap-1">
                    <Clock className="h-4 w-4" />
                    <span>{recipe.cookTime}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" />
                    <span>{recipe.servings} servings</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <span className="text-orange-600 font-medium">{recipe.calories} calories per serving</span>
                  </div>
                </div>
              </CardHeader>
            </Card>

            {/* Ingredients */}
            <Card>
              <CardHeader>
                <CardTitle>Ingredients</CardTitle>
                <CardDescription>
                  Everything you need for {recipe.servings} servings
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {recipe.ingredients.map((ingredient, index) => (
                    <div key={index} className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors">
                      <div className="w-6 h-6 rounded-full border-2 border-green-200 flex items-center justify-center">
                        <div className="w-3 h-3 rounded-full bg-green-600"></div>
                      </div>
                      <span className="flex-1">{ingredient}</span>
                    </div>
                  ))}
                </div>
                
                <Separator className="my-6" />
                
                <div className="flex gap-2">
                  <Button className="flex-1 bg-green-600 hover:bg-green-700">
                    <Plus className="h-4 w-4 mr-2" />
                    Add to Shopping List
                  </Button>
                  <Button variant="outline" className="flex-1">
                    Check My Fridge
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* Instructions */}
            <Card>
              <CardHeader>
                <CardTitle>Instructions</CardTitle>
                <CardDescription>
                  Step-by-step cooking instructions
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  {recipe.instructions.map((instruction, index) => (
                    <div key={index} className="flex gap-4">
                      <div className="flex-shrink-0 w-8 h-8 bg-green-100 text-green-600 rounded-full flex items-center justify-center font-semibold text-sm">
                        {index + 1}
                      </div>
                      <div className="flex-1 pt-1">
                        <p className="text-gray-900">{instruction}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Nutrition Info */}
            <Card>
              <CardHeader>
                <CardTitle>Nutrition Facts</CardTitle>
                <CardDescription>Per serving</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {nutritionInfo.map((item, index) => (
                    <div key={index} className="flex justify-between items-center">
                      <span className="text-gray-600">{item.label}</span>
                      <span className="font-semibold">{item.value}{item.unit}</span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* Recipe Actions */}
            <Card>
              <CardHeader>
                <CardTitle>Recipe Actions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <Button className="w-full bg-green-600 hover:bg-green-700">
                  Add to Meal Plan
                </Button>
                <Button variant="outline" className="w-full">
                  Save to Favorites
                </Button>
                <Button variant="outline" className="w-full">
                  Share Recipe
                </Button>
                <Button variant="outline" className="w-full">
                  Print Recipe
                </Button>
              </CardContent>
            </Card>

            {/* Recipe Tips */}
            <Card>
              <CardHeader>
                <CardTitle>Chef&apos;s Tips</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 text-sm text-gray-600">
                  <div className="p-3 bg-blue-50 rounded-lg">
                    <p className="font-medium text-blue-900 mb-1">💡 Pro Tip</p>
                    <p className="text-blue-800">For best results, let ingredients come to room temperature before cooking.</p>
                  </div>
                  <div className="p-3 bg-green-50 rounded-lg">
                    <p className="font-medium text-green-900 mb-1">🌱 Waste Reduction</p>
                    <p className="text-green-800">Save vegetable scraps for making homemade stock!</p>
                  </div>
                  <div className="p-3 bg-orange-50 rounded-lg">
                    <p className="font-medium text-orange-900 mb-1">⏰ Time Saver</p>
                    <p className="text-orange-800">Prep ingredients the night before to save time.</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      <ChatBot />
    </div>
  );
}
