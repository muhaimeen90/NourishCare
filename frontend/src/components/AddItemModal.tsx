'use client';

import { useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Calendar } from '@/components/ui/calendar';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import { Scan, FileText, PlusCircle, Camera } from 'lucide-react';
import { api } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';

interface AddItemModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onItemAdded?: () => void; // Callback to refresh data
}

export function AddItemModal({ open, onOpenChange, onItemAdded }: AddItemModalProps) {
  const { user } = useAuth();
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(new Date());
  const [formData, setFormData] = useState({
    name: '',
    quantity: '',
    category: '',
    expirationDate: '',
  });
  const [loading, setLoading] = useState(false);

  const handleManualSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedDate || !formData.name || !formData.quantity || !formData.category || !user?.id) {
      console.error('Missing required fields or user not authenticated');
      return;
    }

    setLoading(true);
    try {
      await api.inventory.create({
        name: formData.name,
        quantity: formData.quantity,
        category: formData.category,
        expirationDate: selectedDate.toISOString().split('T')[0], // Format as YYYY-MM-DD
        userId: user.id, // Add the userId field
      });
      
      onOpenChange(false);
      onItemAdded?.(); // Refresh the data
      
      // Reset form
      setFormData({ name: '', quantity: '', category: '', expirationDate: '' });
      setSelectedDate(new Date());
    } catch (error) {
      console.error('Error adding item:', error);
      // You could add error handling here
    } finally {
      setLoading(false);
    }
  };

  const categories = [
    'Vegetables',
    'Fruits',
    'Dairy',
    'Meat',
    'Fish',
    'Grains',
    'Beverages',
    'Snacks',
    'Condiments',
    'Other'
  ];

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Add New Food Item</DialogTitle>
          <DialogDescription>
            Choose how you&apos;d like to add your food item
          </DialogDescription>
        </DialogHeader>

        <Tabs defaultValue="manual" className="w-full">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="manual" className="flex items-center gap-2">
              <PlusCircle className="h-4 w-4" />
              Manual
            </TabsTrigger>
            <TabsTrigger value="barcode" className="flex items-center gap-2">
              <Scan className="h-4 w-4" />
              Barcode
            </TabsTrigger>
            <TabsTrigger value="receipt" className="flex items-center gap-2">
              <FileText className="h-4 w-4" />
              Receipt
            </TabsTrigger>
          </TabsList>

          <TabsContent value="manual" className="space-y-4">
            <form onSubmit={handleManualSubmit} className="space-y-4">
              <div className="space-y-2">
                <label htmlFor="name" className="text-sm font-medium">
                  Item Name
                </label>
                <Input
                  id="name"
                  placeholder="e.g., Organic Spinach"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label htmlFor="quantity" className="text-sm font-medium">
                    Quantity
                  </label>
                  <Input
                    id="quantity"
                    placeholder="e.g., 1 bag"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label htmlFor="category" className="text-sm font-medium">
                    Category
                  </label>
                  <Select 
                    value={formData.category} 
                    onValueChange={(value) => setFormData({ ...formData, category: value })}
                    required
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      {categories.map((category) => (
                        <SelectItem key={category} value={category}>
                          {category}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">
                  Expiration Date
                </label>
                <div className="flex justify-center">
                  <Calendar
                    mode="single"
                    selected={selectedDate}
                    onSelect={setSelectedDate}
                    disabled={(date) => date < new Date()}
                    className="rounded-md border"
                  />
                </div>
              </div>

              <div className="flex gap-2">
                <Button type="button" variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
                  Cancel
                </Button>
                <Button 
                  type="submit" 
                  disabled={loading || !user?.id} 
                  className="flex-1 bg-green-600 hover:bg-green-700"
                >
                  {loading ? 'Adding...' : 'Add Item'}
                </Button>
              </div>
            </form>
          </TabsContent>

          <TabsContent value="barcode" className="space-y-4">
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12 text-center">
                <div className="bg-green-100 p-4 rounded-full mb-4">
                  <Scan className="h-8 w-8 text-green-600" />
                </div>
                <h3 className="text-lg font-semibold mb-2">Scan Barcode</h3>
                <p className="text-gray-600 mb-6 max-w-xs">
                  Point your camera at the product barcode to automatically add item details
                </p>
                <Button className="bg-green-600 hover:bg-green-700">
                  <Camera className="h-4 w-4 mr-2" />
                  Open Camera
                </Button>
                <p className="text-xs text-gray-500 mt-4">
                  Coming soon - Barcode scanning feature
                </p>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="receipt" className="space-y-4">
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12 text-center">
                <div className="bg-green-100 p-4 rounded-full mb-4">
                  <FileText className="h-8 w-8 text-green-600" />
                </div>
                <h3 className="text-lg font-semibold mb-2">Scan Receipt</h3>
                <p className="text-gray-600 mb-6 max-w-xs">
                  Upload a photo of your grocery receipt to automatically add multiple items
                </p>
                <Button className="bg-green-600 hover:bg-green-700">
                  <Camera className="h-4 w-4 mr-2" />
                  Take Photo
                </Button>
                <p className="text-xs text-gray-500 mt-4">
                  Coming soon - Receipt scanning feature
                </p>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </DialogContent>
    </Dialog>
  );
}
