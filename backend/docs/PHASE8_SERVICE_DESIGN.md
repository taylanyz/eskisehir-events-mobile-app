# Phase 8: Backend Service Refactor and Thesis-Ready Architecture

## Overview

Phase 8 focuses on transforming the existing Spring Boot backend into a production-ready, thesis-quality system with clear service boundaries, comprehensive validation, and performance optimization.

**Foundation**: PostgreSQL + Flyway ready (Phase 7), H2 for dev/test, Java 25 compatible

## 1. Package Structure Refinement

```
src/main/java/com/eskisehir/eventapi/
├── config/                    # Spring configuration, security, DB, caching
│   ├── SecurityConfig.java
│   ├── ApplicationConfig.java
│   ├── WebConfig.java
│   ├── CacheConfig.java
│   └── DataSeeder.java
├── controller/                # REST API endpoints (7-10 controllers)
│   ├── AuthController.java
│   ├── UserController.java
│   ├── PoiController.java
│   ├── RecommendationController.java
│   ├── RouteController.java
│   ├── InteractionController.java
│   └── FeedbackController.java
├── domain/
│   ├── model/                 # JPA entities
│   ├── enum/                  # Enums: Category, BudgetLevel, etc.
│   └── constant/              # Domain constants
├── dto/                       # Request/Response DTOs with validation
│   ├── request/
│   ├── response/
│   └── mapper/                # MapStruct or manual mappers
├── repository/                # Spring Data JPA repositories (11 repos)
│   ├── UserRepository.java
│   ├── PoiRepository.java
│   ├── RouteRepository.java
│   └── ... (8 more)
├── service/                   # Business logic (layer 1: core services)
│   ├── UserService.java
│   ├── PoiService.java
│   ├── RouteService.java
│   ├── WeatherService.java
│   └── InteractionService.java
├── algorithm/                 # Algorithm-specific logic
│   ├── recommendation/
│   │   ├── ContentBasedFilter.java
│   │   ├── CandidateGenerator.java
│   │   └── RankingEngine.java
│   ├── optimizer/
│   │   ├── RouteOptimizer.java
│   │   └── TransitRouteOptimizer.java
│   └── scoring/
│       ├── PreferenceScorer.java
│       └── SustainabilityScorer.java
├── nlp/                       # NLP and feedback analysis
│   ├── FeedbackAnalyzer.java
│   └── SentimentScorer.java
├── integration/               # External API integrations
│   ├── WeatherApiClient.java
│   ├── OpenWeatherMapClient.java
│   └── OpenRouteServiceClient.java
├── metrics/                   # Application metrics and monitoring
│   ├── MetricsCollector.java
│   └── PerformanceMonitor.java
├── exception/                 # Custom exceptions
│   ├── ApiException.java
│   ├── NotFoundException.java
│   ├── ValidationException.java
│   └── ExceptionHandler.java
├── security/                  # JWT, authentication, authorization
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── AuthenticationProvider.java
└── util/                      # Utility classes
    ├── GeoUtil.java
    ├── DateUtil.java
    └── ValidationUtil.java
```

## 2. Core Service Classes

### 2.1 RecommendationService
**Responsibility**: Orchestrate recommendation pipeline

```java
public class RecommendationService {
  - generateRecommendations(userId, context): List<PoiRecommendation>
    1. Fetch user profile + preferences
    2. Call CandidateGenerator for pool expansion
    3. Call ContentBasedFilter for initial scoring
    4. Apply contextual bandit selection
    5. Rank and return top N
  - updateRecommendationFeedback(...)
  - getRecommendationMetrics(...)
}
```

### 2.2 RouteService
**Responsibility**: Route generation and optimization

```java
public class RouteService {
  - generateRoute(userId, poiList, constraints): Route
  - optimizeRoute(route): OptimizedRoute
  - validateRoute(route): ValidationResult
  - calculateRouteCost(route): RouteCost
  - getRatedRoutes(userId, minRating): List<Route>
}
```

### 2.3 PoiService
**Responsibility**: POI discovery, search, and filtering

```java
public class PoiService {
  - searchPois(searchRequest): List<Poi>
  - filterByCategory(category): List<Poi>
  - filterByBudget(budget): List<Poi>
  - getPoiDetails(poiId): PoiDetail
  - getNearblyPois(lat, lon, radiusKm): List<Poi>
}
```

### 2.4 UserService
**Responsibility**: User profile and preference management

```java
public class UserService {
  - createUser(registrationRequest): User
  - getUserProfile(userId): UserProfile
  - updatePreferences(userId, preferencesDto): User
  - getUserHistory(userId): UserHistory
}
```

## 3. REST API Contract (Tier 1: Endpoints)

### Authentication Endpoints
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and receive JWT
- `POST /auth/refresh` - Refresh JWT token
- `POST /auth/logout` - Invalidate token

### User Endpoints
- `GET /users/me` - Current user profile
- `PUT /users/preferences` - Update preferences
- `GET /users/{id}/history` - User activity history
- `GET /users/{id}/routes` - User's saved routes

### POI Endpoints
- `GET /pois` - List all POIs (with pagination)
- `GET /pois/search` - Search POIs
- `GET /pois/{id}` - POI detail
- `GET /pois/nearby` - Nearby POIs (geolocation)
- `GET /pois/category/{category}` - POIs by category

