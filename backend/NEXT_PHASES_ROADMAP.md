Merhaba, NEXT_PHASES_ROADMAP.md dosyasını okudum. 
PHASE 2.5 Mobile Authentication ile başlamak istiyorum, işlemlerine başla.

# Eskişehir Events Mobile App - Sonraki Fazlar Yol Haritası & Prompt

## 📋 Proje Durumu (22 Nisan 2026)

### ✅ Tamamlanan:
- **Phase 1**: Data Model (User, POI, Route, Events, Feedback entities)
- **Phase 2**: Backend Authentication & User System
  - JWT token management (access + refresh)
  - User registration, login
  - User preferences management
  - 15 integration test (ALL PASSING)
  - Environment variable externalization
- **Phase 2.5**: Mobile Integration Documentation & API Contract
  - PHASE2_5_MOBILE_INTEGRATION.md - Complete API specification
  - DTO models, endpoint examples, Bearer token pattern

### ⏳ Yapılması Gereken (Sırasıyla):

---

## PHASE 2.5 - MOBILE APP AUTHENTICATION INTEGRATION
**Priority**: 🔴 CRITICAL (Blocker for other mobile features)
**Estimated Duration**: 3-4 days
**Dependencies**: Phase 2 Backend (DONE)

### 2.5.1 - Mobile Project Structure Setup
- [ ] Verify Kotlin/Jetpack Compose project structure
- [ ] Check build.gradle.kts dependencies (Retrofit, OkHttp, Moshi/Gson, coroutines)
- [ ] Set up build variants for dev/prod environments
- [ ] Create app-level build configuration with API base URLs

### 2.5.2 - DTO Models Implementation
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/data/model/`
- [ ] Create `AuthResponse.kt` data class
- [ ] Create `UserResponse.kt` data class
- [ ] Create `RegisterRequest.kt` data class
- [ ] Create `LoginRequest.kt` data class
- [ ] Create `RefreshTokenRequest.kt` data class
- [ ] Create `PreferenceUpdateRequest.kt` data class
- [ ] Add @Parcelize annotations for navigation argument passing
- [ ] Reference: PHASE2_5_MOBILE_INTEGRATION.md for exact field definitions

### 2.5.3 - Retrofit API Service
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/data/remote/`
- [ ] Create `AuthApi.kt` interface with 3 endpoints:
  - `@POST("auth/register")` with RegisterRequest
  - `@POST("auth/login")` with LoginRequest
  - `@POST("auth/refresh")` with RefreshTokenRequest
- [ ] Create `UserApi.kt` interface with 2 endpoints:
  - `@GET("users/me")` 
  - `@PUT("users/preferences")` with PreferenceUpdateRequest
- [ ] Add error response handling (ErrorResponse DTO for 400/401/409/404)
- [ ] Configure Retrofit with base URL from BuildConfig

### 2.5.4 - Local Token Storage
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/data/local/`
- [ ] Create `TokenStore.kt` using DataStore<Preferences>
  - Store: accessToken, refreshToken, userId, email, displayName
  - Use encrypted DataStore for security
- [ ] Create `TokenManager.kt` with methods:
  - `saveTokens(authResponse: AuthResponse)`
  - `getAccessToken(): String?`
  - `getRefreshToken(): String?`
  - `clearTokens()`
  - `isUserLoggedIn(): Boolean`

### 2.5.5 - Authentication Service (Business Logic)
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/domain/usecase/`
- [ ] Create `RegisterUseCase.kt`
- [ ] Create `LoginUseCase.kt`
- [ ] Create `RefreshTokenUseCase.kt`
- [ ] Create `LogoutUseCase.kt` (clear local storage)
- [ ] Create `GetCurrentUserUseCase.kt`
- [ ] Create `UpdatePreferencesUseCase.kt`
- [ ] All should return `Result<T>` sealed class for error handling

### 2.5.6 - HTTP Interceptor & Token Management
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/data/remote/`
- [ ] Create `AuthInterceptor.kt` that:
  - Adds "Authorization: Bearer {token}" to all requests
  - Handles 401 Unauthorized responses
  - Attempts token refresh when 401 received
  - Retries original request with new token
  - Redirects to login if refresh fails
- [ ] Create `ErrorHandlingInterceptor.kt` for logging/debugging

### 2.5.7 - ViewModel & State Management (MVVM)
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/viewmodel/`
- [ ] Create `AuthViewModel.kt` with LiveData<UIState>:
  - `register(email, displayName, password): Flow<Result<AuthResponse>>`
  - `login(email, password): Flow<Result<AuthResponse>>`
  - `logout()`
  - Error state handling (ValidationError, NetworkError, ServerError)
