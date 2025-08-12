# NourishCare - AI-Powered Nutrition Management System

## Overview
NourishCare is a comprehensive nutrition management system that uses Google Cloud Vision API to detect food items from images and provides nutritional analysis, meal planning, and recipe recommendations.

## Features
- 🔍 **AI Food Detection**: Upload images to identify food items using Google Cloud Vision API
- 📊 **Nutritional Analysis**: Get detailed nutritional information for detected foods
- 🍽️ **Meal Planning**: Create personalized meal plans based on dietary goals
- 📖 **Recipe Recommendations**: Discover recipes using Spoonacular API
- 📱 **Responsive Design**: Works on desktop and mobile devices

## Tech Stack
- **Frontend**: Next.js 15, TypeScript, Tailwind CSS
- **Backend**: Spring Boot 2.7, Java
- **Database**: MongoDB Atlas
- **AI Services**: Google Cloud Vision API
- **APIs**: Spoonacular API for recipes

## Getting Started

### Prerequisites
- Node.js 18+ and npm
- Java 8+ and Maven
- MongoDB Atlas account
- Google Cloud Platform account with Vision API enabled
- Spoonacular API key

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/muhaimeen90/NourishCare.git
   cd NourishCare
   ```

2. **Backend Setup**
   
   a. **Environment Variables**: Copy the environment example file
   ```bash
   cp backend/.env.example backend/.env
   ```
   
   b. **Configure Environment Variables**: Edit `backend/.env` with your actual credentials:
   ```env
   MONGODB_URI=your-mongodb-connection-string
   GOOGLE_CLOUD_PROJECT_ID=your-google-cloud-project-id
   GOOGLE_APPLICATION_CREDENTIALS=classpath:your-service-account.json
   SPOONACULAR_API_KEY=your-spoonacular-api-key
   VISION_API_MOCK=false
   CORS_ORIGINS=http://localhost:3000
   ```

   c. **Google Cloud Service Account**: 
   - Download your service account JSON file from Google Cloud Console
   - Place it in `backend/src/main/resources/`
   - Update the `GOOGLE_APPLICATION_CREDENTIALS` path in `.env`

   d. **Build and Run**:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. **Access the Application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8081

### Google Cloud Vision API Setup

1. **Create a Google Cloud Project**
2. **Enable the Vision API**
3. **Create a Service Account**
4. **Download the JSON credentials file**
5. **Place the file in `backend/src/main/resources/`**

### MongoDB Atlas Setup

1. **Create a MongoDB Atlas cluster**
2. **Create a database user**
3. **Get the connection string**
4. **Add it to your `.env` file**

## Security Notes

⚠️ **Important**: Never commit sensitive files to Git:
- Service account JSON files are in `.gitignore`
- Environment variables are in `.gitignore`
- Use environment variables for all sensitive data

## API Endpoints

### Vision API
- `POST /api/vision/detect-food-items` - Upload image to detect food items

### Food Items
- `GET /api/food-items` - Get all food items
- `POST /api/food-items` - Create new food item

### Meal Plans
- `GET /api/meal-plans` - Get meal plans
- `POST /api/meal-plans` - Create meal plan

### Recipes
- `GET /api/recipes` - Get recipes
- `GET /api/recipes/search` - Search recipes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure no sensitive data is committed
5. Submit a pull request

## License

This project is licensed under the MIT License.
