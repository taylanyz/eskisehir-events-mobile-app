# Phase 6 Module Boundaries

Bu doküman, mevcut backend ve mobile codebase içinde hangi modülün hangi sorumluluğu taşıdığını ve sınır ihlallerinden kaçınmak için hangi kuralların izleneceğini tanımlar.

## 1. Boundary Rules

- Controller katmanı domain kararlarını sahiplenmez; yalnızca request/response orkestrasyonu yapar.
- Service katmanı iş kurallarını taşır.
- Repository katmanı yalnızca persistence erişimi yapar.
- Algorithm ve optimizer katmanları HTTP, DTO veya UI bağımlılığı taşımaz.
- Mobile composable'lar API çağrısı değil, ViewModel state tüketir.
- Recommendation çıktısı route optimization'a yalnızca candidate pool olarak girer.

## 2. Backend Ownership Map

## 2.1 Auth
- Owns: authentication, JWT token flow, access control
- Files:
  - `controller/AuthController`
  - `service/AuthService`
  - `security/*`

## 2.2 User Profile
- Owns: user info, preference update, preference persistence
- Files:
  - `controller/UserController`
  - `service/UserService`
  - `domain/model/User`, `UserPreference`

## 2.3 POI Discovery
- Owns: search, category filter, district filter, advanced filters
- Files:
  - `controller/PoiController`
  - `service/PoiService`
  - `service/AdvancedFilterService`
  - `domain/model/Poi`

## 2.4 Recommendation
- Owns: candidate generation, ranking, cold-start behavior, trending fallback
- Files:
  - `controller/RecommendationController`
  - `service/RecommendationEngine`
  - `service/RecommendationService`
  - `algorithm/RecommendationStrategy`
  - `algorithm/ColdStartStrategy`
  - `algorithm/ThompsonSamplingStrategy`

## 2.5 Interaction and Learning
- Owns: event logging, reward mapping, bandit updates
- Files:
  - `controller/InteractionController`
  - `service/InteractionService`
  - `repository/BanditStatsRepository`
  - `repository/BanditEventRepository`
  - `domain/model/UserInteraction`, `BanditEvent`, `BanditArmStat`

## 2.6 Route and Optimization
- Owns: route creation, route metrics, social route operations, navigation steps
- Files:
  - `controller/RouteController`
  - `service/RoutePlanner`
  - `service/RouteService`
  - `service/GeoService`
  - `domain/model/Route`, `RouteItem`, `RouteRating`, `NavigationStep`

## 2.7 Context Enrichment
- Owns: weather enrichment, cache abstraction, contextual augmentation
- Files:
  - `service/WeatherService`
  - `service/CacheService`
  - `data/model/WeatherData`

## 2.8 Analytics
- Owns: application metrics, request tracking, operational summaries
- Files:
  - `controller/AnalyticsController`
  - `service/AnalyticsService`

## 2.9 Future NLP
- Owns: sentiment extraction from Turkish feedback, feature signal extraction
- Reserved package:
  - `nlp/*`

## 3. Mobile Ownership Map

## 3.1 Data Layer
- `data/model`: DTOs and app-facing transfer models
- `data/remote`: Retrofit APIs and interceptors
- `data/local`: token storage and future offline persistence

## 3.2 Domain Layer
- `domain/usecase`: app-specific actions such as login, recommendation fetch, route generation
- `domain/Result`: standardized success/failure envelope

## 3.3 Presentation Layer
- `ui/viewmodel`: state holders and orchestration
- `ui/screens`: screen-level composables
- `ui/components`: reusable UI atoms/molecules
- `navigation`: navigation graph and route contracts

## 4. Known Structural Issues to Address Later

- Legacy package duplication exists between `com.eskisehir.events` and `com.eskisehir.eventapp` on the mobile side.
- Theme and some old presentation classes still live in legacy namespaces.
- Later refactor should consolidate packages under a single root.

## 5. Definition of Done for Boundary Compliance

- No controller directly performs algorithmic ranking logic.
- No optimizer class depends on DTO classes.
- No composable directly calls Retrofit services.
- Recommendation services expose ranked POIs, not pre-baked routes.
- Route services accept candidate inputs and constraints, not user feedback logic.