- [ ] Create `UserViewModel.kt`:
  - `getCurrentUser(): Flow<Result<UserResponse>>`
  - `updatePreferences(request): Flow<Result<Unit>>`

### 2.5.8 - UI Layer (Jetpack Compose)
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/screen/`
- [ ] Create `LoginScreen.kt` composable:
  - Email input field with validation
  - Password input field
  - Login button with loading state
  - Error message display
  - "Register" navigation link
- [ ] Create `RegisterScreen.kt` composable:
  - Email, Display Name, Password input fields with validation
  - Register button with loading state
  - Back navigation
  - Error message display
- [ ] Create `ProfileScreen.kt` composable:
  - Display current user info (email, displayName, createdAt)
  - Edit preferences section
  - Logout button
- [ ] Create `PreferencesScreen.kt` composable:
  - Category selection (multi-select)
  - Tags input (chip-based)
  - Sliders for: budgetSensitivity, crowdTolerance, sustainabilityPreference
  - Spinner for mobilityPreference
  - Number input for maxWalkingMinutes
  - Save button with validation

### 2.5.9 - Navigation Integration
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/navigation/`
- [ ] Create `AuthNavGraph.kt` for auth flows
- [ ] Create `MainNavGraph.kt` for authenticated flows
- [ ] Implement conditional navigation based on authentication state
- [ ] Handle deep linking for auth flows (if needed)

### 2.5.10 - Unit & Integration Tests (Mobile)
**Location**: `mobile/app/src/test/java/com/eskisehir/eventapp/`
- [ ] AuthViewModelTest - Mock AuthService, test register/login flows
- [ ] UserViewModelTest - Mock UserService, test profile operations
- [ ] TokenManagerTest - Test token storage/retrieval
- [ ] AuthInterceptorTest - Test token refresh logic
- [ ] Integration tests with MockWebServer for Retrofit testing

### 2.5.11 - Error Handling & User Feedback
- [ ] Implement proper exception mapping (NetworkException, ValidationException, etc.)
- [ ] Show Toast/Snackbar for user feedback
- [ ] Implement retry logic for network failures
- [ ] Handle edge cases (expired token during app restart, network loss, etc.)

### 2.5.12 - Testing on Backend
- [ ] Test register → login → getProfile → updatePreferences flow end-to-end
- [ ] Test token refresh scenario
- [ ] Test logout (clear local tokens)
- [ ] Test invalid credentials handling
- [ ] Test network timeout scenarios

---

## PHASE 3 - RECOMMENDATION ENGINE (Contextual Bandit Algorithm)
**Priority**: 🟠 HIGH (Core feature)
**Estimated Duration**: 5-7 days
**Dependencies**: Phase 2.5 Mobile (DONE)

### 3.1 - Backend: Bandit Algorithm Implementation
**Location**: `backend/src/main/java/com/eskisehir/eventapi/domain/algorithm/`
- [ ] Create `ContextualBandit.java` class implementing Thompson Sampling
- [ ] Implement `Arm.java` - represents a POI with its statistics
- [ ] Implement `BetaDistribution.java` - for Thompson Sampling
- [ ] Create `RecommendationStrategy.java` interface
- [ ] Implement `ThompsonSamplingStrategy.java`
- [ ] Add UCB (Upper Confidence Bound) alternative strategy option

### 3.2 - Backend: Recommendation Service
**Location**: `backend/src/main/java/com/eskisehir/eventapi/service/`
- [ ] Create `RecommendationService.java`:
  - `getRecommendations(userId, count, userLocation): List<POI>`
  - `recordInteraction(userId, poiId, eventId, feedback): void`
  - `updateBanditModel(userId): void`
- [ ] Implement user context extraction:
  - User preferences (categories, tags, budget, crowd tolerance)
  - Current location
  - Time of day
  - Day of week
  - Weather (optional integration)

### 3.3 - Backend: Interaction Logging
**Location**: `backend/src/main/java/com/eskisehir/eventapi/service/`
- [ ] Create `InteractionService.java`:
  - `logView(userId, poiId): InteractionLog`
  - `logBookmark(userId, poiId): void`
  - `logShare(userId, poiId): void`
  - `logNegativeFeedback(userId, poiId, reason): void`
- [ ] Store in UserInteraction entity
- [ ] Update bandit model statistics after each interaction

### 3.4 - Backend: POI Endpoint Enhancement
**Location**: `backend/src/main/java/com/eskisehir/eventapi/controller/`
- [ ] Create `GET /api/recommendations` endpoint:
  - Query params: `count` (default: 10), `latitude`, `longitude`, `categoryFilter`
  - Response: List<POIResponse> with ranking scores
- [ ] Create `POST /api/interactions` endpoint:
  - Log view/bookmark/share/feedback interactions
  - Trigger bandit model update
