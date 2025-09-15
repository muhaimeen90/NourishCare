# NourishCare Microservices

This directory contains all the microservices for the NourishCare application.

## Services Overview

### Infrastructure Services
- **service-discovery** (Port 8761): Eureka Server for service registration and discovery
- **config-server** (Port 8888): Centralized configuration management
- **api-gateway** (Port 8080): Entry point for all client requests

### Business Services
- **vision-service** (Port 8081): AI-powered food detection using Google Cloud Vision API
- **recipe-service** (Port 8082): Recipe management and Spoonacular API integration
- **inventory-service** (Port 8083): Food inventory and expiration tracking
- **meal-planning-service** (Port 8084): Meal planning and shopping list generation
- **user-service** (Port 8085): User management and authentication

## Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 8761, 8888, 8080-8085, 27017 available

### 1. Environment Setup
Copy the environment template:
```bash
cp .env.example .env
```

Edit `.env` with your actual credentials:
```env
# MongoDB
MONGODB_URI=mongodb://mongodb:27017

# Google Cloud Vision API
GOOGLE_CLOUD_PROJECT_ID=your-project-id
GOOGLE_APPLICATION_CREDENTIALS=classpath:credentials.json
VISION_API_MOCK=true

# Spoonacular API
SPOONACULAR_API_KEY=your-api-key
SPOONACULAR_API_MOCK=true

# CORS
CORS_ORIGINS=http://localhost:3000
```

### 2. Start All Services
```bash
# Start infrastructure services first
docker-compose up -d mongodb eureka-server config-server

# Wait for infrastructure to be ready (30 seconds)
sleep 30

# Start business services
docker-compose up -d

# Check all services are running
docker-compose ps
```

### 3. Verify Services
- **Service Discovery**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Vision Service**: http://localhost:8080/api/vision/health
- **Recipe Service**: http://localhost:8080/api/recipes/health

## Development

### Building Services
```bash
# Build all services
./build-all.sh

# Build specific service
cd vision-service && mvn clean package
```

### Running Individual Services
```bash
# Start infrastructure first
docker-compose up -d mongodb eureka-server config-server

# Run service locally
cd vision-service
mvn spring-boot:run
```

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

### Vision API
- `POST /api/vision/detect-food-items` - Upload image for food detection
- `GET /api/vision/detections` - Get all food detections
- `GET /api/vision/stats` - Get service statistics

### Recipe API
- `GET /api/recipes` - Get all recipes
- `GET /api/recipes/{id}` - Get recipe by ID
- `GET /api/recipes/search?title=...` - Search recipes
- `POST /api/recipes` - Create new recipe

### Inventory API
- `GET /api/inventory` - Get all food items
- `POST /api/inventory` - Add new food item
- `GET /api/inventory/expiring-soon` - Get expiring items

### Meal Planning API
- `GET /api/meal-plans` - Get meal plans
- `POST /api/meal-plans` - Create meal plan
- `GET /api/meal-plans/week?startDate=...` - Get weekly meal plan

## Monitoring

### Health Checks
- Individual service health: `http://localhost:{port}/actuator/health`
- Gateway health: `http://localhost:8080/actuator/health`
- Eureka dashboard: `http://localhost:8761`

### Logs
```bash
# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f vision-service
```

## Troubleshooting

### Common Issues

1. **Services not registering with Eureka**
   - Ensure Eureka server is running first
   - Check service logs for connection errors
   - Verify network connectivity between containers

2. **Database connection issues**
   - Ensure MongoDB container is running
   - Check MongoDB logs: `docker-compose logs mongodb`
   - Verify database URIs in configuration

3. **API Gateway not routing requests**
   - Check gateway logs for routing errors
   - Ensure target services are registered in Eureka
   - Verify route configuration in gateway properties

### Service Dependencies

Start services in this order:
1. MongoDB
2. Eureka Server
3. Config Server
4. Business Services (Vision, Recipe, Inventory, etc.)
5. API Gateway

### Performance Tuning

For development:
- Reduce Eureka lease renewal interval
- Use mock services for external APIs
- Limit log levels to INFO or WARN

For production:
- Increase JVM heap size
- Configure connection pooling
- Enable caching where appropriate

## Security

### Development Environment
- CORS enabled for frontend development
- Mock services for external APIs
- Simplified authentication

### Production Considerations
- Enable HTTPS
- Configure proper authentication
- Use production database credentials
- Enable security headers
- Regular security updates

## Next Steps

1. **Frontend Integration**: Update frontend to use API Gateway
2. **Authentication**: Implement JWT-based authentication
3. **Monitoring**: Add comprehensive logging and metrics
4. **Testing**: Create integration test suite
5. **CI/CD**: Automate build and deployment pipeline
