# NourishCare Microservices Architecture

## Overview
NourishCare has been refactored from a monolithic application into a microservices architecture to improve scalability, maintainability, and deployment flexibility.

## Architecture Diagram
```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Mobile App    │
│   (Next.js)     │    │   (Future)      │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────────────────┘
                     │
          ┌─────────────────────┐
          │   API Gateway       │
          │   (Port 8080)       │
          └─────────┬───────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
┌───▼───┐     ┌────▼────┐     ┌───▼───┐
│Service│     │ Config  │     │Eureka │
│Mesh   │     │ Server  │     │Server │
│       │     │(Port    │     │(Port  │
│       │     │ 8888)   │     │ 8761) │
└───────┘     └─────────┘     └───────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
┌───────▼───┐ ┌────▼─────┐ ┌───▼────────┐
│ Vision    │ │ Recipe   │ │ Inventory  │
│ Service   │ │ Service  │ │ Service    │
│(Port 8081)│ │(Port     │ │(Port 8083) │
│           │ │ 8082)    │ │            │
└───────────┘ └──────────┘ └────────────┘
        │           │           │
┌───────▼───┐ ┌────▼─────┐ ┌───▼────────┐
│Meal Plan  │ │ User     │ │ Notification│
│Service    │ │ Service  │ │ Service     │
│(Port 8084)│ │(Port     │ │(Port 8086) │
│           │ │ 8085)    │ │            │
└───────────┘ └──────────┘ └────────────┘
        │           │           │
┌───────▼───────────▼───────────▼────┐
│        MongoDB Atlas Cluster       │
│     (Separate Databases per        │
│           Service)                 │
└────────────────────────────────────┘
```

## Microservices Overview

### 1. API Gateway (Port 8080)
- **Responsibility**: Entry point for all client requests
- **Technology**: Spring Cloud Gateway
- **Features**:
  - Request routing to appropriate microservices
  - Load balancing
  - Authentication and authorization
  - Rate limiting
  - Request/response transformation
  - CORS handling

### 2. Service Discovery (Port 8761)
- **Responsibility**: Service registration and discovery
- **Technology**: Netflix Eureka Server
- **Features**:
  - Automatic service registration
  - Health checking
  - Load balancing support
  - Failover mechanisms

### 3. Configuration Server (Port 8888)
- **Responsibility**: Centralized configuration management
- **Technology**: Spring Cloud Config Server
- **Features**:
  - Environment-specific configurations
  - Dynamic configuration updates
  - Version control for configurations
  - Encrypted sensitive data

### 4. Vision Service (Port 8081)
- **Responsibility**: AI-powered food detection and nutritional analysis
- **Technology**: Spring Boot + Google Cloud Vision API
- **Database**: MongoDB (vision_db)
- **Features**:
  - Image upload and processing
  - Food item detection using Google Cloud Vision API
  - Nutritional analysis
  - Image metadata storage
  - Mock service for testing

### 5. Recipe Service (Port 8082)
- **Responsibility**: Recipe management and discovery
- **Technology**: Spring Boot + Spoonacular API
- **Database**: MongoDB (recipe_db)
- **Features**:
  - Recipe CRUD operations
  - Recipe search and filtering
  - Integration with Spoonacular API
  - Recipe recommendations
  - Nutritional information
  - Recipe categories and tags

### 6. Inventory Service (Port 8083)
- **Responsibility**: Food inventory and expiration tracking
- **Technology**: Spring Boot
- **Database**: MongoDB (inventory_db)
- **Features**:
  - Food item inventory management
  - Expiration date tracking
  - Category management
  - Waste reduction analytics
  - Notification triggers for expiring items

### 7. Meal Planning Service (Port 8084)
- **Responsibility**: Meal planning and shopping lists
- **Technology**: Spring Boot
- **Database**: MongoDB (meal_planning_db)
- **Features**:
  - Weekly meal planning
  - Shopping list generation
  - Nutrition tracking and goals
  - Meal plan templates
  - Integration with Recipe and Inventory services

### 8. User Service (Port 8085)
- **Responsibility**: User management and preferences
- **Technology**: Spring Boot + Spring Security
- **Database**: MongoDB (user_db)
- **Features**:
  - User registration and authentication
  - User profiles and preferences
  - Dietary restrictions and goals
  - JWT token management
  - Social login integration

### 9. Notification Service (Port 8086)
- **Responsibility**: Notifications and alerts
- **Technology**: Spring Boot
- **Database**: MongoDB (notification_db)
- **Features**:
  - Email notifications
  - Push notifications (future)
  - Expiration alerts
  - Meal planning reminders
  - Custom notification preferences

