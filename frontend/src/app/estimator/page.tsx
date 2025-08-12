'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Upload, Camera, Image as ImageIcon, Zap, TrendingUp, Info } from 'lucide-react';
import { mockNutritionData } from '@/lib/mock-data';
import { Navigation } from '@/components/Navigation';
import { ChatBot } from '@/components/ChatBot';

export default function CalorieEstimator() {
  const [uploadedImage, setUploadedImage] = useState<string | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisComplete, setAnalysisComplete] = useState(false);
  const [nutritionData, setNutritionData] = useState(mockNutritionData);

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setUploadedImage(e.target?.result as string);
        setAnalysisComplete(false);
      };
      reader.readAsDataURL(file);
    }
  };

  const analyzeImage = () => {
    setIsAnalyzing(true);
    // Simulate analysis delay
    setTimeout(() => {
      setIsAnalyzing(false);
      setAnalysisComplete(true);
      // Mock different nutrition data for demonstration
      setNutritionData({
        calories: 487 + Math.floor(Math.random() * 200) - 100,
        protein: 28 + Math.floor(Math.random() * 10) - 5,
        carbs: 45 + Math.floor(Math.random() * 20) - 10,
        fat: 22 + Math.floor(Math.random() * 10) - 5,
        fiber: 8 + Math.floor(Math.random() * 5) - 2,
        sugar: 12 + Math.floor(Math.random() * 8) - 4,
      });
    }, 3000);
  };

  const totalMacros = nutritionData.protein + nutritionData.carbs + nutritionData.fat;
  const proteinPercentage = (nutritionData.protein / totalMacros) * 100;
  const carbsPercentage = (nutritionData.carbs / totalMacros) * 100;
  const fatPercentage = (nutritionData.fat / totalMacros) * 100;

  const nutritionMetrics = [
    {
      label: 'Protein',
      value: nutritionData.protein,
      unit: 'g',
      percentage: proteinPercentage,
      color: 'bg-blue-500',
      target: 30,
    },
    {
      label: 'Carbs',
      value: nutritionData.carbs,
      unit: 'g',
      percentage: carbsPercentage,
      color: 'bg-green-500',
      target: 50,
    },
    {
      label: 'Fat',
      value: nutritionData.fat,
      unit: 'g',
      percentage: fatPercentage,
      color: 'bg-orange-500',
      target: 25,
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      
      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-3xl font-bold text-gray-900">Calorie Estimator</h1>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Upload a photo of your meal and get instant nutrition analysis powered by AI. Track your intake effortlessly!
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Upload Section */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Camera className="h-5 w-5 text-green-600" />
                  Upload Your Meal
                </CardTitle>
                <CardDescription>
                  Take a photo or upload an image of your meal for instant nutrition analysis
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
                    <label htmlFor="image-upload" className="cursor-pointer">
                      <div className="space-y-4">
                        <div className="bg-green-100 p-4 rounded-full w-16 h-16 mx-auto flex items-center justify-center">
                          <ImageIcon className="h-8 w-8 text-green-600" />
                        </div>
                        <div>
                          <h3 className="text-lg font-semibold text-gray-900 mb-2">
                            Upload a meal photo
                          </h3>
                          <p className="text-gray-600 mb-4">
                            JPG, PNG or HEIC up to 10MB
                          </p>
                          <Button className="bg-green-600 hover:bg-green-700">
                            <Upload className="h-4 w-4 mr-2" />
                            Choose File
                          </Button>
                        </div>
                      </div>
                    </label>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div className="relative aspect-video rounded-lg overflow-hidden bg-gray-100">
                      <Image
                        src={uploadedImage}
                        alt="Uploaded meal"
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
                            Analyzing...
                          </>
                        ) : (
                          <>
                            <Zap className="h-4 w-4 mr-2" />
                            Analyze Nutrition
                          </>
                        )}
                      </Button>
                      <label htmlFor="image-upload">
                        <Button variant="outline" className="cursor-pointer">
                          <Upload className="h-4 w-4 mr-2" />
                          New Photo
                        </Button>
                      </label>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Tips */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Info className="h-5 w-5 text-blue-600" />
                  Tips for Better Results
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
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Results Section */}
          <div className="space-y-6">
            {analysisComplete ? (
              <>
                {/* Calorie Summary */}
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <TrendingUp className="h-5 w-5 text-green-600" />
                      Nutrition Analysis
                    </CardTitle>
                    <CardDescription>
                      Estimated nutrition breakdown for your meal
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="text-center mb-6">
                      <div className="text-4xl font-bold text-green-600 mb-2">
                        {nutritionData.calories}
                      </div>
                      <div className="text-lg text-gray-600">calories</div>
                    </div>

                    <div className="space-y-4">
                      {nutritionMetrics.map((metric, index) => (
                        <div key={index} className="space-y-2">
                          <div className="flex justify-between items-center">
                            <span className="font-medium text-gray-700">{metric.label}</span>
                            <span className="font-semibold">
                              {metric.value}{metric.unit} ({metric.percentage.toFixed(0)}%)
                            </span>
                          </div>
                          <div className="relative">
                            <Progress 
                              value={(metric.value / metric.target) * 100} 
                              className="h-2"
                            />
                            <div 
                              className={`absolute top-0 left-0 h-2 rounded-full ${metric.color}`}
                              style={{ width: `${Math.min((metric.value / metric.target) * 100, 100)}%` }}
                            ></div>
                          </div>
                          <div className="text-xs text-gray-500">
                            Target: {metric.target}{metric.unit}
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {/* Detailed Breakdown */}
                <Card>
                  <CardHeader>
                    <CardTitle>Detailed Breakdown</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="grid grid-cols-2 gap-4">
                      <div className="space-y-4">
                        <div className="flex justify-between">
                          <span className="text-gray-600">Fiber</span>
                          <span className="font-semibold">{nutritionData.fiber}g</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Sugar</span>
                          <span className="font-semibold">{nutritionData.sugar}g</span>
                        </div>
                      </div>
                      <div className="space-y-4">
                        <div className="flex justify-between">
                          <span className="text-gray-600">Sodium</span>
                          <span className="font-semibold">~580mg</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Cholesterol</span>
                          <span className="font-semibold">~45mg</span>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                {/* Actions */}
                <Card>
                  <CardHeader>
                    <CardTitle>Save & Track</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <Button className="w-full bg-green-600 hover:bg-green-700">
                      Add to Food Log
                    </Button>
                    <Button variant="outline" className="w-full">
                      Save to Favorites
                    </Button>
                    <Button variant="outline" className="w-full">
                      Share Analysis
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
                    Ready to Analyze
                  </h3>
                  <p className="text-gray-600">
                    Upload a photo of your meal to get instant nutrition analysis
                  </p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>

        {/* Recent Analyses */}
        {analysisComplete && (
          <Card>
            <CardHeader>
              <CardTitle>Recent Analyses</CardTitle>
              <CardDescription>Your nutrition tracking history</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {[
                  { name: 'Grilled Salmon Salad', calories: 420, time: '2 hours ago' },
                  { name: 'Quinoa Buddha Bowl', calories: 380, time: 'Yesterday' },
                  { name: 'Chicken Stir Fry', calories: 520, time: '2 days ago' },
                ].map((item, index) => (
                  <div key={index} className="p-4 border rounded-lg hover:bg-gray-50 transition-colors">
                    <h4 className="font-medium text-gray-900 mb-1">{item.name}</h4>
                    <div className="text-sm text-gray-600">{item.calories} calories</div>
                    <div className="text-xs text-gray-500 mt-1">{item.time}</div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>

      <ChatBot />
    </div>
  );
}
