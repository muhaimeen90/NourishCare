#!/bin/bash

# NourishCare Microservices Status Script

echo "📊 NourishCare Microservices Status"

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

# Function to check service status
check_service() {
    local service_name=$1
    local port=$2
    local pidfile="logs/${service_name}.pid"
    
    printf "%-25s" "$service_name:"
    
    # Check if PID file exists and process is running
    if [ -f "$pidfile" ]; then
        local pid=$(cat "$pidfile")
        if ps -p $pid > /dev/null 2>&1; then
            if check_port $port; then
                echo -e "${GREEN}✅ RUNNING (PID: $pid, Port: $port)${NC}"
            else
                echo -e "${YELLOW}⚠️  RUNNING but port $port not listening (PID: $pid)${NC}"
            fi
        else
            echo -e "${RED}❌ STOPPED (stale PID file)${NC}"
            rm -f "$pidfile"
        fi
    else
        if check_port $port; then
            echo -e "${YELLOW}⚠️  RUNNING (no PID file, Port: $port)${NC}"
        else
            echo -e "${RED}❌ STOPPED${NC}"
        fi
    fi
}

echo "=================================="

# Check all services
check_service "service-discovery" "8761"
check_service "config-server" "8888"
check_service "api-gateway" "8080"
check_service "vision-service" "8081"
check_service "recipe-service" "8082"
check_service "inventory-service" "8083"
check_service "meal-planning-service" "8084"
check_service "user-service" "8086"

echo "=================================="

# Quick health check URLs
echo -e "${BLUE}🔗 Quick Access URLs:${NC}"
echo -e "   Eureka Dashboard: http://localhost:8761"
echo -e "   API Gateway: http://localhost:8080"
echo -e "   Config Server: http://localhost:8888"

echo ""
echo -e "${YELLOW}💡 Use './start-all.sh' to start all services${NC}"
echo -e "${YELLOW}💡 Use './stop-all.sh' to stop all services${NC}"