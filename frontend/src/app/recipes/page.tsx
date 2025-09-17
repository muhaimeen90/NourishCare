'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Search, Clock, Users, ChefHat, Filter } from 'lucide-react';
import { api } from '@/lib/api';
import { ChatBot } from '@/components/ChatBot';

interface Recipe {
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

export default function Recipes() {
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState('all');
  const [selectedCookTime, setSelectedCookTime] = useState('all');

  useEffect(() => {
    fetchRecipes();
  }, []);

  const fetchRecipes = async () => {
    try {
      setLoading(true);
      const data = await api.recipes.getAll();
      setRecipes(data);
    } catch (err) {
      setError('Failed to fetch recipes');
      console.error('Error fetching recipes:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredRecipes = recipes.filter(recipe => {
    const matchesSearch = recipe.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         recipe.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDifficulty = selectedDifficulty === 'all' || recipe.difficulty === selectedDifficulty;
    const matchesCookTime = selectedCookTime === 'all' || 
                           (selectedCookTime === 'quick' && parseInt(recipe.cookTime) <= 20) ||
                           (selectedCookTime === 'medium' && parseInt(recipe.cookTime) > 20 && parseInt(recipe.cookTime) <= 40) ||
                           (selectedCookTime === 'long' && parseInt(recipe.cookTime) > 40);
    
    return matchesSearch && matchesDifficulty && matchesCookTime;
  });

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'Easy': return 'bg-green-100 text-green-800';
      case 'Medium': return 'bg-yellow-100 text-yellow-800';
      case 'Hard': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-green-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading recipes...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center">
          <p className="text-red-600">{error}</p>
          <Button onClick={fetchRecipes} className="mt-4">Try Again</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-3xl font-bold text-gray-900">Recipe Collection</h1>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Discover delicious and healthy recipes that make the most of your ingredients while reducing food waste.
          </p>
        </div>

        {/* Search and Filters */}
        <Card>
          <CardContent className="p-6">
            <div className="flex flex-col md:flex-row gap-4">
              {/* Search */}
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
                <Input
                  placeholder="Search recipes..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>

              {/* Filters */}
              <div className="flex gap-4">
                <Select value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
                  <SelectTrigger className="w-32">
                    <SelectValue placeholder="Difficulty" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Levels</SelectItem>
                    <SelectItem value="Easy">Easy</SelectItem>
                    <SelectItem value="Medium">Medium</SelectItem>
                    <SelectItem value="Hard">Hard</SelectItem>
                  </SelectContent>
                </Select>

                <Select value={selectedCookTime} onValueChange={setSelectedCookTime}>
                  <SelectTrigger className="w-32">
                    <SelectValue placeholder="Cook Time" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Any Time</SelectItem>
                    <SelectItem value="quick">≤ 20 min</SelectItem>
                    <SelectItem value="medium">21-40 min</SelectItem>
                    <SelectItem value="long">&gt; 40 min</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Active Filters */}
            {(searchTerm || selectedDifficulty !== 'all' || selectedCookTime !== 'all') && (
              <div className="flex items-center gap-2 mt-4 pt-4 border-t">
                <Filter className="h-4 w-4 text-gray-500" />
                <span className="text-sm text-gray-500">Active filters:</span>
                {searchTerm && (
                  <Badge variant="secondary">Search: {searchTerm}</Badge>
                )}
                {selectedDifficulty !== 'all' && (
                  <Badge variant="secondary">Difficulty: {selectedDifficulty}</Badge>
                )}
                {selectedCookTime !== 'all' && (
                  <Badge variant="secondary">
                    Time: {selectedCookTime === 'quick' ? '≤ 20 min' : 
                           selectedCookTime === 'medium' ? '21-40 min' : '> 40 min'}
                  </Badge>
                )}
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => {
                    setSearchTerm('');
                    setSelectedDifficulty('all');
                    setSelectedCookTime('all');
                  }}
                  className="text-xs"
                >
                  Clear all
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Recipe Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredRecipes.map((recipe) => (
            <Link key={recipe.id} href={`/recipes/${recipe.id}`}>
              <Card className="group hover:shadow-lg transition-all duration-200 cursor-pointer h-full">
                <div className="relative aspect-video overflow-hidden rounded-t-lg">
                  <div className="w-full h-full bg-gradient-to-br from-green-100 to-green-200 flex items-center justify-center">
                    <ChefHat className="h-12 w-12 text-green-600" />
                  </div>
                  <div className="absolute top-3 right-3 flex gap-2">
                    <Badge className={getDifficultyColor(recipe.difficulty)}>
                      {recipe.difficulty}
                    </Badge>
                  </div>
                </div>
                
                <CardHeader className="pb-2">
                  <CardTitle className="group-hover:text-green-600 transition-colors line-clamp-2">
                    {recipe.title}
                  </CardTitle>
                  <CardDescription className="line-clamp-2">
                    {recipe.description}
                  </CardDescription>
                </CardHeader>
                
                <CardContent className="space-y-4">
                  <div className="flex items-center justify-between text-sm text-gray-600">
                    <div className="flex items-center gap-1">
                      <Clock className="h-4 w-4" />
                      <span>{recipe.cookTime}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Users className="h-4 w-4" />
                      <span>{recipe.servings} servings</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <span className="text-orange-600 font-medium">{recipe.calories} cal</span>
                    </div>
                  </div>
                  
                  <div className="flex flex-wrap gap-1">
                    {recipe.ingredients.slice(0, 3).map((ingredient, index) => (
                      <Badge key={index} variant="outline" className="text-xs">
                        {ingredient.split(',')[0]}
                      </Badge>
                    ))}
                    {recipe.ingredients.length > 3 && (
                      <Badge variant="outline" className="text-xs">
                        +{recipe.ingredients.length - 3} more
                      </Badge>
                    )}
                  </div>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>

        {/* No Results */}
        {filteredRecipes.length === 0 && (
          <div className="text-center py-12">
            <ChefHat className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No recipes found</h3>
            <p className="text-gray-600 mb-4">
              Try adjusting your search terms or filters to find more recipes
            </p>
            <Button
              onClick={() => {
                setSearchTerm('');
                setSelectedDifficulty('all');
                setSelectedCookTime('all');
              }}
              variant="outline"
            >
              Clear Filters
            </Button>
          </div>
        )}

        {/* Quick Stats */}
        <Card>
          <CardContent className="p-6">
            <div className="text-center">
              <h3 className="text-lg font-semibold mb-4">Recipe Collection Stats</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div>
                  <div className="text-2xl font-bold text-green-600">{recipes.length}</div>
                  <div className="text-sm text-gray-600">Total Recipes</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-blue-600">
                    {recipes.filter((r: Recipe) => r.difficulty === 'Easy').length}
                  </div>
                  <div className="text-sm text-gray-600">Easy Recipes</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-orange-600">
                    {recipes.length > 0 ? Math.round(recipes.reduce((acc: number, r: Recipe) => acc + parseInt(r.cookTime), 0) / recipes.length) : 0}
                  </div>
                  <div className="text-sm text-gray-600">Avg Cook Time (min)</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-purple-600">
                    {recipes.length > 0 ? Math.round(recipes.reduce((acc: number, r: Recipe) => acc + r.calories, 0) / recipes.length) : 0}
                  </div>
                  <div className="text-sm text-gray-600">Avg Calories</div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

      <ChatBot />
    </div>
  );
}
