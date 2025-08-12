'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { Plus, AlertTriangle, TrendingUp, Leaf, DollarSign, Calendar, Package } from 'lucide-react';
import { mockFoodItems, mockAnalyticsData } from '@/lib/mock-data';
import { AddItemModal } from '@/components/AddItemModal';
import { ChatBot } from '@/components/ChatBot';
import { Navigation } from '@/components/Navigation';

export default function Dashboard() {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const getExpirationColor = (days: number) => {
    if (days >= 5) return 'expiration-green';
    if (days >= 2) return 'expiration-yellow';
    return 'expiration-red';
  };

  const getExpirationBadge = (days: number) => {
    if (days >= 5) return { variant: 'default' as const, text: `${days} days left`, color: 'bg-green-100 text-green-800' };
    if (days >= 2) return { variant: 'secondary' as const, text: `${days} days left`, color: 'bg-yellow-100 text-yellow-800' };
    if (days >= 0) return { variant: 'destructive' as const, text: `${days} days left`, color: 'bg-red-100 text-red-800' };
    return { variant: 'destructive' as const, text: 'Expired', color: 'bg-red-100 text-red-800' };
  };

  const categoryIcons: { [key: string]: string } = {
    'Vegetables': '🥬',
    'Dairy': '🥛',
    'Meat': '🥩',
    'Fruits': '🍎',
    'Grains': '🌾',
    'Fish': '🐟',
  };

  const wasteData = [
    { name: 'Food Saved', value: mockAnalyticsData.wasteReduced, fill: '#22c55e' },
    { name: 'Food Wasted', value: 15, fill: '#ef4444' },
  ];

  const categoryData = mockAnalyticsData.wasteByCategory.map(item => ({
    category: item.category,
    saved: item.saved,
    wasted: item.wasted,
  }));

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Virtual Fridge</h1>
            <p className="text-gray-600 mt-1">Track your food inventory and reduce waste</p>
          </div>
          <Button 
            onClick={() => setIsAddModalOpen(true)}
            className="bg-green-600 hover:bg-green-700"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add New Item
          </Button>
        </div>

        {/* Analytics Dashboard */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Items</CardTitle>
              <Package className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{mockAnalyticsData.totalItems}</div>
              <p className="text-xs text-muted-foreground">
                <TrendingUp className="h-3 w-3 inline mr-1 text-green-600" />
                +2 from yesterday
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Expiring Soon</CardTitle>
              <AlertTriangle className="h-4 w-4 text-yellow-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-yellow-600">{mockAnalyticsData.expiringSoon}</div>
              <p className="text-xs text-muted-foreground">
                Items expiring in 2 days
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Money Saved</CardTitle>
              <DollarSign className="h-4 w-4 text-green-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">${mockAnalyticsData.moneySaved}</div>
              <p className="text-xs text-muted-foreground">
                <TrendingUp className="h-3 w-3 inline mr-1 text-green-600" />
                +12% this month
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">CO₂ Saved</CardTitle>
              <Leaf className="h-4 w-4 text-green-600" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">{mockAnalyticsData.co2Saved}kg</div>
              <p className="text-xs text-muted-foreground">
                Carbon footprint reduced
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle>Food Waste Overview</CardTitle>
              <CardDescription>Your waste reduction progress this month</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={wasteData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {wasteData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.fill} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Waste by Category</CardTitle>
              <CardDescription>Breakdown by food category</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={categoryData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="category" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="saved" fill="#22c55e" name="Saved" />
                  <Bar dataKey="wasted" fill="#ef4444" name="Wasted" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        {/* Food Items Grid */}
        <Card>
          <CardHeader>
            <CardTitle>Your Food Items</CardTitle>
            <CardDescription>
              Color-coded by expiration date: 
              <Badge className="ml-2 bg-green-100 text-green-800">5+ days</Badge>
              <Badge className="ml-2 bg-yellow-100 text-yellow-800">2-4 days</Badge>
              <Badge className="ml-2 bg-red-100 text-red-800">0-1 day</Badge>
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {mockFoodItems.map((item) => {
                const badge = getExpirationBadge(item.daysUntilExpiration);
                return (
                  <Card key={item.id} className={`transition-all duration-200 hover:shadow-md ${getExpirationColor(item.daysUntilExpiration)}`}>
                    <CardContent className="p-4">
                      <div className="flex items-start justify-between mb-3">
                        <div className="text-2xl">
                          {categoryIcons[item.category] || '📦'}
                        </div>
                        <Badge className={badge.color}>
                          {badge.text}
                        </Badge>
                      </div>
                      <h3 className="font-semibold text-gray-900 mb-1">{item.name}</h3>
                      <p className="text-sm text-gray-600 mb-2">{item.quantity}</p>
                      <div className="flex items-center text-xs text-gray-500">
                        <Calendar className="h-3 w-3 mr-1" />
                        Expires: {new Date(item.expirationDate).toLocaleDateString()}
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>

            {mockFoodItems.length === 0 && (
              <div className="text-center py-12">
                <Package className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No items in your fridge</h3>
                <p className="text-gray-600 mb-4">Start by adding your first food item</p>
                <Button onClick={() => setIsAddModalOpen(true)}>
                  <Plus className="h-4 w-4 mr-2" />
                  Add Item
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <AddItemModal open={isAddModalOpen} onOpenChange={setIsAddModalOpen} />
      <ChatBot />
    </div>
  );
}
