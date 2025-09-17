# Check status of all NourishCare services
Write-Host "🔍 Checking NourishCare Services Status..." -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# Function to check if a service is running
function Test-Service {
    param(
        [string]$ServiceName,
        [int]$Port,
        [string]$HealthEndpoint = ""
    )
    
    Write-Host -NoNewline "📦 $ServiceName (Port $Port): " -ForegroundColor Yellow
    
    # Test port connection
    $connection = Test-NetConnection -ComputerName localhost -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue -ErrorAction SilentlyContinue
    
    if ($connection) {
        Write-Host "🟢 RUNNING" -ForegroundColor Green
        
        # Test health endpoint if provided
        if ($HealthEndpoint) {
            try {
                $response = Invoke-RestMethod -Uri $HealthEndpoint -TimeoutSec 5 -ErrorAction SilentlyContinue
                if ($response) {
                    Write-Host "   ✅ Health check: OK" -ForegroundColor Green
                } else {
                    Write-Host "   ⚠️  Health check: No response" -ForegroundColor Yellow
                }
            } catch {
                Write-Host "   ❌ Health check: Failed" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "🔴 NOT RUNNING" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Infrastructure Services:" -ForegroundColor Cyan
Write-Host "======================" -ForegroundColor Cyan
Test-Service -ServiceName "Config Server" -Port 8888 -HealthEndpoint "http://localhost:8888/actuator/health"
Test-Service -ServiceName "Eureka Server" -Port 8761 -HealthEndpoint "http://localhost:8761/actuator/health"
Test-Service -ServiceName "API Gateway" -Port 8080 -HealthEndpoint "http://localhost:8080/actuator/health"

Write-Host ""
Write-Host "Business Services:" -ForegroundColor Cyan
Write-Host "=================" -ForegroundColor Cyan
Test-Service -ServiceName "Vision Service" -Port 8081 -HealthEndpoint "http://localhost:8081/api/vision/health"
Test-Service -ServiceName "Recipe Service" -Port 8082 -HealthEndpoint "http://localhost:8082/api/recipes/health"
Test-Service -ServiceName "Inventory Service" -Port 8083 -HealthEndpoint "http://localhost:8083/api/inventory/health"
Test-Service -ServiceName "Meal Planning Service" -Port 8085
Test-Service -ServiceName "User Service" -Port 8086

Write-Host ""
Write-Host "Frontend:" -ForegroundColor Cyan
Write-Host "========" -ForegroundColor Cyan
Test-Service -ServiceName "Next.js Frontend" -Port 3000

Write-Host ""
Write-Host "Database:" -ForegroundColor Cyan
Write-Host "========" -ForegroundColor Cyan
Write-Host -NoNewline "📊 MongoDB (Port 27017): " -ForegroundColor Yellow
$mongoConnection = Test-NetConnection -ComputerName localhost -Port 27017 -InformationLevel Quiet -WarningAction SilentlyContinue -ErrorAction SilentlyContinue
if ($mongoConnection) {
    Write-Host "🟢 RUNNING" -ForegroundColor Green
} else {
    Write-Host "🔴 NOT RUNNING" -ForegroundColor Red
    Write-Host "   ⚠️  Please start MongoDB service" -ForegroundColor Yellow
}

# Check Eureka registered services
Write-Host ""
Write-Host "Eureka Service Registry:" -ForegroundColor Cyan
Write-Host "======================" -ForegroundColor Cyan

try {
    $eurekaResponse = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{Accept="application/json"} -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($eurekaResponse -and $eurekaResponse.applications -and $eurekaResponse.applications.application) {
        $services = $eurekaResponse.applications.application
        Write-Host "✅ Registered Services:" -ForegroundColor Green
        foreach ($service in $services) {
            $serviceName = $service.name
            $instanceCount = if ($service.instance -is [Array]) { $service.instance.Count } else { 1 }
            Write-Host "   • $serviceName ($instanceCount instance(s))" -ForegroundColor White
        }
    } else {
        Write-Host "⚠️  No services registered with Eureka" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not connect to Eureka Server" -ForegroundColor Red
}

Write-Host ""
Write-Host "🌐 Access URLs:" -ForegroundColor Green
Write-Host "=============" -ForegroundColor Cyan
Write-Host "• Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "• API Gateway: http://localhost:8080" -ForegroundColor White
Write-Host "• Frontend Application: http://localhost:3000" -ForegroundColor White

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
