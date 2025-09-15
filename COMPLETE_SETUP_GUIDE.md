# 🚀 NourishCare Microservices - Complete Setup & Run Guide

## 📋 Prerequisites Setup

### 1. Install Required Software

#### Java 11+
```powershell
# Check if Java is installed
java -version

# If not installed, download from: https://adoptium.net/
# Or install via Chocolatey:
choco install openjdk11
```

#### Maven 3.6+
```powershell
# Check if Maven is installed
mvn -version

# If not installed, download from: https://maven.apache.org/download.cgi
# Or install via Chocolatey:
choco install maven
```

#### Node.js 14+ and npm
```powershell
# Check if Node.js is installed
node --version
npm --version

# If not installed, download from: https://nodejs.org/
# Or install via Chocolatey:
choco install nodejs
```

#### MongoDB
```powershell
# Check if MongoDB is running
mongo --eval "db.runCommand({connectionStatus : 1})"

# If not installed, download from: https://www.mongodb.com/try/download/community
# Or install via Chocolatey:
choco install mongodb

# Start MongoDB service:
net start MongoDB
```

---

## 🏗️ Build All Services

### Option 1: Manual Build (Recommended First Time)
```powershell
# Navigate to NourishCare directory
cd E:\NourishCare

# Build each service individually
cd microservices\config-server
mvn clean install
cd ..\..

cd microservices\eureka-server  
mvn clean install
cd ..\..

cd microservices\api-gateway
mvn clean install
cd ..\..

cd microservices\vision-service
mvn clean install
cd ..\..

cd microservices\recipe-service
mvn clean install
cd ..\..

cd microservices\inventory-service
mvn clean install
cd ..\..

cd microservices\meal-planning-service
mvn clean install
cd ..\..

cd microservices\user-service
mvn clean install
cd ..\..
```

### Option 2: Automated Build Script
```powershell
# Make sure Maven is in PATH, then run:
.\scripts\build-all-services.ps1
```

---

## 🚀 Start Services (Step by Step)

### Step 1: Start Infrastructure Services

Open **4 separate PowerShell terminals** and run these commands:

#### Terminal 1 - Config Server (Port 8888)
```powershell
cd E:\NourishCare\microservices\config-server
mvn spring-boot:run
```
**Wait for**: `Started ConfigServerApplication`

#### Terminal 2 - Eureka Server (Port 8761)
```powershell
cd E:\NourishCare\microservices\eureka-server
mvn spring-boot:run
```
**Wait for**: `Started EurekaServerApplication`
**Check**: http://localhost:8761

#### Terminal 3 - API Gateway (Port 8080)
```powershell
cd E:\NourishCare\microservices\api-gateway
mvn spring-boot:run
```
**Wait for**: `Started ApiGatewayApplication`

### Step 2: Start Business Services

Open **5 more PowerShell terminals** for business services:

#### Terminal 4 - Vision Service (Port 8081)
```powershell
cd E:\NourishCare\microservices\vision-service
mvn spring-boot:run
```

#### Terminal 5 - Recipe Service (Port 8082)
```powershell
cd E:\NourishCare\microservices\recipe-service
mvn spring-boot:run
```

#### Terminal 6 - Inventory Service (Port 8083)
```powershell
cd E:\NourishCare\microservices\inventory-service
mvn spring-boot:run
```

#### Terminal 7 - Meal Planning Service (Port 8085)
```powershell
cd E:\NourishCare\microservices\meal-planning-service
mvn spring-boot:run
```

#### Terminal 8 - User Service (Port 8086)
```powershell
cd E:\NourishCare\microservices\user-service
mvn spring-boot:run
```

### Step 3: Start Frontend

#### Terminal 9 - Next.js Frontend (Port 3000)
```powershell
cd E:\NourishCare\frontend
npm install
npm run dev
```

---

## ✅ Verification Steps

### 1. Check Service Registration
Visit: http://localhost:8761
You should see all services registered in Eureka.

### 2. Test Health Endpoints
```powershell
# Via API Gateway (Preferred)
curl http://localhost:8080/vision-service/api/vision/health
curl http://localhost:8080/recipe-service/api/recipes/health
curl http://localhost:8080/inventory-service/api/inventory/health

# Direct service access
curl http://localhost:8081/api/vision/health
curl http://localhost:8082/api/recipes/health
curl http://localhost:8083/api/inventory/health
```

### 3. Test Frontend
Visit: http://localhost:3000

---

## 🛠️ Quick Start Scripts

### Use Automated Scripts (After Prerequisites)
```powershell
# Start all services automatically
.\scripts\start-all-services.ps1

# Check service status
.\scripts\check-services.ps1

# Stop all services
.\scripts\stop-all-services.ps1
```

---

## 🌐 Service Access URLs

### Infrastructure
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888/actuator/health
- **API Gateway**: http://localhost:8080/actuator/health

### Business Services (via API Gateway)
- **Vision Service**: http://localhost:8080/vision-service/api/vision/health
- **Recipe Service**: http://localhost:8080/recipe-service/api/recipes/health
- **Inventory Service**: http://localhost:8080/inventory-service/api/inventory/health
- **Meal Planning**: http://localhost:8080/meal-planning-service/api/meal-plans/health
- **User Service**: http://localhost:8080/user-service/api/users/health

### Frontend
- **Main Application**: http://localhost:3000

---

## 🐛 Troubleshooting

### Common Issues

1. **"mvn command not found"**
   - Install Maven and add to PATH
   - Or use full path: `C:\apache-maven-3.8.6\bin\mvn.cmd`

2. **Port already in use**
   ```powershell
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   ```

3. **MongoDB not running**
   ```powershell
   net start MongoDB
   ```

4. **Services not registering with Eureka**
   - Ensure Eureka Server starts first
   - Wait 1-2 minutes for registration
   - Check application.properties configurations

5. **Build failures**
   - Ensure Java 11+ is installed
   - Clear Maven cache: `mvn dependency:purge-local-repository`
   - Delete target folders and rebuild

### Startup Order Importance
1. **MongoDB** (Database)
2. **Config Server** (Configuration)
3. **Eureka Server** (Service Discovery)
4. **API Gateway** (Routing)
5. **Business Services** (Any order)
6. **Frontend** (Last)

---

## 📊 Development Tips

1. **Use IDE Integration**: Import each microservice as a separate Maven project in IntelliJ IDEA or Eclipse
2. **Hot Reload**: Services restart automatically on code changes with `mvn spring-boot:run`
3. **Debug Mode**: Add debug parameters to JVM arguments
4. **Logging**: Check console output for each service
5. **Database**: Use MongoDB Compass to view data: mongodb://localhost:27017

---

## 🎯 API Testing

### Test with Postman/Curl
```bash
# Add a food item to inventory
curl -X POST http://localhost:8080/inventory-service/api/inventory/items \
  -H "Content-Type: application/json" \
  -d '{"name":"Apple","category":"Fruits","quantity":5,"unit":"pieces","userId":"user1"}'

# Search recipes
curl http://localhost:8080/recipe-service/api/recipes/search?query=chicken

# Detect food items
curl -X POST http://localhost:8080/vision-service/api/vision/detect \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/food-image.jpg"}'
```

---

## 💡 Next Steps

1. **Configure External APIs**: Add your Google Cloud Vision API key and Spoonacular API key
2. **Database Setup**: Configure MongoDB with authentication if needed
3. **SSL/HTTPS**: Configure SSL certificates for production
4. **Monitoring**: Add monitoring tools like Micrometer/Prometheus
5. **Testing**: Run integration tests between services

---

**🎉 Your NourishCare microservices architecture is ready to run!**
