# NourishCare - AI-Powered Nutrition Management System

## Overview
NourishCare is a comprehensive nutrition management system built with a microservices architecture. It uses Google Cloud Vision API to detect food items from images and provides nutritional analysis, meal planning, and recipe recommendations.

## Features
- 🔍 **AI Food Detection**: Upload images to identify food items using Google Cloud Vision API
- 📊 **Nutritional Analysis**: Get detailed nutritional information for detected foods
- 🍽️ **Meal Planning**: Create personalized meal plans based on dietary goals
- 📖 **Recipe Recommendations**: Discover recipes using Spoonacular API
- 📱 **Responsive Design**: Works on desktop and mobile devices
- 🏗️ **Microservices Architecture**: Scalable, maintainable, and independently deployable services

## Architecture
NourishCare follows a microservices architecture pattern with the following services:

### Infrastructure Services
- **API Gateway** (Port 8080): Entry point for all client requests with routing, load balancing, and CORS handling
- **Service Discovery** (Port 8761): Eureka server for service registration and discovery
- **Configuration Server** (Port 8888): Centralized configuration management

### Business Services
- **Vision Service** (Port 8081): AI-powered food detection using Google Cloud Vision API
- **Recipe Service** (Port 8082): Recipe management and Spoonacular API integration  
- **Inventory Service** (Port 8083): Food inventory and expiration tracking
- **Meal Planning Service** (Port 8084): Meal planning and shopping list generation
- **User Service** (Port 8085): User management and authentication

## Tech Stack
- **Frontend**: Next.js 15, TypeScript, Tailwind CSS
- **Microservices**: Spring Boot 2.7, Spring Cloud
- **Database**: MongoDB (separate database per service)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Containerization**: Docker & Docker Compose
- **AI Services**: Google Cloud Vision API
- **APIs**: Spoonacular API for recipes

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ and npm (for frontend)
- Java 8+ and Maven (for local development)
- MongoDB (included in Docker setup)
- Google Cloud Platform account with Vision API enabled
- Spoonacular API key

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/muhaimeen90/NourishCare.git
   cd NourishCare
   ```

2. **Setup Microservices Environment**
   ```bash
   cd microservices
   cp .env.example .env
   # Edit .env with your actual API keys and configuration
   ```

3. **Start All Services**
   ```bash
   # Build all microservices
   ./build-all.sh  # or build-all.bat on Windows
   
   # Start with Docker Compose
   docker-compose up -d
   
   # Check all services are running
   docker-compose ps
   ```

4. **Setup Frontend**
   ```bash
   cd ../frontend
   npm install
   npm run dev
   ```

5. **Access the Application**
   - **Frontend**: http://localhost:3000
   - **API Gateway**: http://localhost:8080
   - **Service Discovery**: http://localhost:8761
   - **Individual Services**: Ports 8081-8085

### Development Setup

For local development without Docker:

1. **Start Infrastructure Services**
   ```bash
   cd microservices
   docker-compose up -d mongodb eureka-server config-server
   ```

2. **Run Services Locally**
   ```bash
   # Terminal 1 - Vision Service
   cd vision-service && mvn spring-boot:run
   
   # Terminal 2 - Recipe Service  
   cd recipe-service && mvn spring-boot:run
   
   # And so on for other services...
   ```

### Environment Configuration

Edit `microservices/.env` with your credentials:

```env
# MongoDB
MONGODB_URI=mongodb://localhost:27017

# Google Cloud Vision API
GOOGLE_CLOUD_PROJECT_ID=your-project-id
GOOGLE_APPLICATION_CREDENTIALS=classpath:credentials.json
VISION_API_MOCK=true  # Set to false for production

# Spoonacular API  
SPOONACULAR_API_KEY=your-spoonacular-api-key
SPOONACULAR_API_MOCK=true  # Set to false for production

