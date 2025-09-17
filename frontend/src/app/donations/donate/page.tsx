'use client';

import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Calendar } from "@/components/ui/calendar";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ArrowLeft, Plus, Trash2, Calendar as CalendarIcon, Save, Info } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { donationApi, inventoryApi } from '@/lib/api';

// Types
interface FoodItem {
  id: string;
  name: string;
  category: string;
  quantity: number | string; // Can be either number or string like "1 kg"
  unit: string;
  expirationDate: string;
  isOpened: boolean;
}

interface DonationFormData {
  donorName: string;
  donorPhone: string;
  donorEmail: string;
  address: string;
  city: string;
  pickupInstructions: string;
  description: string;
  foodItems: string[]; // Array of food item IDs
}

const FOOD_CATEGORIES = [
  'Fruits', 'Vegetables', 'Dairy', 'Meat & Poultry', 'Fish & Seafood',
  'Grains & Cereals', 'Bakery', 'Canned Goods', 'Frozen Foods',
  'Snacks', 'Beverages', 'Prepared Food', 'Dessert', 'Other'
];

const UNITS = [
  'pieces', 'lbs', 'oz', 'kg', 'g', 'liters', 'ml', 'cups',
  'containers', 'packages', 'bunches', 'loaves', 'bottles', 'cans'
];

