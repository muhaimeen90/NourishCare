# NourishCare Microservices Startup Script
# Run this from the NourishCare root directory

Write-Host "🚀 Starting NourishCare Microservices..." -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port,
        [int]$DelaySeconds = 10
    )
    
    Write-Host "📦 Starting $ServiceName on port $Port..." -ForegroundColor Yellow
    
    # Start the service in a new PowerShell window
    Start-Process powershell -ArgumentList "-Command", "cd '$ServicePath'; Write-Host 'Starting $ServiceName...' -ForegroundColor Green; mvn spring-boot:run; Read-Host 'Press Enter to close'" -WindowStyle Normal
    
    # Wait for service to start
    Start-Sleep -Seconds $DelaySeconds
    
    # Check if port is in use (service started)
    $connection = Test-NetConnection -ComputerName localhost -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue -ErrorAction SilentlyContinue
    if ($connection) {
        Write-Host "✅ $ServiceName started successfully on port $Port" -ForegroundColor Green
    } else {
        Write-Host "⚠️  $ServiceName may be starting on port $Port..." -ForegroundColor Yellow
    }
}

# Check if MongoDB is running
Write-Host "🔍 Checking MongoDB..." -ForegroundColor Cyan
try {
    $mongoProcess = Get-Process mongod -ErrorAction SilentlyContinue
    if ($mongoProcess) {
        Write-Host "✅ MongoDB is running" -ForegroundColor Green
    } else {
        Write-Host "⚠️  MongoDB may not be running. Please start MongoDB manually." -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️  Could not check MongoDB status. Please ensure MongoDB is running." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Starting Infrastructure Services..." -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# Start Config Server first (required by other services)
Start-Service -ServiceName "Config Server" -ServicePath "microservices\config-server" -Port 8888 -DelaySeconds 15

# Start Eureka Server (service discovery)
Start-Service -ServiceName "Eureka Server" -ServicePath "microservices\eureka-server" -Port 8761 -DelaySeconds 20

# Start API Gateway
Start-Service -ServiceName "API Gateway" -ServicePath "microservices\api-gateway" -Port 8080 -DelaySeconds 15

Write-Host ""
Write-Host "Starting Business Services..." -ForegroundColor Cyan
Write-Host "============================" -ForegroundColor Cyan

# Start Business Services
Start-Service -ServiceName "Vision Service" -ServicePath "microservices\vision-service" -Port 8081 -DelaySeconds 10
Start-Service -ServiceName "Recipe Service" -ServicePath "microservices\recipe-service" -Port 8082 -DelaySeconds 10
Start-Service -ServiceName "Inventory Service" -ServicePath "microservices\inventory-service" -Port 8083 -DelaySeconds 10
Start-Service -ServiceName "Meal Planning Service" -ServicePath "microservices\meal-planning-service" -Port 8085 -DelaySeconds 10
Start-Service -ServiceName "User Service" -ServicePath "microservices\user-service" -Port 8086 -DelaySeconds 10

Write-Host ""
Write-Host "🎉 All services are starting up!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Service Dashboard URLs:" -ForegroundColor Cyan
Write-Host "  • Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "  • API Gateway Health: http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host ""
Write-Host "🔗 API Endpoints (via Gateway):" -ForegroundColor Cyan
Write-Host "  • Vision Service: http://localhost:8080/vision-service/api/vision/health" -ForegroundColor White
Write-Host "  • Recipe Service: http://localhost:8080/recipe-service/api/recipes/health" -ForegroundColor White
Write-Host "  • Inventory Service: http://localhost:8080/inventory-service/api/inventory/health" -ForegroundColor White
Write-Host "  • Meal Planning: http://localhost:8080/meal-planning-service/api/meal-plans/health" -ForegroundColor White
Write-Host "  • User Service: http://localhost:8080/user-service/api/users/health" -ForegroundColor White
Write-Host ""
Write-Host "⏳ Services may take 1-2 minutes to fully register with Eureka..." -ForegroundColor Yellow
Write-Host ""
Write-Host "🖥️  To start the frontend:" -ForegroundColor Green
Write-Host "  cd frontend" -ForegroundColor White
Write-Host "  npm install" -ForegroundColor White
Write-Host "  npm run dev" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