# CORS Configuration
CORS_ORIGINS=http://localhost:3000
```

### Google Cloud Vision API Setup

1. **Create a Google Cloud Project**
2. **Enable the Vision API**
3. **Create a Service Account**
4. **Download the JSON credentials file**
5. **Place the file in `microservices/credentials/` directory**

### MongoDB Setup

MongoDB is automatically configured with Docker Compose. Each microservice uses its own database:
- `nourishcare_vision` - Vision service data
- `nourishcare_recipes` - Recipe data
- `nourishcare_inventory` - Food inventory data  
- `nourishcare_meal_planning` - Meal plans and shopping lists
- `nourishcare_users` - User accounts and preferences

## Microservices Architecture

For detailed information about the microservices architecture, see [Microservices Documentation](microservices/MICROSERVICES_ARCHITECTURE.md).

### Service Communication
- **Synchronous**: HTTP REST calls through API Gateway
- **Service Discovery**: Netflix Eureka for service registration
- **Configuration**: Centralized configuration management
- **Load Balancing**: Client-side load balancing via Eureka
- **Circuit Breaker**: Resilience4j for fault tolerance

### Database Strategy
- **Database per Service**: Each microservice has its own MongoDB database
- **Data Consistency**: Eventual consistency model
- **Independent Scaling**: Services can be scaled independently

## Monitoring and Health Checks

### Service Health
- **Individual Services**: `http://localhost:{port}/actuator/health`
- **API Gateway**: `http://localhost:8080/actuator/health`
- **Service Discovery Dashboard**: `http://localhost:8761`

### Logs
```bash
# View all service logs
docker-compose logs -f

# View specific service logs  
docker-compose logs -f vision-service
```

## Development Workflow

### Adding New Features
1. Identify the appropriate microservice
2. Make changes to the specific service
3. Test the service independently
4. Update API contracts if needed
5. Test integration through API Gateway

### Testing
- **Unit Tests**: `mvn test` in each service directory
- **Integration Tests**: Test service-to-service communication
- **End-to-End Tests**: Test complete workflows through frontend

## Troubleshooting

### Common Issues
1. **Services not registering with Eureka**
   - Ensure Eureka server is running first
   - Check network connectivity between services

2. **Database connection issues**  
   - Verify MongoDB is running
   - Check database connection strings

3. **API Gateway routing issues**
   - Ensure target services are registered in Eureka
   - Check gateway route configuration

### Service Startup Order
1. MongoDB
2. Eureka Server  
3. Config Server
4. Business Services (Vision, Recipe, Inventory, Meal Planning, User)
5. API Gateway

## API Endpoints

All API requests go through the API Gateway at `http://localhost:8080`

### Vision API
- `POST /api/vision/detect-food-items` - Upload image to detect food items
- `GET /api/vision/detections` - Get all food detections
- `GET /api/vision/detections/{id}` - Get detection by ID
- `GET /api/vision/stats` - Get detection statistics

### Recipe API
- `GET /api/recipes` - Get all recipes
- `GET /api/recipes/{id}` - Get recipe by ID
- `GET /api/recipes/search?title=...` - Search recipes by title
- `POST /api/recipes` - Create new recipe
- `PUT /api/recipes/{id}` - Update recipe
- `DELETE /api/recipes/{id}` - Delete recipe

### Inventory API
- `GET /api/inventory` - Get all food items
- `GET /api/inventory/{id}` - Get food item by ID
- `POST /api/inventory` - Add new food item
- `PUT /api/inventory/{id}` - Update food item
- `DELETE /api/inventory/{id}` - Remove food item
- `GET /api/inventory/expiring-soon` - Get items expiring soon

### Meal Planning API
- `GET /api/meal-plans` - Get all meal plans
- `GET /api/meal-plans/{id}` - Get meal plan by ID
- `POST /api/meal-plans` - Create meal plan
- `GET /api/meal-plans/week?startDate=...` - Get weekly meal plan

### User API
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure no sensitive data is committed
5. Submit a pull request

## License

This project is licensed under the MIT License.
