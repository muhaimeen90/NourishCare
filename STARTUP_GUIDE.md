# NourishCare Microservices Startup Guide

## Prerequisites
- Java 11 or higher
- Node.js 14+ and npm
- MongoDB running on localhost:27017
- Maven 3.6+

## Services Overview
The NourishCare application consists of:
- **Infrastructure Services**: Eureka Server, Config Server, API Gateway
- **Business Services**: Vision Service, Recipe Service, Inventory Service, Meal Planning Service, User Service
- **Frontend**: Next.js React application

## Quick Start - Automated

### 1. Start All Backend Services (Windows)
```powershell
# Run this from the NourishCare root directory
.\scripts\start-all-services.ps1
```

### 2. Start Frontend
```powershell
cd frontend
npm install
npm run dev
```

## Manual Startup Order

### Step 1: Start Infrastructure Services (Required Order)

#### 1.1 Start Config Server (Port 8888)
```powershell
cd microservices\config-server
mvn spring-boot:run
```
Wait for: `Started ConfigServerApplication in X seconds`

#### 1.2 Start Eureka Server (Port 8761)
```powershell
cd microservices\eureka-server
mvn spring-boot:run
```
Wait for: `Started EurekaServerApplication in X seconds`
Check: http://localhost:8761 (Eureka Dashboard)

#### 1.3 Start API Gateway (Port 8080)
```powershell
cd microservices\api-gateway
mvn spring-boot:run
```
Wait for: `Started ApiGatewayApplication in X seconds`

### Step 2: Start Business Services (Any Order)

#### 2.1 Vision Service (Port 8081)
```powershell
cd microservices\vision-service
mvn spring-boot:run
```

#### 2.2 Recipe Service (Port 8082)
```powershell
cd microservices\recipe-service
mvn spring-boot:run
```

#### 2.3 Inventory Service (Port 8083)
```powershell
cd microservices\inventory-service
mvn spring-boot:run
```

#### 2.4 Meal Planning Service (Port 8085)
```powershell
cd microservices\meal-planning-service
mvn spring-boot:run
```

#### 2.5 User Service (Port 8086)
```powershell
cd microservices\user-service
mvn spring-boot:run
```

### Step 3: Start Frontend (Port 3000)
```powershell
cd frontend
npm install
npm run dev
```

## Service URLs and Health Checks

### Infrastructure Services
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888/actuator/health
- **API Gateway**: http://localhost:8080/actuator/health

### Business Services (via API Gateway)
- **Vision Service**: http://localhost:8080/vision-service/api/vision/health
- **Recipe Service**: http://localhost:8080/recipe-service/api/recipes/health
- **Inventory Service**: http://localhost:8080/inventory-service/api/inventory/health
- **Meal Planning Service**: http://localhost:8080/meal-planning-service/api/meal-plans/health
- **User Service**: http://localhost:8080/user-service/api/users/health

### Frontend
- **Main Application**: http://localhost:3000

## Direct Service URLs (for development)
- **Vision Service**: http://localhost:8081/api/vision/health
- **Recipe Service**: http://localhost:8082/api/recipes/health
- **Inventory Service**: http://localhost:8083/api/inventory/health
- **Meal Planning Service**: http://localhost:8085/api/meal-plans/health
- **User Service**: http://localhost:8086/api/users/health

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```powershell
   # Find process using port (e.g., 8080)
   netstat -ano | findstr :8080
   # Kill process by PID
   taskkill /PID <PID> /F
   ```

2. **MongoDB Not Running**
   ```powershell
   # Start MongoDB service
   net start MongoDB
   # Or if using MongoDB Community Edition
   mongod --dbpath "C:\data\db"
   ```

3. **Service Registration Issues**
   - Ensure Eureka Server starts before other services
   - Check Eureka dashboard for registered services
   - Verify service application.properties configurations

4. **Config Server Issues**
   - Ensure Config Server starts first
   - Check config server logs for configuration loading
   - Verify git repository access if using git backend

### Service Startup Verification

Check all services are registered:
1. Open Eureka Dashboard: http://localhost:8761
2. Verify all services appear in "Instances currently registered with Eureka"
3. Services should show: API-GATEWAY, VISION-SERVICE, RECIPE-SERVICE, INVENTORY-SERVICE, MEAL-PLANNING-SERVICE, USER-SERVICE

### Frontend Configuration
The frontend is configured to route API calls through the API Gateway at http://localhost:8080

## Development Tips

1. **Hot Reload**: Use `mvn spring-boot:run` for automatic restart on code changes
2. **Debug Mode**: Add `-Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"` for debugging
3. **Profile Selection**: Add `-Dspring.profiles.active=dev` for development profiles
4. **Logging**: Check logs in each service terminal for troubleshooting

## API Testing
You can test the APIs using:
- **Postman**: Import the API collections (if available)
- **Curl**: Test individual endpoints
- **Browser**: For GET endpoints like health checks
- **Frontend**: The React application will consume all APIs

## Database Setup
Each service uses its own MongoDB database:
- vision_db
- recipe_db  
- inventory_db
- meal_planning_db
- user_db

MongoDB will automatically create these databases when the services start.