- [ ] Add `GET /api/recommendations/trending` endpoint for cold-start users

### 3.5 - Backend: Cold Start Strategy
**Location**: `backend/src/main/java/com/eskisehir/eventapi/service/`
- [ ] Implement `ColdStartStrategy.java`:
  - Use POI popularity (view count) for new users
  - Use trending events for new users
  - Use user preferences to filter popular POIs
  - Gradually transition to contextual bandit as interactions accumulate

### 3.6 - Backend: Bandit Model Persistence
- [ ] Design database schema for bandit statistics:
  - Table: `bandit_arm_stats` (user_id, poi_id, alpha, beta, plays, wins)
  - Table: `user_interaction_log` (user_id, poi_id, event_id, interaction_type, timestamp, rating)
- [ ] Create `BanditStatsRepository`
- [ ] Implement model serialization/loading

### 3.7 - Backend: Integration Tests for Recommendations
**Location**: `backend/src/test/java/com/eskisehir/eventapi/service/`
- [ ] RecommendationServiceTest
  - Test Thompson Sampling distribution
  - Test cold-start recommendations
  - Test context filtering (category, budget, etc.)
  - Test bandit model update after feedback
- [ ] InteractionServiceTest
  - Test interaction logging
  - Test bandit stats updates

### 3.8 - Mobile: Recommendation UI
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/screen/`
- [ ] Create `RecommendationsScreen.kt` composable:
  - Display list of recommended POIs
  - Show ranking score/confidence level (optional)
  - Implement infinite scroll/pagination
  - Handle loading states
- [ ] Create `RecommendationCard.kt` composable:
  - POI image, name, category, distance
  - "View Details" button
  - "Save" bookmark button
  - Rating display

### 3.9 - Mobile: Interaction Tracking
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/data/remote/`
- [ ] Create `InteractionService.kt` for:
  - `logView(poiId, eventId)`
  - `logBookmark(poiId)`
  - `logShare(poiId)`
  - `logFeedback(poiId, rating, comment)`
- [ ] Implement automatic interaction logging on:
  - POI card view (use SnapshotStateList to track)
  - Detail screen open
  - Bookmark action
  - Share action

### 3.10 - Mobile: Feedback UI
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/screen/`
- [ ] Create feedback dialog/bottom sheet:
  - Star rating (1-5)
  - Optional comment field
  - "Helpful" / "Not Helpful" quick buttons
  - Submit button

### 3.11 - Testing Recommendation Flow
- [ ] E2E test: User interacts with 5+ POIs → recommendations improve
- [ ] Test: Preferences change → recommendations update
- [ ] Test: Cold start (new user) → gets popularity-based recommendations
- [ ] Test: Warm start (established user) → gets personalized recommendations

---

## PHASE 4 - ROUTE PLANNING & OPTIMIZATION
**Priority**: 🟡 MEDIUM (Complex feature)
**Estimated Duration**: 5-6 days
**Dependencies**: Phase 3 Recommendations (DONE)

### 4.1 - Backend: Route Planning Service
**Location**: `backend/src/main/java/com/eskisehir/eventapi/domain/algorithm/`
- [ ] Implement `RoutePlanner.java` using Nearest Neighbor + 2-opt optimization
- [ ] Implement `TSPSolver.java` (Traveling Salesman Problem) for optimal route
- [ ] Consider constraints:
  - Walking time between POIs
  - Maximum total walking time (from user preference)
  - Operating hours of POIs
  - Category preferences
  - Budget constraints

### 4.2 - Backend: Route Generation Endpoint
**Location**: `backend/src/main/java/com/eskisehir/eventapi/controller/`
- [ ] Create `POST /api/routes/generate` endpoint:
  - Input: startLocation, duration, categories, maxWalkingTime, budget
  - Output: Optimized route with POIs, directions, estimated times
- [ ] Create `GET /api/routes/{routeId}` endpoint:
  - Get saved route details

### 4.3 - Backend: Distance & Navigation Integration
- [ ] Integrate with OpenStreetMap/Overpass for:
  - Distance calculations between POIs
  - Walking routes/pathfinding
  - Estimated walking time
- [ ] Create `GeoService.java` for distance calculations

### 4.4 - Mobile: Route Generation UI
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/screen/`
- [ ] Create `RouteGeneratorScreen.kt`:
  - Start location picker
  - Duration slider (1-6 hours)
  - Category multi-select filter
  - Budget range slider
  - Generate button with loading state

### 4.5 - Mobile: Route Display & Navigation
**Location**: `mobile/app/src/main/java/com/eskisehir/eventapp/ui/screen/`
- [ ] Create `RouteDetailScreen.kt`:
  - Map showing POIs and route path
  - POI list with order numbers
  - Step-by-step directions
  - Total walking time/distance
  - Save route button
