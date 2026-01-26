# Fitness Microservices

A cloud-native microservices architecture built with Spring Boot and Spring Cloud for a comprehensive fitness management platform.

## 🏗️ Architecture Overview

This project demonstrates a production-ready microservices ecosystem with service discovery, API gateway routing, centralized configuration, and OAuth2 security.

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (8888)                      │
│              Spring Cloud Gateway + OAuth2                  │
└────────────────┬────────────────────────────────────────────┘
                 │
    ┌────────────┼────────────┬──────────────┐
    │            │            │              │
    ▼            ▼            ▼              ▼
┌────────┐  ┌────────┐  ┌────────┐   ┌──────────┐
│  User  │  │Activity│  │   AI   │   │Eureka    │
│Service │  │Service │  │Service │   │(8761)    │
└────────┘  └────────┘  └────────┘   └──────────┘
    │            │            │
    └────────────┼────────────┘
                 │
    ┌────────────▼────────────┐
    │  Config Server (8888)   │
    │  Centralized Config     │
    └─────────────────────────┘
```

## 📦 Services

### 1. **API Gateway** (`gateway/`)
**Port:** 8888

#### Core Features
- ✅ Central entry point for all API requests
- ✅ Dynamic routing to microservices via Eureka
- ✅ OAuth2 Resource Server for authentication
- ✅ Spring Cloud Gateway with WebFlux

**Route Example:**
```yaml
/api/users/** → USER-SERVICE
/api/activities/** → ACTIVITY-SERVICE
/api/ai/** → AI-SERVICE
```

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Cloud Gateway
- Spring Security OAuth2
- Eureka Client
- Reactor Netty (WebFlux)
- Java 8

**Key Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**
```

---

### 2. **User Service** (`userService/`)
**Port:** 8081 (Registered with Eureka as `USER-SERVICE`)

#### Core Features
- ✅ User registration & authentication
- ✅ Keycloak integration for OAuth2/OIDC
- ✅ User profile management
- ✅ REST API for user operations
- ✅ PostgreSQL database with Hibernate ORM

**Key Endpoints:**
```
POST   /api/users/register              - Register new user
GET    /api/users/{userId}              - Get user profile
GET    /api/users/{userId}/validateUser - Validate user existence
PUT    /api/users/{userId}              - Update user profile
DELETE /api/users/{userId}              - Delete user account
```

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Data JPA
- Spring Security
- Keycloak integration
- PostgreSQL + Hibernate ORM
- Lombok (boilerplate reduction)

**Database Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fitnes_user_db
spring.datasource.username=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**Repository Methods (Best Practices):**
```java
// Spring Data JPA generates SQL automatically
boolean existsByEmail(String email);
boolean existsByKeycloakId(String keycloakId);  // ✅ Correct naming convention
UserResponse findByEmail(String email);
```

---

### 3. **Activity Service** (`activityService/`)
**Port:** 8082 (Registered with Eureka as `ACTIVITY-SERVICE`)

#### Core Features
- ✅ Activity tracking and logging
- ✅ User activity history management
- ✅ Statistics and analytics
- ✅ Integration with fitness platform
- ✅ Real-time activity updates

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Data JPA
- Spring Cloud Eureka Client
- PostgreSQL Database
- Spring Web MVC

**Key Responsibilities:**
- Log user workout sessions
- Track calories burned
- Calculate activity statistics
- Provide historical data to AI Service

---

### 4. **AI Service** (`aiService/`)
**Port:** 8083 (Registered with Eureka as `AI-SERVICE`)

#### Core Features
- ✅ Machine learning predictions
- ✅ Personalized recommendations
- ✅ Workout suggestions based on activity data
- ✅ REST API for ML models
- ✅ Data analysis and insights

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Cloud Eureka Client
- TensorFlow/ML Libraries (optional)
- Python integration (optional)
- Data processing libraries

**API Endpoints:**
```
POST   /api/ai/recommendations    - Get personalized recommendations
POST   /api/ai/predict            - Predict fitness outcomes
GET    /api/ai/insights/{userId}  - Get user insights
```

---

### 5. **Eureka Server** (`eureka/`)
**Port:** 8761
**Dashboard URL:** http://localhost:8761

#### Core Features
- ✅ Service registry for all microservices
- ✅ Service discovery mechanism
- ✅ Health checks and automatic deregistration
- ✅ Client-side load balancing
- ✅ Dynamic service registration/deregistration

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Cloud Eureka Server
- Built-in web dashboard

**Service Registration Flow:**
```
User Service ──┐
Activity Svc ──├──> Eureka Server <──┐
AI Service   ──┘                      │
                                Gateway queries
                                  for services
```

---

### 6. **Config Server** (`configServer/`)
**Port:** 8888

#### Core Features
- ✅ Centralized configuration management
- ✅ Environment-specific properties (dev, prod, test)
- ✅ Dynamic configuration updates
- ✅ Git-based configuration storage
- ✅ Version control for configurations

**Supported Configurations:**
- `activity-service.properties` - Activity Service config
- `ai-service.properties` - AI Service config
- `user-service.properties` - User Service config
- `gateway.yml` - Gateway config

**Tech Stack:**
- Spring Boot 2.7.18
- Spring Cloud Config Server
- Git backend for versioning

**Configuration Directory:**
```
configServer/src/main/resources/config/
├── activity-service.properties
├── ai-service.properties
├── user-service.properties
└── gateway.yml
```

---

## 🚀 Technology Stack & Hacks

### Core Technologies

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| Framework | Spring Boot | 2.7.18 | Microservice foundation |
| Cloud | Spring Cloud | 2021.0.8 | Distributed system patterns |
| Gateway | Spring Cloud Gateway | WebFlux | API routing & filtering |
| Service Discovery | Eureka | Built-in | Service registry & load balancing |
| Config Management | Spring Cloud Config | Git-based | Centralized configuration |
| Security | Spring Security + OAuth2 | Keycloak | Authentication & authorization |
| Data Access | Spring Data JPA | Hibernate | ORM and database abstraction |
| Database | PostgreSQL | Latest | Persistent data storage |
| Java Version | OpenJDK | 1.8 | Compatibility & stability |

### Technology Hacks & Best Practices

#### 1. **Dynamic Service Discovery via Eureka Load Balancer**
```yaml
# gateway/src/main/resources/application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE  # 'lb://' enables load balancing
          predicates:
            - Path=/api/users/**
```
**Benefit:** Add/remove service instances without redeploying gateway. Automatic load balancing across multiple instances.

#### 2. **Optional Config Server Integration**
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```
**Benefit:** Services work standalone without config server, but use it when available. Graceful degradation.

#### 3. **OAuth2 Resource Server at API Gateway**
```java
@EnableResourceServer
public class GatewayApplication { }
```
**Benefit:** Validate JWT tokens at gateway level, protect all downstream services without individual security configs. Single point of authentication.

#### 4. **Spring Data JPA Naming Conventions**
```java
// These automatically generate SQL queries!
existsByEmail(String email)           // SELECT ... WHERE email = ?
existsByKeycloakId(String keycloakId) // SELECT ... WHERE keycloak_id = ?
findByEmail(String email)              // SELECT ... WHERE email = ?
```
**Benefit:** No need to write SQL, type-safe queries, automatic query generation.

#### 5. **Lombok for Boilerplate Reduction**
```java
@AllArgsConstructor  // Auto-generates constructor
@Data               // Auto-generates getters/setters/equals/hashCode
@Entity            // JPA entity mapping
public class User {
    @Id
    private String id;
    private String email;
}
```
**Benefit:** 40% less code, fewer bugs, cleaner classes.

#### 6. **Spring Cloud Gateway with WebFlux (Non-Blocking I/O)**
```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          # Uses Netty reactor instead of Tomcat
```
**Benefit:** Handle 10x more concurrent connections with lower memory footprint. Non-blocking I/O for better performance.

#### 7. **Keycloak for Enterprise Security**
```properties
keycloak.auth-server-url=http://keycloak:8080/auth
keycloak.realm=fitness
keycloak.resource=fitness-app
keycloak.public-client=true
```
**Benefit:** SSO (Single Sign-On), multi-factor auth, social login, and token management out-of-the-box.

#### 8. **Git-Backed Configuration Management**
```
configServer/src/main/resources/config/
├── activity-service.properties
├── ai-service.properties
├── user-service.properties
└── gateway.yml
```
**Benefit:** Config changes tracked in Git, easy rollback, audit trail, versioning, and collaboration.

#### 9. **Hibernate DDL Auto for Schema Management**
```properties
spring.jpa.hibernate.ddl-auto=update  # Auto-creates/updates database schema
```
**Benefit:** No manual SQL scripts, automatic schema evolution.

#### 10. **PostgreSQL Dialect for Optimal Query Generation**
```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```
**Benefit:** Hibernate generates PostgreSQL-specific optimized queries.

---

## 🛠️ Running the Services

### Prerequisites
```bash
Java 8 (OpenJDK or Oracle JDK)
Maven 3.6 or higher
Git
PostgreSQL database
Keycloak server (optional, for OAuth2)
```

### Start Services in Order

**Terminal 1 - Eureka Server (Service Registry)**
```bash
cd eureka
mvn spring-boot:run
# Verify at: http://localhost:8761
```

**Terminal 2 - Config Server (Configuration)**
```bash
cd configServer
mvn spring-boot:run
# Available at: http://localhost:8888
```

**Terminal 3 - User Service**
```bash
cd userService
mvn spring-boot:run
# Registers as: USER-SERVICE on Eureka
```

**Terminal 4 - Activity Service**
```bash
cd activityService
mvn spring-boot:run
# Registers as: ACTIVITY-SERVICE on Eureka
```

**Terminal 5 - AI Service**
```bash
cd aiService
mvn spring-boot:run
# Registers as: AI-SERVICE on Eureka
```

**Terminal 6 - API Gateway (Start Last)**
```bash
cd gateway
mvn spring-boot:run
# Available at: http://localhost:8888
```

### Verify Services Are Running

```bash
# Check Eureka Dashboard
curl http://localhost:8761

# Check Config Server
curl http://localhost:8888/user-service/default

# Test Gateway Routing
curl http://localhost:8888/api/users/

# Check Service Health
curl http://localhost:8761/eureka/apps
```

### Expected Service Ports

| Service | Port | Eureka Name | Health Check |
|---------|------|-------------|--------------|
| Eureka | 8761 | N/A | http://localhost:8761 |
| Config Server | 8888 | N/A | http://localhost:8888/actuator/health |
| API Gateway | 8888 | GATEWAY | http://localhost:8888/actuator/health |
| User Service | 8081 | USER-SERVICE | http://localhost:8081/actuator/health |
| Activity Service | 8082 | ACTIVITY-SERVICE | http://localhost:8082/actuator/health |
| AI Service | 8083 | AI-SERVICE | http://localhost:8083/actuator/health |

---

## 📝 Configuration Examples

### Gateway Route Configuration
```yaml
# gateway/src/main/resources/application.yml
spring:
  application:
    name: api-gateway
  config:
    import: optional:configserver:http://localhost:8888
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**
        - id: activity-service
          uri: lb://ACTIVITY-SERVICE
          predicates:
            - Path=/api/activities/**
        - id: ai-service
          uri: lb://AI-SERVICE
          predicates:
            - Path=/api/ai/**

server:
  port: 8888

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Service Registration
```properties
# user-service/src/main/resources/application.properties
spring.application.name=USER-SERVICE
server.port=8081
spring.config.import=optional:configserver:http://localhost:8888

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/fitnes_user_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

# Eureka Registration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

---

## 🔐 Security Architecture

```
┌─────────────────────────────────────────┐
│      Client (Web/Mobile App)            │
│      Sends: Authorization: Bearer <JWT> │
└────────────────┬────────────────────────┘
                 │ Request + JWT Token
┌────────────────▼────────────────────────┐
│ API Gateway (OAuth2 Resource Server)    │
│ ├─ Validate JWT Token signature         │
│ ├─ Extract User Claims & Authorities    │
│ ├─ Verify Token expiration              │
│ └─ Route to Service with headers        │
└────────────────┬────────────────────────┘
                 │ Authenticated Request
┌────────────────▼────────────────────────┐
│  Microservice (Protected Endpoints)     │
│  ├─ User Service (Port 8081)            │
│  ├─ Activity Service (Port 8082)        │
│  └─ AI Service (Port 8083)              │
└─────────────────────────────────────────┘
```

### Token Flow
1. Client authenticates with Keycloak/OAuth2 provider
2. Receives JWT token
3. Includes token in Authorization header
4. API Gateway validates token
5. Routes request to appropriate microservice
6. Microservice processes authenticated request

---

## 📊 Key Metrics & Monitoring

### Health Endpoints
Each service exposes health via Spring Boot Actuator:
```bash
curl http://localhost:8081/actuator/health      # User Service
curl http://localhost:8082/actuator/health      # Activity Service
curl http://localhost:8083/actuator/health      # AI Service
curl http://localhost:8888/actuator/health      # Gateway
```

### Service Discovery Monitoring
```bash
curl http://localhost:8761/eureka/apps          # List all registered services
curl http://localhost:8761/eureka/apps/USER-SERVICE  # User Service details
```

### Config Server Monitoring
```bash
curl http://localhost:8888/user-service/default          # User Service config
curl http://localhost:8888/activity-service/default      # Activity Service config
curl http://localhost:8888/ai-service/default            # AI Service config
```

---

## 🎯 Project Structure

```
fitness-microservice/
├── eureka/                    # Service Registry
│   ├── pom.xml
│   ├── src/main/java/
│   └── src/main/resources/
│
├── configServer/              # Centralized Configuration
│   ├── pom.xml
│   ├── src/main/resources/config/
│   │   ├── activity-service.properties
│   │   ├── ai-service.properties
│   │   ├── user-service.properties
│   │   └── gateway.yml
│   └── src/main/java/
│
├── gateway/                   # API Gateway
│   ├── pom.xml
│   ├── src/main/java/
│   ├── src/main/resources/application.yml
│   └── src/test/java/
│
├── userService/               # User Management Microservice
│   ├── pom.xml
│   ├── src/main/java/
│   ├── src/main/resources/application.properties
│   └── src/test/java/
│
├── activityService/           # Activity Tracking Microservice
│   ├── pom.xml
│   ├── src/main/java/
│   ├── src/main/resources/application.properties
│   └── src/test/java/
│
├── aiService/                 # AI/ML Microservice
│   ├── pom.xml
│   ├── src/main/java/
│   ├── src/main/resources/application.properties
│   └── src/test/java/
│
└── README.md                  # This file
```

---

## 🎓 Learning Resources

### Spring Boot & Spring Cloud
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Cloud Gateway Guide](https://cloud.spring.io/spring-cloud-gateway/reference/html/)

### Microservices Patterns
- Service Discovery Pattern
- API Gateway Pattern
- Config Server Pattern
- Load Balancing Pattern

### Security
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Keycloak Documentation](https://www.keycloak.org/documentation)

---

## 🔄 Service Communication

### Inter-Service Communication Pattern
```
User Service → Activity Service
└─ Uses Eureka to discover ACTIVITY-SERVICE
└─ Makes REST calls to: http://ACTIVITY-SERVICE/api/...

Activity Service → AI Service
└─ Uses Eureka to discover AI-SERVICE
└─ Makes REST calls to: http://AI-SERVICE/api/...
```

### Example Inter-Service Call
```java
@FeignClient(name = "ACTIVITY-SERVICE")  // Uses Eureka to find service
public interface ActivityServiceClient {
    @GetMapping("/api/activities/{userId}")
    List<Activity> getUserActivities(@PathVariable String userId);
}
```

---

## 🚀 Future Enhancements

- [ ] Docker containerization (Dockerfile for each service)
- [ ] Docker Compose for easy local development
- [ ] Kubernetes deployment configs (YAML manifests)
- [ ] ELK Stack for logging (Elasticsearch, Logstash, Kibana)
- [ ] Prometheus + Grafana for metrics and monitoring
- [ ] Circuit breaker pattern (Hystrix/Resilience4j)
- [ ] Distributed tracing (Sleuth + Zipkin)
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Integration tests with TestContainers
- [ ] Performance testing (JMeter/Gatling)
- [ ] Security scanning (OWASP, SonarQube)

---

## 💡 Best Practices Implemented

✅ **Microservices Architecture** - Independent, deployable services  
✅ **Service Discovery** - Dynamic service location with Eureka  
✅ **API Gateway** - Single entry point for all clients  
✅ **Centralized Configuration** - Config Server for environment management  
✅ **OAuth2 Security** - Token-based authentication at gateway  
✅ **Database per Service** - Independent data storage  
✅ **Spring Data JPA** - Type-safe data access  
✅ **Lombok** - Reduced boilerplate code  
✅ **Non-blocking I/O** - WebFlux for scalability  
✅ **Health Checks** - Service health monitoring  

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**ehasan23** - Fitness Microservices Architecture

For questions or contributions, please open an issue or create a pull request.

---

## 📞 Support

If you encounter any issues:

1. Check if all services are registered in Eureka: http://localhost:8761
2. Verify Config Server is running: http://localhost:8888
3. Check service logs for errors
4. Ensure PostgreSQL is running and accessible
5. Verify all environment variables are set correctly

---

**Last Updated:** January 26, 2026
