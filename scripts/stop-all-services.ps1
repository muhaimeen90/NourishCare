# Stop all NourishCare services
Write-Host "🛑 Stopping NourishCare Microservices..." -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Cyan

# Function to stop services on specific ports
function Stop-ServiceOnPort {
    param([int]$Port, [string]$ServiceName)
    
    Write-Host "🔍 Checking for $ServiceName on port $Port..." -ForegroundColor Yellow
    
    # Find processes using the port
    $connections = netstat -ano | Select-String ":$Port "
    
    if ($connections) {
        foreach ($connection in $connections) {
            $pid = ($connection -split '\s+')[-1]
            if ($pid -and $pid -ne "0") {
                try {
                    $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                    if ($process) {
                        Write-Host "🔪 Stopping $ServiceName (PID: $pid)..." -ForegroundColor Red
                        Stop-Process -Id $pid -Force
                        Write-Host "✅ $ServiceName stopped" -ForegroundColor Green
                    }
                } catch {
                    Write-Host "⚠️  Could not stop process with PID $pid" -ForegroundColor Yellow
                }
            }
        }
    } else {
        Write-Host "✅ No $ServiceName running on port $Port" -ForegroundColor Green
    }
}

# Stop all services
Write-Host "Stopping services..." -ForegroundColor Cyan

Stop-ServiceOnPort -Port 8888 -ServiceName "Config Server"
Stop-ServiceOnPort -Port 8761 -ServiceName "Eureka Server"
Stop-ServiceOnPort -Port 8080 -ServiceName "API Gateway"
Stop-ServiceOnPort -Port 8081 -ServiceName "Vision Service"
Stop-ServiceOnPort -Port 8082 -ServiceName "Recipe Service"
Stop-ServiceOnPort -Port 8083 -ServiceName "Inventory Service"
Stop-ServiceOnPort -Port 8085 -ServiceName "Meal Planning Service"
Stop-ServiceOnPort -Port 8086 -ServiceName "User Service"

# Also stop frontend if running
Stop-ServiceOnPort -Port 3000 -ServiceName "Frontend (Next.js)"

Write-Host ""
Write-Host "🏁 All NourishCare services stopped!" -ForegroundColor Green

# Option to kill all Java processes (be careful with this)
Write-Host ""
$killJava = Read-Host "Do you want to kill ALL Java processes? (y/N)"
if ($killJava -eq 'y' -or $killJava -eq 'Y') {
    Write-Host "🔪 Killing all Java processes..." -ForegroundColor Red
    Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
    Write-Host "✅ All Java processes stopped" -ForegroundColor Green
}

Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