- [ ] Integrate with Maps API (Google Maps or OSM)

### 4.6 - Mobile: Turn-by-Turn Navigation (Advanced)
- [ ] Create `NavigationScreen.kt` for active navigation:
  - Real-time location tracking
  - Next waypoint highlighting
  - Arrival detection
  - Re-route on deviation

---

## PHASE 5 - ADVANCED FEATURES & POLISH
**Priority**: 🟢 LOW (Nice-to-have)
**Estimated Duration**: 4-5 days
**Dependencies**: Phase 4 (DONE)

### 5.1 - Weather Integration
- [ ] Integrate OpenWeatherMap API
- [ ] Show weather conditions on recommendation cards
- [ ] Adjust recommendations based on weather (indoor events on rain)

### 5.2 - Social Features
- [ ] User-to-user route sharing
- [ ] Route ratings/comments
- [ ] Popular routes based on community
- [ ] Follower/following system (optional)

### 5.3 - Advanced Filtering
- [ ] Filter by price range, distance, rating
- [ ] Saved searches
- [ ] Advanced category/tag combinations

### 5.4 - Offline Support
- [ ] Cache POI data locally
- [ ] Offline map tiles (OSM)
- [ ] Offline route availability

### 5.5 - Analytics & Performance
- [ ] Backend: Add request logging and metrics
- [ ] Mobile: Crash reporting (Firebase)
- [ ] Performance monitoring

---

## 🛠️ TECHNICAL DEPENDENCIES & SETUP

### Backend (Already Done ✅)
- Spring Boot 3.4.4
- Java 21
- Spring Security + JWT
- Spring Data JPA
- H2 (dev) / PostgreSQL (prod)
- Maven

### Mobile (To Setup)
```gradle
// Retrofit + OkHttp
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Moshi/Gson for JSON
implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

// Jetpack
implementation("androidx.datastore:datastore-preferences:1.1.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

// Maps
implementation("com.google.android.gms:play-services-maps:18.2.0")

// Hilt DI
implementation("com.google.dagger:hilt-android:2.51.1")
kapt("com.google.dagger:hilt-compiler:2.51.1")
```

---

## 📅 TIMELINE ESTIMATE

| Phase | Duration | Start | End |
|-------|----------|-------|-----|
| 2.5 Mobile Auth | 3-4 days | Day 23 | Day 26 |
| 3 Recommendations | 5-7 days | Day 27 | Day 33 |
| 4 Route Planning | 5-6 days | Day 34 | Day 40 |
| 5 Polish & Features | 4-5 days | Day 41 | Day 45 |
| **Total** | **17-22 days** | **Day 23** | **Day 45** |

---

## ✅ DAILY WORKFLOW TEMPLATE (for next session)

```
MORNING (Start of Day):
1. Read previous session notes: /memories/session/
2. Check last test run results
3. Review TODO list for current phase
4. Set 1-3 concrete goals for the day

DURING WORK:
1. Mark tasks as in-progress before starting
2. Run tests after each feature completion
3. Commit changes regularly
4. Update session memory with progress

END OF DAY:
1. Mark completed tasks as done
2. Write session summary:
   - What was accomplished
   - What's blocking further progress
   - What to start with tomorrow
3. Push all changes to git
4. Save memory notes for continuation
```

---

## 🔑 KEY DECISIONS TO MAKE

1. **Recommendation Algorithm**: Thompson Sampling vs UCB vs other?
2. **Route Optimization**: Greedy NN+2opt vs exact TSP solver vs heuristic?
3. **Map Provider**: Google Maps vs OSM vs Mapbox?
4. **Offline Support**: Include in Phase 4 or Phase 5?
5. **Authentication Cache**: How long to keep tokens locally?

---

## 📞 QUICK REFERENCE

- **Backend Tests**: `cd backend && ./mvnw test`
- **Mobile Build**: `cd mobile && ./gradlew build`
- **Docs**: See `PHASE2_5_MOBILE_INTEGRATION.md` for API contracts
- **Architecture**: MVVM + Clean Architecture pattern
- **State**: Use ViewModel + LiveData/StateFlow
- **Threading**: Coroutines for all async operations

---

**Bu prompt'u kullanarakı yarın aşağıdaki işlemleri yapabilirsiniz:**

1. Yukarıdaki fazlardan birini seçin (önerilen sıra: 2.5 → 3 → 4 → 5)
2. `İşlemlerine başla` diyerek başlayın
3. Sistem otomatik olarak proje analizi yapacak ve spesifik görevleri başlatacak

---

*Son güncelleme: 22 Nisan 2026*
*Hazırlayan: Claude (Faz 2 Implementation Complete)*