### Recommendation Endpoints
- `GET /recommendations` - Get personalized recommendations
- `GET /recommendations/{id}/details` - Recommendation details
- `POST /recommendations/{id}/feedback` - Rate a recommendation

### Route Endpoints
- `POST /routes/generate` - Generate optimized route
- `GET /routes/{id}` - Route details
- `PUT /routes/{id}` - Update route
- `DELETE /routes/{id}` - Delete route
- `POST /routes/{id}/rate` - Rate route
- `GET /routes/shared/{shareCode}` - Access shared route

### Interaction Endpoints
- `POST /interactions` - Log user interaction
- `GET /interactions/analytics` - User analytics

### Feedback Endpoints
- `POST /feedback` - Submit feedback
- `GET /feedback/summary` - Aggregated feedback

## 4. DTO Validation Strategy

### Input Validation Approach
- Use Jakarta Bean Validation (formerly javax.validation)
- Annotations: @NotBlank, @Min, @Max, @Email, etc.
- Custom validators for domain logic

### Example DTOs

```java
@Data
public class RecommendationRequest {
  @NotNull
  private Long userId;
  
  @NotEmpty
  private List<@NotBlank String> preferredCategories;
  
  @Min(0) @Max(5000)
  private Integer maxBudget;
  
  @Min(0) @Max(480)
  private Integer maxMinutesToTravel;
}

@Data
public class RouteGenerationRequest {
  @NotEmpty
  private List<@NotNull Long> poiIds;
  
  @NotNull
  @Positive
  private Double startLat;
  
  @NotNull
  @Positive
  private Double startLon;
  
  @NotNull
  private TransportMode transportMode;
}
```

## 5. Global Exception Handling

### Exception Hierarchy
```
ApiException (extends RuntimeException)
├── ResourceNotFoundException
├── ValidationException
├── UnauthorizedException
├── ForbiddenException
├── BadRequestException
└── InternalServerException
```

### Error Response Contract
```json
{
  "timestamp": "2026-04-29T20:35:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": {
    "field": "maxBudget",
    "rejectedValue": -100,
    "message": "must be greater than or equal to 0"
  },
  "traceId": "uuid"
}
```

## 6. Caching Strategy

### Cache Layers

**Layer 1: POI Cache**
- Key: `poi::{poiId}`
- TTL: 1 hour
- Invalidation: Update/delete POI
- Use case: High-read, low-write

**Layer 2: Recommendation Cache**
- Key: `recommendation::{userId}::{contextHash}`
- TTL: 30 minutes
- Invalidation: Explicit (user preference change, interaction)
- Use case: Same user, same context = same recommendations

**Layer 3: Weather Cache**
- Key: `weather::{lat}::{lon}`
- TTL: 30 minutes
- Invalidation: Time-based

**Implementation**: Spring Cache Abstraction with Redis backend
```yaml
# application-prod.properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

## 7. Performance Optimization Points

### Database Query Optimization
- Implement pagination (default: 20 items/page)
- Use `@EntityGraph` for N+1 prevention
- Index frequently searched columns
- Lazy loading where appropriate

### Rate Limiting
- 100 requests/minute per user for public endpoints
- 1000 requests/minute for authenticated endpoints
- Implement via Spring Cloud Config/Resilience4j

### Async Processing
- Heavy computation: Recommendation generation (>5s) → async
- Batch processing: Analytics aggregation → scheduled job (nightly)
- Feedback processing: Sentiment analysis → async

## 8. Testing Strategy

### Unit Tests
- Service layer: Mockito + JUnit 5
- Algorithm layer: Pure logic tests
- Target: 80% code coverage

### Integration Tests
- Repository tests with TestContainers (PostgreSQL)
- Controller tests with MockMvc
- End-to-end scenarios

### Test Data
- Use `@Sql` for test data setup
- Separate test database profile

## 9. Monitoring and Metrics

### Application Metrics to Track
- Recommendation generation latency (p50, p95, p99)
- Route optimization time
- Cache hit ratio
- API endpoint latency by path
- Error rate by endpoint
- User activity counts

### Implementation
- Micrometer + Prometheus
- Custom metrics via `@Timed`, `@Counted`

## 10. Deliverables Checklist

- [ ] Refactored package structure deployed
- [ ] All service classes with clear responsibilities
- [ ] 15+ REST endpoints with OpenAPI documentation
- [ ] Validation framework integrated
- [ ] Global exception handler implemented
- [ ] Caching layer configured and tested
- [ ] Performance benchmarks documented
- [ ] Unit test coverage >80%
- [ ] Integration tests passing
- [ ] Monitoring and metrics in place
- [ ] API contract documentation (Swagger/OpenAPI)

## 11. Timeline Estimate

- **Week 1**: Package refactoring, service class creation
- **Week 2**: REST endpoint implementation, DTO validation
- **Week 3**: Exception handling, caching integration
- **Week 4**: Testing, documentation, optimization
- **Week 5**: Performance tuning, monitoring setup

## Next Phase (Phase 9)

Phase 9 will deepen the recommendation algorithm with advanced AI/ML techniques:
- Content-based filtering
- Contextual bandit implementation
- NLP sentiment analysis
- Advanced scoring functions
