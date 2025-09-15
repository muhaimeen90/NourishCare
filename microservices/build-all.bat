@echo off
REM NourishCare Microservices Build Script for Windows

echo 🚀 Building NourishCare Microservices...

set services=service-discovery config-server api-gateway vision-service recipe-service inventory-service meal-planning-service user-service

for %%s in (%services%) do (
    if exist %%s (
        echo 📦 Building %%s...
        cd %%s
        if exist pom.xml (
            call mvn clean package -DskipTests
            if errorlevel 1 (
                echo ❌ Failed to build %%s
                exit /b 1
            ) else (
                echo ✅ %%s built successfully
            )
        ) else (
            echo ⚠️ No pom.xml found for %%s, skipping...
        )
        cd ..
    ) else (
        echo ⚠️ Directory %%s not found, skipping...
    )
)

echo.
echo 🎉 All services built successfully!
echo.
echo Next steps:
echo 1. Copy .env.example to .env and configure your environment variables
echo 2. Run: docker-compose up -d
echo 3. Check services: docker-compose ps
echo 4. View logs: docker-compose logs -f