const DonatePage = () => {
  const router = useRouter();
  const { user, token, isAuthenticated } = useAuth();
  
  const [formData, setFormData] = useState<DonationFormData>({
    donorName: '',
    donorPhone: '',
    donorEmail: '',
    address: '',
    city: '',
    pickupInstructions: '',
    description: '',
    foodItems: []
  });

  const [userFoodItems, setUserFoodItems] = useState<FoodItem[]>([]);
  const [selectedItems, setSelectedItems] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [itemsLoading, setItemsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showAddItemDialog, setShowAddItemDialog] = useState(false);

  // New food item form state
  const [newItem, setNewItem] = useState({
    name: '',
    category: '',
    quantity: 1,
    unit: 'pieces',
    expirationDate: '',
    isOpened: false
  });

  // Redirect if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/auth/login?callbackUrl=' + encodeURIComponent('/donations/donate'));
      return;
    }
  }, [isAuthenticated, router]);

  // Pre-fill form with user data
  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        donorName: `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username || '',
        donorEmail: user.email || ''
      }));
    }
  }, [user]);

  // Load user's food items from inventory API
  useEffect(() => {
    const loadUserItems = async () => {
      if (!user?.id || !token) return;
      
      console.log('Loading inventory for user:', { userId: user.id, email: user.email, token: token ? 'present' : 'missing' });
      
      setItemsLoading(true);
      setError(null);
      
      try {
        // Fetch user-specific inventory items
        const url = `${process.env.NEXT_PUBLIC_API_URL}/api/inventory/users/${user.id}/items`;
        console.log('Fetching inventory from URL:', url);
        
        const response = await fetch(url, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        console.log('Inventory API response:', { status: response.status, ok: response.ok });

        if (!response.ok) {
          const errorText = await response.text();
          console.error('Inventory API error:', errorText);
          throw new Error(`Failed to fetch inventory: ${response.status} - ${errorText}`);
        }

        const items: FoodItem[] = await response.json();
        console.log('Inventory items received:', items);
        
        // Filter only available items (not expired, not consumed, quantity > 0)
        const availableItems = items.filter(item => {
          const expirationDate = new Date(item.expirationDate);
          const today = new Date();
          const isNotExpired = expirationDate >= today;
          
          // Handle both string and number quantity formats
          let hasQuantity = false;
          if (typeof item.quantity === 'string') {
            // For string quantities like "1 kg", assume they are available if not empty
            hasQuantity = item.quantity.trim().length > 0 && !item.quantity.toLowerCase().includes('0');
          } else {
            // For number quantities
            hasQuantity = item.quantity > 0;
          }
          
          console.log(`Item ${item.name}: expired=${!isNotExpired}, hasQuantity=${hasQuantity}, quantity=${item.quantity}`);
          
          return isNotExpired && hasQuantity;
        });
        
        console.log('Available items after filtering:', availableItems);
        setUserFoodItems(availableItems);
      } catch (error) {
        console.error('Error loading user inventory:', error);
        setError('Failed to load your inventory items. Please try again.');
        // Fallback to empty array instead of mock data
        setUserFoodItems([]);
      } finally {
        setItemsLoading(false);
      }
    };

    loadUserItems();
  }, [user?.id, token]);

  const handleInputChange = (field: keyof DonationFormData, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleItemSelection = (itemId: string, selected: boolean) => {
    if (selected) {
      setSelectedItems(prev => [...prev, itemId]);
    } else {
      setSelectedItems(prev => prev.filter(id => id !== itemId));
    }
  };

  const addNewFoodItem = () => {
    if (!newItem.name || !newItem.category || !newItem.expirationDate) {
      alert('Please fill in all required fields');
      return;
    }

    const item: FoodItem = {
      id: Date.now().toString(),
      name: newItem.name,
      category: newItem.category,
      quantity: newItem.quantity,
      unit: newItem.unit,
      expirationDate: newItem.expirationDate,
      isOpened: newItem.isOpened
    };

    setUserFoodItems(prev => [...prev, item]);
    setSelectedItems(prev => [...prev, item.id]);
    
    // Reset form
    setNewItem({
      name: '',
      category: '',
      quantity: 1,
      unit: 'pieces',
      expirationDate: '',
      isOpened: false
    });
    setShowAddItemDialog(false);
  };

  const submitDonation = async () => {
    if (!user?.id || !token) {
      setError('You must be logged in to create a donation');
      return;
    }

    if (!formData.donorName || !formData.donorPhone || !formData.address || !formData.city) {
      setError('Please fill in all required fields');
      return;
    }

    if (selectedItems.length === 0) {
      setError('Please select at least one food item to donate');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Create donation request
      const donationRequest = {
        donorName: formData.donorName,
        donorPhone: formData.donorPhone,
        donorEmail: formData.donorEmail,
        address: formData.address,
        city: formData.city,
        pickupInstructions: formData.pickupInstructions,
        description: formData.description,
        foodItemIds: selectedItems
      };

      console.log('Submitting donation:', donationRequest);

      // Submit to API with authentication
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/community/donations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(donationRequest)
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || `Failed to create donation: ${response.status}`);
      }

      const result = await response.json();
      console.log('Donation created successfully:', result);

      // Success
      alert('Donation created successfully! Thank you for sharing with the community.');
      router.push('/donations');
    } catch (err) {
      console.error('Error creating donation:', err);
      setError('Failed to create donation. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getDaysUntilExpiry = (expirationDate: string) => {
    const today = new Date();
    const expiry = new Date(expirationDate);
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const getExpiryBadgeColor = (days: number) => {
    if (days <= 1) return 'destructive';
    if (days <= 3) return 'secondary';
    return 'default';
  };

  const selectedItemsData = userFoodItems.filter(item => selectedItems.includes(item.id));

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      {/* Authentication Check */}
      {!isAuthenticated ? (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Redirecting to login...</p>
          </div>
        </div>
      ) : (
        <>
          <div className="mb-8">
            <div className="flex items-center mb-4">
              <Link href="/donations">
                <Button variant="ghost" size="sm">
                  <ArrowLeft className="w-4 h-4 mr-2" />
                  Back to Donations
                </Button>
              </Link>
            </div>
            
            <h1 className="text-3xl font-bold">Donate Food</h1>
            <p className="text-muted-foreground mt-2">
              Share your excess food with the community and help reduce food waste
            </p>
          </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Left Column: Donor Information */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Your Information</CardTitle>
              <CardDescription>
                This information will be shared with people who want to claim your donation
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium">Name *</label>
                <Input
                  placeholder="Your full name"
                  value={formData.donorName}
                  onChange={(e) => handleInputChange('donorName', e.target.value)}
                  className="mt-1"
                />
              </div>
              
              <div>
                <label className="text-sm font-medium">Phone Number *</label>
                <Input
                  placeholder="+1 (555) 123-4567"
                  value={formData.donorPhone}
                  onChange={(e) => handleInputChange('donorPhone', e.target.value)}
                  className="mt-1"
                />
              </div>
              
              <div>
                <label className="text-sm font-medium">Email</label>
                <Input
                  placeholder="your.email@example.com"
                  type="email"
                  value={formData.donorEmail}
                  onChange={(e) => handleInputChange('donorEmail', e.target.value)}
                  className="mt-1"
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Pickup Location</CardTitle>
              <CardDescription>
                Where can people pick up the food?
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium">Address *</label>
                <Input
                  placeholder="123 Main Street, Apt 4B"
                  value={formData.address}
                  onChange={(e) => handleInputChange('address', e.target.value)}
                  className="mt-1"
                />
              </div>
              
              <div>
                <label className="text-sm font-medium">City *</label>
                <Input
                  placeholder="New York"
                  value={formData.city}
                  onChange={(e) => handleInputChange('city', e.target.value)}
                  className="mt-1"
                />
              </div>
              
              <div>
                <label className="text-sm font-medium">Pickup Instructions</label>
                <textarea
                  placeholder="Ring doorbell. Available evenings after 6 PM. Leave at door if no answer."
                  value={formData.pickupInstructions}
                  onChange={(e) => handleInputChange('pickupInstructions', e.target.value)}
                  className="mt-1 w-full min-h-[80px] px-3 py-2 text-sm ring-offset-background border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                />
              </div>
              
              <div>
                <label className="text-sm font-medium">Description</label>
                <textarea
                  placeholder="Additional details about your donation..."
                  value={formData.description}
                  onChange={(e) => handleInputChange('description', e.target.value)}
                  className="mt-1 w-full min-h-[80px] px-3 py-2 text-sm ring-offset-background border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column: Food Items Selection */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <div className="flex justify-between items-center">
                <div>
                  <CardTitle>Select Food Items</CardTitle>
                  <CardDescription>
                    Choose which items from your inventory to donate
                  </CardDescription>
                </div>
                <Dialog open={showAddItemDialog} onOpenChange={setShowAddItemDialog}>
                  <DialogTrigger asChild>
                    <Button variant="outline" size="sm">
                      <Plus className="w-4 h-4 mr-1" />
                      Add Item
                    </Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Add New Food Item</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4 py-4">
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="text-sm font-medium">Item Name *</label>
                          <Input
                            placeholder="e.g., Organic Apples"
                            value={newItem.name}
                            onChange={(e) => setNewItem(prev => ({ ...prev, name: e.target.value }))}
                            className="mt-1"
                          />
                        </div>
                        <div>
                          <label className="text-sm font-medium">Category *</label>
                          <Select
                            value={newItem.category}
                            onValueChange={(value) => setNewItem(prev => ({ ...prev, category: value }))}
                          >
                            <SelectTrigger className="mt-1">
                              <SelectValue placeholder="Select category" />
                            </SelectTrigger>
                            <SelectContent>
                              {FOOD_CATEGORIES.map(category => (
                                <SelectItem key={category} value={category}>
                                  {category}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </div>
                      
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="text-sm font-medium">Quantity *</label>
                          <Input
                            type="number"
                            min="1"
                            value={newItem.quantity}
                            onChange={(e) => setNewItem(prev => ({ ...prev, quantity: parseInt(e.target.value) || 1 }))}
                            className="mt-1"
                          />
                        </div>
                        <div>
                          <label className="text-sm font-medium">Unit *</label>
                          <Select
                            value={newItem.unit}
                            onValueChange={(value) => setNewItem(prev => ({ ...prev, unit: value }))}
                          >
                            <SelectTrigger className="mt-1">
                              <SelectValue placeholder="Select unit" />
                            </SelectTrigger>
                            <SelectContent>
                              {UNITS.map(unit => (
                                <SelectItem key={unit} value={unit}>
                                  {unit}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </div>
                      
                      <div>
                        <label className="text-sm font-medium">Expiration Date *</label>
                        <Input
                          type="date"
                          value={newItem.expirationDate}
                          onChange={(e) => setNewItem(prev => ({ ...prev, expirationDate: e.target.value }))}
                          className="mt-1"
                        />
                      </div>
                      
                      <div className="flex justify-end space-x-2">
                        <Button
                          variant="outline"
                          onClick={() => setShowAddItemDialog(false)}
                        >
                          Cancel
                        </Button>
                        <Button onClick={addNewFoodItem}>
                          Add Item
                        </Button>
                      </div>
                    </div>
                  </DialogContent>
                </Dialog>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {itemsLoading ? (
                  <div className="text-center py-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mx-auto mb-4"></div>
                    <p className="text-gray-600">Loading your inventory items...</p>
                  </div>
                ) : userFoodItems.length === 0 ? (
                  <div className="text-center py-8 bg-gray-50 rounded-lg">
                    <div className="w-16 h-16 mx-auto mb-4 text-gray-400">
                      <Info className="w-16 h-16" />
                    </div>
                    <p className="text-gray-600 mb-2">No food items available for donation</p>
                    <p className="text-sm text-gray-500 mb-4">
                      Add items to your inventory first, or all your items may be expired.
                    </p>
                    <Button 
                      onClick={() => router.push('/dashboard')}
                      variant="outline"
                    >
                      Go to Inventory
                    </Button>
                  </div>
                ) : (
                  <>
                    {userFoodItems.map((item) => {
                  const daysLeft = getDaysUntilExpiry(item.expirationDate);
                  const isSelected = selectedItems.includes(item.id);
                  
                  return (
                    <div
                      key={item.id}
                      className={`p-3 border rounded-lg cursor-pointer transition-colors ${
                        isSelected ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/50'
                      }`}
                      onClick={() => handleItemSelection(item.id, !isSelected)}
                    >
                      <div className="flex justify-between items-start">
                        <div className="flex-1">
                          <div className="flex items-center space-x-2">
                            <input
                              type="checkbox"
                              checked={isSelected}
                              onChange={(e) => handleItemSelection(item.id, e.target.checked)}
                              className="rounded"
                            />
                            <span className="font-medium">{item.name}</span>
                          </div>
                          <p className="text-sm text-muted-foreground mt-1">
                            {typeof item.quantity === 'string' ? item.quantity : `${item.quantity} ${item.unit}`} • {item.category}
                          </p>
                        </div>
                        <Badge variant={getExpiryBadgeColor(daysLeft)} className="text-xs">
                          {daysLeft === 0 ? 'Today' : daysLeft === 1 ? '1 day' : `${daysLeft} days`}
                        </Badge>
                      </div>
                    </div>
                  );
                })}
                
                {userFoodItems.length === 0 && (
                  <div className="text-center py-8 text-muted-foreground">
                    <Info className="w-8 h-8 mx-auto mb-2 opacity-50" />
                    <p>No food items in your inventory.</p>
                    <p className="text-sm">Add some items to donate!</p>
                  </div>
                )}
                  </>
                )}
              </div>
            </CardContent>
          </Card>

          {selectedItems.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle>Donation Summary</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <p className="text-sm text-muted-foreground mb-3">
                    You're donating {selectedItems.length} item{selectedItems.length !== 1 ? 's' : ''}:
                  </p>
                  {selectedItemsData.map((item) => (
                    <div key={item.id} className="flex justify-between items-center p-2 bg-muted rounded">
                      <span className="text-sm">
                        {item.name} ({typeof item.quantity === 'string' ? item.quantity : `${item.quantity} ${item.unit}`})
                      </span>
                      <Badge variant="outline" className="text-xs">
                        {item.category}
                      </Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {error && (
            <Card>
              <CardContent className="py-4">
                <p className="text-sm text-destructive">{error}</p>
              </CardContent>
            </Card>
          )}

          <Button
            onClick={submitDonation}
            disabled={loading || selectedItems.length === 0}
            className="w-full"
            size="lg"
          >
            {loading ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                Creating Donation...
              </>
            ) : (
              <>
                <Save className="w-4 h-4 mr-2" />
                Create Donation
              </>
            )}
          </Button>
        </div>
      </div>
        </>
      )}
    </div>
  );
};

export default DonatePage;