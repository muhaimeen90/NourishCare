#!/bin/bash

# NourishCare Microservices Startup Script

echo "🚀 Starting NourishCare Microservices..."

# Set script directory as working directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Color codes for better output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}⏳ Waiting for $service_name to start on port $port...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if check_port $port; then
            echo -e "${GREEN}✅ $service_name is ready on port $port${NC}"
            return 0
        fi
        
        echo -e "${YELLOW}   Attempt $attempt/$max_attempts - waiting 10 seconds...${NC}"
        sleep 10
        ((attempt++))
    done
    
    echo -e "${RED}❌ $service_name failed to start within expected time${NC}"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    local wait_for_port=$3
    
    echo -e "${BLUE}🔄 Starting $service_name...${NC}"
    
    # Check if service is already running
    if check_port $port; then
        echo -e "${YELLOW}⚠️  $service_name is already running on port $port${NC}"
        return 0
    fi
    
    # Navigate to service directory
    cd "$service_name"
    
    if [ ! -f "pom.xml" ]; then
        echo -e "${RED}❌ No pom.xml found for $service_name${NC}"
        cd ..
        return 1
    fi
    
    # Start the service in background
    nohup mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "../logs/${service_name}.pid"
    
    echo -e "${GREEN}✅ $service_name started with PID $pid${NC}"
    cd ..
    
    # Wait for service to be ready if specified
    if [ "$wait_for_port" = "true" ]; then
        wait_for_service "$service_name" "$port"
    fi
}

# Create logs directory
mkdir -p logs

# Kill any existing processes
echo -e "${YELLOW}🧹 Cleaning up any existing processes...${NC}"
if [ -d "logs" ]; then
    for pidfile in logs/*.pid; do
        if [ -f "$pidfile" ]; then
            pid=$(cat "$pidfile")
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${YELLOW}   Killing process $pid${NC}"
                kill -9 $pid 2>/dev/null
            fi
            rm -f "$pidfile"
        fi
    done
fi

# Also kill any maven spring-boot processes
pkill -f "spring-boot:run" 2>/dev/null

echo -e "${GREEN}🎯 Starting services in proper order...${NC}"

# Step 1: Start Service Discovery (Eureka Server)
echo -e "${BLUE}📡 Step 1: Starting Service Discovery${NC}"
start_service "service-discovery" "8761" "true"
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Failed to start Service Discovery. Exiting.${NC}"
    exit 1
fi

# Step 2: Start Config Server
echo -e "${BLUE}⚙️  Step 2: Starting Config Server${NC}"
start_service "config-server" "8888" "true"
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Failed to start Config Server. Exiting.${NC}"
    exit 1
fi

# Step 3: Start Business Services
echo -e "${BLUE}💼 Step 3: Starting Business Services${NC}"
start_service "vision-service" "8081" "false"
start_service "recipe-service" "8082" "false"
start_service "inventory-service" "8083" "false"
start_service "meal-planning-service" "8084" "false"
start_service "user-service" "8086" "false"

# Give business services time to register
echo -e "${YELLOW}⏳ Waiting 30 seconds for business services to register with Eureka...${NC}"
sleep 30

# Step 4: Start API Gateway
echo -e "${BLUE}🌐 Step 4: Starting API Gateway${NC}"
start_service "api-gateway" "8080" "true"
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Failed to start API Gateway. Exiting.${NC}"
    exit 1
fi

echo -e "${GREEN}🎉 All services started successfully!${NC}"
echo ""
echo -e "${BLUE}📋 Service Status:${NC}"
echo -e "${GREEN}   🔍 Service Discovery (Eureka): http://localhost:8761${NC}"
echo -e "${GREEN}   ⚙️  Config Server: http://localhost:8888${NC}"
echo -e "${GREEN}   🌐 API Gateway: http://localhost:8080${NC}"
echo -e "${GREEN}   👁️  Vision Service: http://localhost:8081${NC}"
echo -e "${GREEN}   🍳 Recipe Service: http://localhost:8082${NC}"
echo -e "${GREEN}   📦 Inventory Service: http://localhost:8083${NC}"
echo -e "${GREEN}   📅 Meal Planning Service: http://localhost:8084${NC}"
echo -e "${GREEN}   👤 User Service: http://localhost:8086${NC}"
echo ""
echo -e "${YELLOW}📜 Logs are available in the 'logs' directory${NC}"
echo -e "${YELLOW}🛑 To stop all services, run: ./stop-all.sh${NC}"
echo ""
echo -e "${BLUE}🔗 Main Application URL: http://localhost:8080${NC}"