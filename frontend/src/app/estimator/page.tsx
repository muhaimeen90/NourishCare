'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Upload, Camera, Image as ImageIcon, Zap, Check, X, Edit3, Save, AlertCircle, Info } from 'lucide-react';
import { ChatBot } from '@/components/ChatBot';
import { api } from '@/lib/api';

interface DetectedFoodItem {
  name: string;
  category: string;
  estimatedWeight: string;
  confidence: number;
  selected: boolean;
}

export default function CalorieEstimator() {
  const [uploadedImage, setUploadedImage] = useState<string | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [detectedItems, setDetectedItems] = useState<DetectedFoodItem[]>([]);
  const [analysisComplete, setAnalysisComplete] = useState(false);
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  const [editWeight, setEditWeight] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setUploadedImage(e.target?.result as string);
        setAnalysisComplete(false);
        setDetectedItems([]);
        setError(null);
        setSuccess(null);
      };
      reader.readAsDataURL(file);
    }
  };

  const analyzeImage = async () => {
    if (!selectedFile) {
      setError('Please select an image first');
      return;
    }

    setIsAnalyzing(true);
    setError(null);

    try {
      const response = await api.vision.detectFood(selectedFile);
      
      if (response.success) {
        setDetectedItems(response.detectedItems || []);
        setAnalysisComplete(true);
        if (response.detectedItems.length === 0) {
          setError('No food items detected in the image. Please try a different image.');
        }
      } else {
        setError(response.message || 'Failed to analyze image');
      }
    } catch (error) {
      console.error('Error analyzing image:', error);
      setError('Failed to analyze image. Please try again.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const toggleItemSelection = (index: number) => {
    const updatedItems = [...detectedItems];
    updatedItems[index].selected = !updatedItems[index].selected;
    setDetectedItems(updatedItems);
  };

  const startEditingWeight = (index: number) => {
    setEditingIndex(index);
    setEditWeight(detectedItems[index].estimatedWeight);
  };

  const saveWeight = (index: number) => {
    const updatedItems = [...detectedItems];
    updatedItems[index].estimatedWeight = editWeight;
    setDetectedItems(updatedItems);
    setEditingIndex(null);
    setEditWeight('');
  };

  const cancelEdit = () => {
    setEditingIndex(null);
    setEditWeight('');
  };

  const saveSelectedItems = async () => {
    const selectedItems = detectedItems.filter(item => item.selected);
    
    if (selectedItems.length === 0) {
      setError('Please select at least one item to save');
      return;
    }

    setIsSaving(true);
    setError(null);

    try {
      const response = await api.vision.saveSelectedItems(selectedItems);
      
      if (response.success) {
        setSuccess(`Successfully saved ${selectedItems.length} items to your inventory!`);
        // Reset the form after successful save
        setTimeout(() => {
          setUploadedImage(null);
          setSelectedFile(null);
          setDetectedItems([]);
          setAnalysisComplete(false);
          setSuccess(null);
        }, 2000);
      } else {
        setError(response.message || 'Failed to save items');
      }
    } catch (error) {
      console.error('Error saving items:', error);
      setError('Failed to save items. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };

  const selectedCount = detectedItems.filter(item => item.selected).length;

  return (
    <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-3xl font-bold text-gray-900">AI Food Detection</h1>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Upload a photo of your meal and our AI will detect the food items, estimate weights, and add them to your inventory!
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Upload Section */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Camera className="h-5 w-5 text-green-600" />
                  Upload Food Image
                </CardTitle>
                <CardDescription>
                  Select an image of your food to detect ingredients automatically
                </CardDescription>
              </CardHeader>
              <CardContent>
                {!uploadedImage ? (
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-green-400 transition-colors">
                    <input
                      type="file"
                      accept="image/*"
                      onChange={handleImageUpload}
                      className="hidden"
                      id="image-upload"
                    />
                    <label htmlFor="image-upload" className="cursor-pointer block">
                      <div className="space-y-4">
                        <div className="bg-green-100 p-4 rounded-full w-16 h-16 mx-auto flex items-center justify-center">
                          <ImageIcon className="h-8 w-8 text-green-600" />
                        </div>
                        <div>
                          <h3 className="text-lg font-semibold text-gray-900 mb-2">
                            Upload a food image
                          </h3>
                          <p className="text-gray-600 mb-4">
                            JPG, PNG or HEIC up to 10MB
                          </p>
                          <div className="inline-flex items-center justify-center px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-medium rounded-md transition-colors">
                            <Upload className="h-4 w-4 mr-2" />
                            Choose File
                          </div>
                        </div>
                      </div>
                    </label>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div className="relative aspect-video rounded-lg overflow-hidden bg-gray-100">
                      <Image
                        src={uploadedImage}
                        alt="Uploaded food"
                        fill
                        className="object-cover"
                        sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                      />
                    </div>
                    <div className="flex gap-2">
                      <Button
                        onClick={analyzeImage}
                        disabled={isAnalyzing}
                        className="flex-1 bg-green-600 hover:bg-green-700"
                      >
                        {isAnalyzing ? (
                          <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                            Detecting Food...
                          </>
                        ) : (
                          <>
                            <Zap className="h-4 w-4 mr-2" />
                            Detect Food Items
                          </>
                        )}
                      </Button>
                      <label htmlFor="image-upload" className="cursor-pointer">
                        <div className="inline-flex items-center justify-center px-4 py-2 border border-gray-300 bg-white hover:bg-gray-50 text-gray-700 font-medium rounded-md transition-colors">
                          <Upload className="h-4 w-4 mr-2" />
                          New Photo
                        </div>
                      </label>
                    </div>
                  </div>
                )}

                {/* Error/Success Messages */}
                {error && (
                  <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg flex items-center gap-2 text-red-700">
                    <AlertCircle className="h-4 w-4" />
                    <span className="text-sm">{error}</span>
                  </div>
                )}

                {success && (
                  <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg flex items-center gap-2 text-green-700">
                    <Check className="h-4 w-4" />
                    <span className="text-sm">{success}</span>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Tips for Food Detection */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Info className="h-5 w-5 text-blue-600" />
                  Tips for Better Detection
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 text-sm">
                  <div className="flex items-start gap-3">
                    <div className="bg-green-100 p-1 rounded-full">
                      <span className="text-green-600 text-xs">📸</span>
                    </div>
                    <p className="text-gray-700">Take photos in good lighting for better accuracy</p>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="bg-blue-100 p-1 rounded-full">
                      <span className="text-blue-600 text-xs">🍽️</span>
                    </div>
                    <p className="text-gray-700">Include the entire plate for complete analysis</p>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="bg-orange-100 p-1 rounded-full">
                      <span className="text-orange-600 text-xs">📏</span>
                    </div>
                    <p className="text-gray-700">Place a reference object (coin, utensil) for scale</p>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="bg-purple-100 p-1 rounded-full">
                      <span className="text-purple-600 text-xs">🎯</span>
                    </div>
                    <p className="text-gray-700">Capture from directly above for best perspective</p>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="bg-red-100 p-1 rounded-full">
                      <span className="text-red-600 text-xs">🥗</span>
                    </div>
                    <p className="text-gray-700">Separate overlapping foods for better identification</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Results Section */}
          <div className="space-y-6">
            {analysisComplete && detectedItems.length > 0 ? (
              <>
                {/* Detected Items */}
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                      <span>Detected Food Items</span>
                      <span className="text-sm font-normal text-gray-500">
                        {selectedCount} of {detectedItems.length} selected
                      </span>
                    </CardTitle>
                    <CardDescription>
                      Select items to add to your inventory and adjust weights if needed
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-3">
                      {detectedItems.map((item, index) => (
                        <div 
                          key={index} 
                          className={`p-3 border rounded-lg transition-all ${
                            item.selected 
                              ? 'bg-green-50 border-green-200' 
                              : 'bg-gray-50 border-gray-200'
                          }`}
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-3">
                              <input
                                type="checkbox"
                                checked={item.selected}
                                onChange={() => toggleItemSelection(index)}
                                className="h-4 w-4 text-green-600 rounded border-gray-300 focus:ring-green-500"
                              />
                              <div>
                                <div className="font-medium text-gray-900">{item.name}</div>
                                <div className="text-sm text-gray-500">{item.category}</div>
                                <div className="text-xs text-gray-400">
                                  Confidence: {(item.confidence * 100).toFixed(0)}%
                                </div>
                              </div>
                            </div>
                            <div className="flex items-center gap-2">
                              {editingIndex === index ? (
                                <div className="flex items-center gap-2">
                                  <Input
                                    value={editWeight}
                                    onChange={(e) => setEditWeight(e.target.value)}
                                    className="w-20 h-8 text-sm"
                                    placeholder="100g"
                                  />
                                  <Button
                                    size="sm"
                                    onClick={() => saveWeight(index)}
                                    className="h-8 w-8 p-0"
                                  >
                                    <Save className="h-3 w-3" />
                                  </Button>
                                  <Button
                                    size="sm"
                                    variant="outline"
                                    onClick={cancelEdit}
                                    className="h-8 w-8 p-0"
                                  >
                                    <X className="h-3 w-3" />
                                  </Button>
                                </div>
                              ) : (
                                <div className="flex items-center gap-2">
                                  <span className="text-sm font-medium text-gray-700">
                                    {item.estimatedWeight}
                                  </span>
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => startEditingWeight(index)}
                                    className="h-8 w-8 p-0"
                                  >
                                    <Edit3 className="h-3 w-3" />
                                  </Button>
                                </div>
                              )}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* Save Actions */}
                <Card>
                  <CardHeader>
                    <CardTitle>Save to Inventory</CardTitle>
                    <CardDescription>
                      Add selected items to your food inventory
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <div className="text-sm text-gray-600">
                      {selectedCount} item{selectedCount !== 1 ? 's' : ''} selected to save
                    </div>
                    <Button 
                      onClick={saveSelectedItems}
                      disabled={selectedCount === 0 || isSaving}
                      className="w-full bg-green-600 hover:bg-green-700"
                    >
                      {isSaving ? (
                        <>
                          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                          Saving Items...
                        </>
                      ) : (
                        <>
                          <Save className="h-4 w-4 mr-2" />
                          Save Selected Items
                        </>
                      )}
                    </Button>
                  </CardContent>
                </Card>
              </>
            ) : (
              <Card>
                <CardContent className="p-8 text-center">
                  <div className="bg-gray-100 p-8 rounded-full w-24 h-24 mx-auto mb-4 flex items-center justify-center">
                    <Zap className="h-12 w-12 text-gray-400" />
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Ready to Detect Food
                  </h3>
                  <p className="text-gray-600">
                    Upload a photo of your food to detect ingredients automatically
                  </p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>

      <ChatBot />
    </div>
  );
}
