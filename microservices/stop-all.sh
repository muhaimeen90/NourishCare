#!/bin/bash

# NourishCare Microservices Stop Script

echo "🛑 Stopping NourishCare Microservices..."

# Set script directory as working directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Color codes for better output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to stop a service by PID file
stop_service_by_pid() {
    local service_name=$1
    local pidfile="logs/${service_name}.pid"
    
    if [ -f "$pidfile" ]; then
        local pid=$(cat "$pidfile")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}🔄 Stopping $service_name (PID: $pid)...${NC}"
            kill -TERM $pid
            
            # Wait for graceful shutdown
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 10 ]; do
                sleep 1
                ((count++))
            done
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${RED}   Force killing $service_name...${NC}"
                kill -9 $pid
            fi
            
            echo -e "${GREEN}✅ $service_name stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name was not running${NC}"
        fi
        rm -f "$pidfile"
    else
        # Try to find and stop Maven process for this service
        stop_maven_service "$service_name"
    fi
}

# Function to stop Maven-started services
stop_maven_service() {
    local service_name=$1
    local pids=$(ps aux | grep -E "spring-boot:run.*${service_name}" | grep -v grep | awk '{print $2}')
    
    if [ -n "$pids" ]; then
        echo -e "${YELLOW}🔄 Stopping $service_name (Maven process)...${NC}"
        for pid in $pids; do
            kill -TERM $pid
            # Wait for graceful shutdown
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 10 ]; do
                sleep 1
                ((count++))
            done
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${RED}   Force killing $service_name (PID: $pid)...${NC}"
                kill -9 $pid
            fi
        done
        echo -e "${GREEN}✅ $service_name stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  $service_name was not running${NC}"
    fi
}

# Stop services in reverse order (API Gateway first, then business services, then infrastructure)
echo -e "${BLUE}🌐 Stopping API Gateway...${NC}"
stop_service_by_pid "api-gateway"

echo -e "${BLUE}💼 Stopping Business Services...${NC}"
stop_service_by_pid "user-service"
stop_service_by_pid "meal-planning-service"
stop_service_by_pid "inventory-service"
stop_service_by_pid "recipe-service"
stop_service_by_pid "vision-service"

echo -e "${BLUE}⚙️  Stopping Infrastructure Services...${NC}"
stop_service_by_pid "config-server"
stop_service_by_pid "service-discovery"

# Kill any remaining Spring Boot processes
echo -e "${YELLOW}🧹 Cleaning up any remaining processes...${NC}"

# Kill any remaining Maven spring-boot:run processes
pkill -f "spring-boot:run" 2>/dev/null

# Kill any remaining Java processes with our service names
pkill -f "ServiceDiscoveryApplication" 2>/dev/null
pkill -f "ConfigServerApplication" 2>/dev/null  
pkill -f "ApiGatewayApplication" 2>/dev/null
pkill -f "RecipeServiceApplication" 2>/dev/null
pkill -f "VisionServiceApplication" 2>/dev/null
pkill -f "InventoryServiceApplication" 2>/dev/null
pkill -f "MealPlanningServiceApplication" 2>/dev/null
pkill -f "UserServiceApplication" 2>/dev/null

# Give processes time to terminate
sleep 2

# Verify all services are stopped
remaining_processes=$(ps aux | grep -E "(spring-boot:run|ServiceDiscoveryApplication|ConfigServerApplication|ApiGatewayApplication|RecipeServiceApplication|VisionServiceApplication|InventoryServiceApplication|MealPlanningServiceApplication|UserServiceApplication)" | grep -v grep | wc -l)

if [ $remaining_processes -gt 0 ]; then
    echo -e "${RED}⚠️  Some processes may still be running. Forcing cleanup...${NC}"
    pkill -9 -f "spring-boot:run" 2>/dev/null
    pkill -9 -f "ServiceDiscoveryApplication" 2>/dev/null
    pkill -9 -f "ConfigServerApplication" 2>/dev/null
    pkill -9 -f "ApiGatewayApplication" 2>/dev/null
    pkill -9 -f "RecipeServiceApplication" 2>/dev/null
    pkill -9 -f "VisionServiceApplication" 2>/dev/null
    pkill -9 -f "InventoryServiceApplication" 2>/dev/null
    pkill -9 -f "MealPlanningServiceApplication" 2>/dev/null
    pkill -9 -f "UserServiceApplication" 2>/dev/null
fi

# Clean up log files if requested
read -p "🗑️  Do you want to clean up log files? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}🧹 Cleaning up log files...${NC}"
    rm -f logs/*.log
    echo -e "${GREEN}✅ Log files cleaned${NC}"
fi

echo -e "${GREEN}🎉 All services stopped successfully!${NC}"