## Communication Patterns

### Synchronous Communication
- **API Gateway** ↔ **All Services**: HTTP REST calls
- **Recipe Service** ↔ **Spoonacular API**: HTTP REST calls
- **Vision Service** ↔ **Google Cloud Vision API**: HTTP REST calls

### Asynchronous Communication (Future Enhancement)
- Event-driven architecture using message queues (RabbitMQ/Apache Kafka)
- Domain events for service-to-service communication
- Event sourcing for audit trails

## Database Strategy

### Database per Service Pattern
Each microservice has its own MongoDB database:
- `vision_db`: Image metadata, detection results
- `recipe_db`: Recipes, categories, nutritional data
- `inventory_db`: Food items, categories, expiration tracking
- `meal_planning_db`: Meal plans, shopping lists, nutrition goals
- `user_db`: User accounts, preferences, authentication
- `notification_db`: Notification history, preferences

### Data Consistency
- **Eventual Consistency**: Services maintain their own data
- **Saga Pattern**: For distributed transactions
- **CQRS**: Command Query Responsibility Segregation where needed

## Security Architecture

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication
- **API Gateway**: Central authentication point
- **OAuth2**: Social login integration
- **Role-based Access Control**: User permissions

### Data Security
- **Encrypted Environment Variables**: Sensitive configuration
- **Service-to-Service Authentication**: Mutual TLS
- **Data Encryption**: At rest and in transit

## Deployment Strategy

### Containerization
- **Docker**: Each service containerized
- **Docker Compose**: Local development environment
- **Kubernetes**: Production orchestration (future)

### CI/CD Pipeline
- **Individual Service Deployment**: Independent deployments
- **Blue-Green Deployment**: Zero-downtime deployments
- **Feature Toggles**: Safe feature rollouts

## Monitoring and Observability

### Logging
- **Centralized Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Structured Logging**: JSON format with correlation IDs
- **Log Aggregation**: Distributed tracing

### Metrics
- **Application Metrics**: Micrometer + Prometheus
- **Infrastructure Metrics**: Docker stats, JVM metrics
- **Business Metrics**: Custom metrics per service

### Health Checks
- **Spring Boot Actuator**: Health endpoints
- **Eureka**: Service health monitoring
- **API Gateway**: Circuit breaker patterns

## Development Workflow

### Local Development
1. Start infrastructure services (Eureka, Config Server, MongoDB)
2. Start required business services
3. Use Docker Compose for simplified setup
4. Individual service debugging and testing

### Testing Strategy
- **Unit Tests**: Service-specific logic
- **Integration Tests**: Service-to-service communication
- **Contract Tests**: API contract validation
- **End-to-End Tests**: Full workflow testing

## Migration Path from Monolith

### Phase 1: Infrastructure Setup
- ✅ Create service discovery (Eureka)
- ✅ Setup configuration server
- ✅ Implement API Gateway

### Phase 2: Extract Services
- ✅ Vision Service (least dependencies)
- ✅ Recipe Service (external API integration)
- ✅ User Service (foundational)
- ✅ Inventory Service
- ✅ Meal Planning Service

### Phase 3: Enhancement
- Notification Service
- Advanced monitoring
- Event-driven architecture
- Performance optimization

## Benefits of Microservices Architecture

### Scalability
- **Independent Scaling**: Scale services based on demand
- **Resource Optimization**: Allocate resources per service needs
- **Load Distribution**: Better load management

### Development
- **Team Independence**: Teams can work on different services
- **Technology Diversity**: Choose best technology per service
- **Faster Deployment**: Independent service deployments

### Maintenance
- **Fault Isolation**: Service failures don't affect entire system
- **Easier Testing**: Smaller, focused test suites
- **Better Code Organization**: Domain-driven design

### Business
- **Faster Time to Market**: Parallel development
- **Better Resource Utilization**: Cost optimization
- **Enhanced Reliability**: Improved system resilience

## Future Enhancements

### Short Term
- Event-driven architecture
- Advanced monitoring and alerting
- Performance optimization
- Security enhancements

### Long Term
- Serverless functions for specific tasks
- Machine learning service integration
- Real-time notifications
- Mobile app support
- Multi-tenant architecture

## Conclusion

This microservices architecture provides NourishCare with:
- **Scalability** for handling growing user base
- **Flexibility** in technology choices and team organization
- **Resilience** through fault isolation and redundancy
- **Maintainability** through clear service boundaries and responsibilities

The architecture supports current features while providing a foundation for future growth and enhancement.
