#!/bin/bash

# NourishCare Microservices Build Script

echo "🚀 Building NourishCare Microservices..."

# Function to build a service
build_service() {
    local service_name=$1
    echo "📦 Building $service_name..."
    
    cd $service_name
    if [ -f "pom.xml" ]; then
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            echo "✅ $service_name built successfully"
        else
            echo "❌ Failed to build $service_name"
            exit 1
        fi
    else
        echo "⚠️  No pom.xml found for $service_name, skipping..."
    fi
    cd ..
}

# Build all services
services=("service-discovery" "config-server" "api-gateway" "vision-service" "recipe-service" "inventory-service" "meal-planning-service" "user-service")

for service in "${services[@]}"; do
    if [ -d "$service" ]; then
        build_service $service
    else
        echo "⚠️  Directory $service not found, skipping..."
    fi
done

echo ""
echo "🎉 All services built successfully!"
echo ""
echo "Next steps:"
echo "1. Copy .env.example to .env and configure your environment variables"
echo "2. Run: docker-compose up -d"
echo "3. Check services: docker-compose ps"
echo "4. View logs: docker-compose logs -f"
