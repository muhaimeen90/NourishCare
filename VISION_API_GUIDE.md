# Google Vision API Food Detection Feature

## Overview
The NourishCare application now includes AI-powered food detection using Google Cloud Vision API. Users can upload images of their meals, and the system will automatically detect food ingredients and estimate their weights.

## Features

### 🖼️ Image Upload
- Support for JPG, PNG, and HEIC image formats
- Maximum file size: 10MB
- Drag-and-drop or click to upload interface

### 🤖 AI Food Detection
- Powered by Google Cloud Vision API
- Detects multiple food items in a single image
- Provides confidence scores for each detection
- Estimates weights based on common food portions

### ✅ Smart Selection Interface
- Checkbox-based selection for detected items
- Edit weight estimates if needed
- Category-based organization (Fruits, Vegetables, Meat, etc.)
- Confidence indicators for each detection

### 💾 Inventory Integration
- Save selected items directly to your food inventory
- Automatic expiration date assignment (7 days default)
- Real-time updates to your dashboard

## How to Use

### Step 1: Upload an Image
1. Navigate to the "AI Food Detection" page (Estimator)
2. Click "Choose File" or drag an image into the upload area
3. Select a clear photo of your food items

### Step 2: Detect Food Items
1. Click "Detect Food Items" button
2. Wait for the AI analysis to complete (usually 2-5 seconds)
3. Review the detected items list

### Step 3: Review and Edit
1. Check/uncheck items you want to save
2. Click the edit icon (📝) next to any weight to modify it
3. Verify the food categories are correct

### Step 4: Save to Inventory
1. Click "Save Selected Items"
2. Selected items will be added to your food inventory
3. Access them later from the Dashboard

## Tips for Better Results

### 📸 Photography Tips
- **Good Lighting**: Take photos in bright, natural lighting
- **Clear View**: Ensure all food items are clearly visible
- **Overhead Angle**: Capture from directly above for best perspective
- **Reference Objects**: Include utensils or coins for scale
- **Separate Items**: Avoid overlapping foods when possible

### 🎯 Best Practices
- Use high-resolution images when possible
- Avoid blurry or motion-blurred photos
- Include the entire plate or bowl in the frame
- Remove packaging or wrappers from food items
- Ensure good contrast between food and background

## Supported Food Categories

The system can detect and categorize foods into:
- **Fruits**: Apples, bananas, oranges, berries, etc.
- **Vegetables**: Carrots, broccoli, tomatoes, lettuce, etc.
- **Meat**: Chicken, beef, pork, fish, etc.
- **Seafood**: Salmon, fish, shellfish, etc.
- **Grains**: Rice, bread, pasta, cereals, etc.
- **Dairy**: Cheese, milk, eggs, yogurt, etc.
- **Other**: Mixed dishes and unclassified items

## Technical Details

### Backend Implementation
- **Google Cloud Vision API**: Label detection for food identification
- **Spring Boot Controller**: `/api/vision/detect-food` endpoint
- **MongoDB Integration**: Automatic saving to food_items collection
- **File Upload**: MultipartFile handling with validation

### Frontend Implementation
- **React/Next.js**: Modern file upload interface
- **FormData API**: Proper image file transmission
- **Real-time UI**: Loading states and error handling
- **TypeScript**: Type-safe API integration

### API Endpoints
- `POST /api/vision/detect-food`: Upload image and detect food items
- `POST /api/vision/save-selected-items`: Save selected items to inventory
- `GET /api/vision/health`: Health check endpoint

## Error Handling

The system includes comprehensive error handling for:
- Invalid file formats
- Network connectivity issues
- Google Vision API errors
- Database connection problems
- File size limitations

## Future Enhancements

Planned improvements include:
- Nutrition information extraction
- Portion size detection using reference objects
- Custom food training for better accuracy
- Batch processing for multiple images
- Recipe suggestions based on detected ingredients

## Security & Privacy

- Images are processed temporarily and not stored permanently
- Google Vision API processes images securely
- No personal data is transmitted with images
- All API calls use HTTPS encryption

---

*This feature requires an active internet connection and Google Cloud Vision API access.*
