# Build all NourishCare microservices
Write-Host "🔨 Building NourishCare Microservices..." -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Cyan

# Function to build a service
function Build-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath
    )
    
    Write-Host ""
    Write-Host "🔧 Building $ServiceName..." -ForegroundColor Yellow
    Write-Host "Path: $ServicePath" -ForegroundColor Gray
    
    if (Test-Path $ServicePath) {
        $currentDir = Get-Location
        Set-Location $ServicePath
        try {
            # Clean and compile
            mvn clean compile -q
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ $ServiceName built successfully" -ForegroundColor Green
                Set-Location $currentDir
                return $true
            } else {
                Write-Host "❌ $ServiceName build failed" -ForegroundColor Red
                Set-Location $currentDir
                return $false
            }
        } catch {
            Write-Host "❌ $ServiceName build error: $_" -ForegroundColor Red
            Set-Location $currentDir
            return $false
        }
    } else {
        Write-Host "❌ $ServiceName path not found: $ServicePath" -ForegroundColor Red
        return $false
    }
}

$buildResults = @()

Write-Host "Building Infrastructure Services..." -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

$buildResults += Build-Service -ServiceName "Config Server" -ServicePath "microservices\config-server"
$buildResults += Build-Service -ServiceName "Eureka Server" -ServicePath "microservices\eureka-server"
$buildResults += Build-Service -ServiceName "API Gateway" -ServicePath "microservices\api-gateway"

Write-Host ""
Write-Host "Building Business Services..." -ForegroundColor Cyan
Write-Host "============================" -ForegroundColor Cyan

$buildResults += Build-Service -ServiceName "Vision Service" -ServicePath "microservices\vision-service"
$buildResults += Build-Service -ServiceName "Recipe Service" -ServicePath "microservices\recipe-service"
$buildResults += Build-Service -ServiceName "Inventory Service" -ServicePath "microservices\inventory-service"
$buildResults += Build-Service -ServiceName "Meal Planning Service" -ServicePath "microservices\meal-planning-service"
$buildResults += Build-Service -ServiceName "User Service" -ServicePath "microservices\user-service"

Write-Host ""
Write-Host "Build Summary:" -ForegroundColor Cyan
Write-Host "=============" -ForegroundColor Cyan

$successCount = ($buildResults | Where-Object { $_ -eq $true }).Count
$totalCount = $buildResults.Count

if ($successCount -eq $totalCount) {
    Write-Host "🎉 All $totalCount services built successfully!" -ForegroundColor Green
} else {
    $failedCount = $totalCount - $successCount
    Write-Host "⚠️  $successCount/$totalCount services built successfully" -ForegroundColor Yellow
    Write-Host "❌ $failedCount services failed to build" -ForegroundColor Red
}

Write-Host ""
Write-Host "🖥️  To build frontend:" -ForegroundColor Green
Write-Host "cd frontend" -ForegroundColor White
Write-Host "npm install" -ForegroundColor White
Write-Host "npm run build" -ForegroundColor White

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
