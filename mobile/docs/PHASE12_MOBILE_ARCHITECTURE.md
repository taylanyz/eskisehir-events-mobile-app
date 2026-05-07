# Phase 12: Mobile App Re-Architecture for Thesis Quality

## Executive Summary

Phase 12 focuses on establishing a thesis-grade mobile architecture for the Eskişehir Events mobile application. The goal is to transform the Kotlin/Jetpack Compose client into a highly modular, maintainable, and user-friendly system with complete Turkish localization and standardized state management patterns.

**Status**: Complete ✅
**Technology Stack**: Kotlin, Jetpack Compose, MVVM, Hilt/Dagger, Retrofit, Coroutines, Room, DataStore

---

## 1. Mobile Architecture Overview

### 1.1 Architecture Layers

The mobile application is structured in four distinct layers following Clean Architecture principles:

```
┌─────────────────────────────────────────┐
│          UI Layer (Jetpack Compose)     │
│   (Screens, Components, ViewModels)     │
├─────────────────────────────────────────┤
│        Domain Layer (Use Cases)         │
│    (Business Logic, Interfaces)         │
├─────────────────────────────────────────┤
│      Data Layer (Repository Pattern)    │
│  (Remote API, Local Cache, Mappers)     │
├─────────────────────────────────────────┤
│     Infrastructure Layer (Platform)     │
│ (Networking, Database, DataStore)       │
└─────────────────────────────────────────┘
```

### 1.2 Key Design Principles

1. **Unidirectional Data Flow**: All data flows from the domain layer through the UI layer, with user actions flowing back through ViewModels
2. **Separation of Concerns**: Each module has a specific responsibility and minimal dependencies
3. **Testability**: All layers are independently testable through dependency injection
4. **Reusability**: Components, ViewModels, and utilities are designed for maximum reuse
5. **Maintainability**: Code organization facilitates quick navigation and modification

---

## 2. Package Structure

### 2.1 Complete Package Hierarchy

```
com.eskisehir.eventapp/
├── di/                          # Dependency Injection (Hilt modules)
│   ├── NetworkModule.kt         # Retrofit, OkHttp configuration
│   ├── RepositoryModule.kt      # Repository bindings
│   ├── DatabaseModule.kt        # Room database setup
│   ├── DataStoreModule.kt       # Secure preference store
│   └── ApiModule.kt             # API client configuration
│
├── data/                        # Data layer (Repository Pattern)
│   ├── repository/              # Repository implementations
│   │   ├── AuthRepository.kt
│   │   ├── UserRepository.kt
│   │   ├── POIRepository.kt
│   │   ├── RecommendationRepository.kt
│   │   ├── RouteRepository.kt
│   │   ├── FeedbackRepository.kt
│   │   └── InteractionRepository.kt
│   │
│   ├── remote/                  # API client and DTOs
│   │   ├── api/
│   │   │   ├── AuthApi.kt
│   │   │   ├── UserApi.kt
│   │   │   ├── POIApi.kt
│   │   │   ├── RecommendationApi.kt
│   │   │   ├── RouteApi.kt
│   │   │   └── FeedbackApi.kt
│   │   │
│   │   └── dto/                 # Data Transfer Objects
│   │       ├── UserDto.kt
│   │       ├── POIDto.kt
│   │       ├── RouteDto.kt
│   │       ├── RecommendationDto.kt
│   │       └── FeedbackDto.kt
│   │
│   ├── local/                   # Local database and cache
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── UserDao.kt
│   │   │   ├── POIDao.kt
│   │   │   ├── RouteDao.kt
│   │   │   └── InteractionDao.kt
│   │   │
│   │   ├── datastore/
│   │   │   └── PreferenceDataStore.kt
│   │   │
│   │   └── entity/              # Room entities
│   │       ├── UserEntity.kt
│   │       ├── POIEntity.kt
│   │       ├── RouteEntity.kt
│   │       └── InteractionEntity.kt
│   │
│   └── mapper/                  # DTO ↔ Domain model conversions
│       ├── UserMapper.kt
│       ├── POIMapper.kt
│       ├── RouteMapper.kt
│       └── RecommendationMapper.kt
│
├── domain/                      # Domain layer (Business logic)
│   ├── model/                   # Domain models (platform-agnostic)
│   │   ├── User.kt
│   │   ├── POI.kt
│   │   ├── Route.kt
│   │   ├── Recommendation.kt
│   │   ├── Feedback.kt
│   │   ├── UserPreferences.kt
│   │   └── RouteStep.kt
│   │
│   ├── repository/              # Repository interfaces
│   │   ├── IAuthRepository.kt
│   │   ├── IUserRepository.kt
│   │   ├── IPOIRepository.kt
│   │   ├── IRecommendationRepository.kt
│   │   ├── IRouteRepository.kt
│   │   ├── IFeedbackRepository.kt
│   │   └── IInteractionRepository.kt
│   │
│   └── usecase/                 # Use case implementations
│       ├── auth/
│       │   ├── LoginUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── CheckAuthStatusUseCase.kt
│       │
│       ├── user/
│       │   ├── GetUserProfileUseCase.kt
│       │   ├── UpdateUserPreferencesUseCase.kt
│       │   └── GetUserStatisticsUseCase.kt
│       │
│       ├── poi/
│       │   ├── SearchPOIUseCase.kt
│       │   ├── GetNearbyPOIUseCase.kt
│       │   └── GetPOIDetailsUseCase.kt
│       │
│       ├── recommendation/
│       │   ├── GetRecommendationsUseCase.kt
│       │   └── SaveRecommendationUseCase.kt
│       │
│       ├── route/
│       │   ├── GenerateRouteUseCase.kt
│       │   ├── GetSavedRoutesUseCase.kt
│       │   ├── SaveRouteUseCase.kt
│       │   └── DeleteRouteUseCase.kt
│       │
│       └── feedback/
│           └── SubmitFeedbackUseCase.kt
│
├── ui/                         # Presentation layer
│   ├── screens/                # Screen/Page composables
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt
│   │   │   └── RegisterScreen.kt
│   │   │
│   │   ├── onboarding/
│   │   │   ├── OnboardingScreen.kt
│   │   │   └── PreferenceSetupScreen.kt
│   │   │
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── QuickActionSection.kt
│   │   │
│   │   ├── discovery/
│   │   │   ├── DiscoveryScreen.kt
│   │   │   └── PlaceDetailScreen.kt
│   │   │
│   │   ├── recommendation/
│   │   │   ├── RecommendationScreen.kt
│   │   │   └── RecommendationDetailScreen.kt
│   │   │
│   │   ├── route/
│   │   │   ├── RouteDetailsScreen.kt
│   │   │   ├── RoutePreviewScreen.kt
│   │   │   ├── NavigationScreen.kt
│   │   │   ├── SavedRoutesScreen.kt
│   │   │   └── RouteFormScreen.kt
│   │   │
│   │   ├── map/
│   │   │   └── MapScreen.kt
│   │   │
│   │   ├── feedback/
│   │   │   └── FeedbackScreen.kt
│   │   │
│   │   └── profile/
│   │       ├── ProfileScreen.kt
│   │       └── PreferenceEditScreen.kt
│   │
│   ├── viewmodel/              # ViewModels (MVVM)
│   │   ├── AuthViewModel.kt
│   │   ├── HomeViewModel.kt
│   │   ├── DiscoveryViewModel.kt
│   │   ├── RecommendationViewModel.kt
│   │   ├── RouteViewModel.kt
│   │   ├── MapViewModel.kt
│   │   ├── FeedbackViewModel.kt
│   │   └── ProfileViewModel.kt
│   │
│   ├── components/             # Reusable Compose components
│   │   ├── common/
│   │   │   ├── AppBar.kt
│   │   │   ├── BottomNavigation.kt
│   │   │   ├── LoadingIndicator.kt
│   │   │   ├── ErrorDialog.kt
│   │   │   ├── EmptyStateView.kt
│   │   │   ├── OfflineBanner.kt
│   │   │   └── Button.kt
│   │   │
│   │   ├── poi/
│   │   │   ├── POICard.kt
│   │   │   ├── POIListItem.kt
│   │   │   ├── POICategoryFilter.kt
│   │   │   └── POIDetailCard.kt
│   │   │
│   │   ├── route/
│   │   │   ├── RouteCard.kt
│   │   │   ├── RouteListItem.kt
│   │   │   ├── RouteStepItem.kt
│   │   │   └── RouteSummary.kt
│   │   │
│   │   ├── recommendation/
│   │   │   ├── RecommendationCard.kt
│   │   │   ├── RecommendationReason.kt
│   │   │   └── MatchScoreIndicator.kt
│   │   │
│   │   └── forms/
│   │       ├── TextInputField.kt
│   │       ├── RatingComponent.kt
│   │       ├── CheckboxGroup.kt
│   │       └── SegmentedControl.kt
│   │
│   ├── navigation/             # Navigation structure
│   │   ├── NavGraph.kt
│   │   └── NavigationDestinations.kt
│   │
│   └── theme/                  # Compose theming
│       ├── Color.kt
│       ├── Typography.kt
│       ├── Shapes.kt
│       └── Theme.kt
│
├── navigation/                 # Navigation routes and deeplinks
│   ├── NavDestination.kt
│   ├── NavigationManager.kt
│   └── DeepLinkHandler.kt
│
├── util/                       # Utilities and helpers
│   ├── constants/
│   │   ├── ApiConstants.kt
│   │   ├── AppConstants.kt
│   │   └── ErrorMessages.kt
│   │
│   ├── extension/
│   │   ├── StringExtension.kt
│   │   ├── DateExtension.kt
│   │   ├── ModifierExtension.kt
│   │   └── CoroutineExtension.kt
│   │
│   ├── validator/
│   │   ├── InputValidator.kt
│   │   ├── EmailValidator.kt
│   │   └── PhoneValidator.kt
│   │
│   ├── logger/
│   │   └── AppLogger.kt
│   │
│   └── error/
│       ├── ErrorHandler.kt
│       └── ExceptionMapper.kt
│
├── EskisehirEventsApp.kt       # Application class
└── MainActivity.kt             # Main activity entry point
```

### 2.2 Module Responsibilities

| Module | Responsibility |
|--------|-----------------|
| `di/` | Provides all dependencies through Hilt |
| `data/` | Implements repository pattern, manages data sources |
| `domain/` | Contains business logic and use cases |
| `ui/` | Renders UI with Jetpack Compose |
| `navigation/` | Manages app navigation and deep links |
| `util/` | Provides utilities and helpers |

---

## 3. Core Screens Implementation

### 3.1 Screen Hierarchy

```
Splash Screen
    ↓
Auth State Check
    ├─→ Login Screen ─→ Register Screen
    │
    └─→ Onboarding ─→ Preference Setup
            ↓
        Main App
            ├─→ Home Screen
            ├─→ Discovery Screen → Place Detail
            ├─→ Recommendation Screen → Recommendation Detail
            ├─→ Route Management
            │   ├─→ Route Details
            │   ├─→ Navigation Screen
            │   ├─→ Saved Routes
            │   └─→ Route Form
            ├─→ Map Screen
            ├─→ Feedback Screen
            └─→ Profile Screen
```

### 3.2 Screen Specifications

#### Authentication Screens
- **LoginScreen**: Email/password entry, forgot password link, register redirect
- **RegisterScreen**: Full user registration form with validation
- **State Management**: AuthViewModel handles auth state and user session

#### Onboarding & Setup
- **OnboardingScreen**: 4-step carousel introducing key features (Turkish)
- **PreferenceSetupScreen**: Interest selection, budget range, transportation mode, crowd tolerance
- **Offline Availability**: Cached after first load

#### Main Application Screens
- **HomeScreen**: Dashboard with quick actions, featured places, popular today
- **DiscoveryScreen**: Search, filter, and browse POIs with category/sorting options
- **RecommendationScreen**: Display AI-generated recommendations with explanations
- **RouteDetailsScreen**: Display route summary, itinerary, and explainability
- **NavigationScreen**: Turn-by-turn navigation with map integration
- **SavedRoutesScreen**: List of user's saved routes with manage options
- **MapScreen**: Interactive map showing current location, places, and routes
- **FeedbackScreen**: Post-route feedback collection with rating and comments
- **ProfileScreen**: User profile, preferences, statistics, settings

### 3.3 Turkish Localization

All UI text is provided in Turkish through the strings resource files:
- `res/values-tr/strings.xml` - Turkish translations
- `res/values/strings.xml` - English fallback

**Key Turkish UI Categories**:
- Authentication labels and messages
- Screen titles and headers
- Button labels and CTAs
- Form placeholders and validation messages
- Empty state messages
- Error messages
- Navigation labels

---

## 4. State Management Architecture

### 4.1 MVVM Pattern with Unidirectional Data Flow

```
User Action (Click, Input)
    ↓
ViewModel Event Handler
    ↓
Use Case Execution
    ↓
Repository Call
    ↓
Data Response
    ↓
State Update
    ↓
UI Recomposition
```

### 4.2 ViewModel State Structure

Every ViewModel follows a consistent state structure:

```kotlin
// Sealed class for screen state
sealed class ScreenState {
    object Loading : ScreenState()
    data class Success(val data: T) : ScreenState()
    data class Error(val exception: Exception) : ScreenState()
    object Empty : ScreenState()
}

// ViewModel state holder
data class HomeUiState(
    val screenState: ScreenState = ScreenState.Loading,
    val featuredPlaces: List<POI> = emptyList(),
    val nearbyPlaces: List<POI> = emptyList(),
    val popularToday: List<POI> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)
```

### 4.3 Unidirectional Event Flow

**ViewModels expose three types of reactive properties**:

1. **State**: UI state using StateFlow
   ```kotlin
   private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Loading)
   val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
   ```

2. **Events**: One-time events using Channel
   ```kotlin
   private val _navigationEvent = Channel<NavigationEvent>()
   val navigationEvent: Flow<NavigationEvent> = _navigationEvent.receiveAsFlow()
   ```

3. **Effects**: Side effects using SharedFlow
   ```kotlin
   private val _effectFlow = MutableSharedFlow<UiEffect>()
   val effectFlow: SharedFlow<UiEffect> = _effectFlow.asSharedFlow()
   ```

### 4.4 Error State Management

Standardized error handling across all screens:

```kotlin
sealed class UiError {
    object NetworkError : UiError()
    object ServerError : UiError()
    object TimeoutError : UiError()
    object PermissionError : UiError()
    data class ValidationError(val message: String) : UiError()
    data class UnknownError(val message: String) : UiError()
}

// Displayed to user through ErrorDialog or SnackBar
```

### 4.5 Caching and Offline Support

**Data Layer implements caching strategy**:

1. **Network-First with Cache Fallback**:
   ```kotlin
   // Try network first
   val remoteData = apiCall()
   // Cache result
   cacheManager.save(remoteData)
   // Return remote or cache on failure
   ```

2. **Cache Validity**:
   - POI data: 1 hour
   - User preferences: 30 minutes
   - Recommendations: 15 minutes
   - Routes: 24 hours

3. **Offline Mode Detection**:
   ```kotlin
   // Check connection status
   if (connectivityManager.isOnline()) {
       // Perform network operations
   } else {
       // Use cached data
       showOfflineBanner()
   }
   ```

### 4.6 Side Effects and Navigation

**Navigation Events**:
```kotlin
sealed class NavigationEvent {
    data class NavigateTo(val destination: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    data class NavigateWithArgs(val destination: String, val args: Bundle) : NavigationEvent()
}

// Collected in UI:
LaunchedEffect(navigationEvent) {
    navigationEvent.collect { event ->
        when (event) {
            is NavigationEvent.NavigateTo -> navController.navigate(event.destination)
            // ...
        }
    }
}
```

---

## 5. API Integration Architecture

### 5.1 Retrofit Configuration

**Centralized API client setup through Hilt**:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(TokenInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

### 5.2 Error Handling Interceptor

```kotlin
class ErrorHandlingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())
            
            when (response.code) {
                401 -> {
                    // Handle unauthorized - refresh token or logout
                }
                500 -> {
                    // Handle server error
                }
                408 -> {
                    // Handle timeout
                }
            }
            
            return response
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}")
        }
    }
}
```

### 5.3 Request Mapping & DTOs

All API requests/responses use DTOs with proper mapping:

```kotlin
// DTO from API
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

// Domain model (platform-agnostic)
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String
)

// Mapper
fun UserDto.toDomain(): User = User(
    id = this.id,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName
)
```

---

## 6. Local Database & Caching

### 6.1 Room Database Setup

**SQLite-based local persistence with Room ORM**:

```kotlin
@Database(
    entities = [
        UserEntity::class,
        POIEntity::class,
        RouteEntity::class,
        InteractionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun poiDao(): POIDao
    abstract fun routeDao(): RouteDao
    abstract fun interactionDao(): InteractionDao
}
```

### 6.2 Data Store for Sensitive Data

**Encrypted preferences for tokens and preferences**:

```kotlin
class PreferenceDataStore @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.createEncryptedDataStore("preferences")
    
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }
    
    val authToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[AUTH_TOKEN_KEY] }
}
```

### 6.3 Cache Manager

**Intelligent cache invalidation**:

```kotlin
data class CacheEntry<T>(
    val data: T,
    val timestamp: Long,
    val ttlMillis: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > ttlMillis
    }
}

class CacheManager {
    fun <T> getCachedData(key: String): T? {
        val entry = cache[key] as? CacheEntry<T>
        return if (entry?.isExpired() == false) entry.data else null
    }
    
    fun <T> setCachedData(key: String, data: T, ttlMinutes: Int) {
        cache[key] = CacheEntry(data, System.currentTimeMillis(), ttlMinutes * 60 * 1000L)
    }
}
```

---

## 7. Dependency Injection with Hilt

### 7.1 Application-Wide DI Setup

```kotlin
@HiltAndroidApp
class EskisehirEventsApp : Application()

// In Activity/Fragment
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()
    // ...
}
```

### 7.2 Repository Bindings

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): IAuthRepository
    
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): IUserRepository
    
    @Binds
    abstract fun bindPOIRepository(
        impl: POIRepositoryImpl
    ): IPOIRepository
    
    // ... more bindings
}
```

---

## 8. Testing Architecture

### 8.1 Unit Testing

- ViewModels: Mock repositories, test state changes
- Use Cases: Mock dependencies, test business logic
- Repositories: Mock API and local sources

### 8.2 Integration Testing

- API integration: Mock API responses
- Database operations: In-memory database tests
- End-to-end flows: Test complete feature flows

### 8.3 UI Testing

- Compose tests: Test UI elements and interactions
- Navigation tests: Test screen transitions
- Accessibility tests: Verify a11y compliance

---

## 9. Security Considerations

### 9.1 Token Management

- Tokens stored in encrypted DataStore
- Token refresh mechanism with interceptor
- Automatic logout on token expiration

### 9.2 API Security

- HTTPS only communication
- Certificate pinning for sensitive endpoints
- Encrypted request/response bodies when needed

### 9.3 Local Data Security

- Room database encryption with SQLCipher (if needed for sensitive data)
- Secure preferences using EncryptedSharedPreferences
- Clear sensitive data on logout

---

## 10. Performance Optimization

### 10.1 Lazy Loading

```kotlin
@Composable
fun LazyRouteList(routes: List<Route>) {
    LazyColumn {
        items(routes, key = { it.id }) { route ->
            RouteCard(route = route)
        }
    }
}
```

### 10.2 Image Optimization

- Coil image loading with caching
- Size constraints in Compose
- Low resolution thumbnails for lists, high res for details

### 10.3 Memory Management

- ViewModel lifecycle awareness
- Proper Coroutine cancellation
- Resource cleanup in Effects

---

## 11. Accessibility & Localization

### 11.1 Turkish Localization

- **Complete Turkish Strings**: All UI text in Turkish via strings.xml
- **Numbers & Dates**: Turkish number formatting and date display
- **RTL-Ready**: Future support for right-to-left languages

### 11.2 Accessibility Features

- Content descriptions for all interactive elements
- Semantic Compose components for screen readers
- Sufficient color contrast (WCAG AA standard)
- Keyboard navigation support

---

## 12. Navigation Architecture

### 12.1 Navigation Graph

```kotlin
@Composable
fun EskisehirEventsNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController)
        onboardingNavGraph(navController)
        mainNavGraph(navController)
    }
}
```

### 12.2 Navigation Destinations

```kotlin
object Destinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val PREFERENCE_SETUP = "preference_setup"
    const val HOME = "home"
    const val DISCOVERY = "discovery"
    const val RECOMMENDATIONS = "recommendations"
    // ... more destinations
}
```

---

## 13. Phase 12 Deliverables Checklist

### 12.1 Mobile Package Structure
- [x] `data` layer with repositories, remote, local modules
- [x] `domain` layer with models and use cases
- [x] `ui` layer with screens, components, viewmodels
- [x] `navigation` module
- [x] `di` dependency injection modules

### 12.2 Core Screens
- [x] Splash Screen
- [x] Login / Register Screens
- [x] Onboarding (4-step)
- [x] Preference Setup Screen
- [x] Home Screen
- [x] Place Discovery Screen
- [x] Recommendation Results Screen
- [x] Route Details Screen
- [x] Map Screen
- [x] Saved Routes Screen
- [x] Feedback Screen
- [x] Profile Screen

### 12.3 Turkish UI Requirements
- [x] Complete Turkish strings.xml (Turkish)
- [x] English strings.xml (fallback)
- [x] All labels in Turkish
- [x] CTA text in Turkish
- [x] Empty state messages in Turkish
- [x] Error messages in Turkish
- [x] Onboarding aligned with thesis goals

### 12.4 UX and State Management
- [x] State management pattern standardized (MVVM + Unidirectional Flow)
- [x] Screen state differentiation (Loading, Success, Error, Empty)
- [x] Events vs State vs Effects architecture
- [x] API error state standardization
- [x] Error state UI components
- [x] Offline mode handling with cached data display
- [x] Loading states with spinners/progress
- [x] Empty state components

---

## 14. Implementation Guidelines

### 14.1 Creating a New Feature

1. **Define domain model** in `domain/model/`
2. **Create repository interface** in `domain/repository/`
3. **Implement repository** in `data/repository/`
4. **Create use case** in `domain/usecase/`
5. **Create ViewModel** in `ui/viewmodel/`
6. **Create Screen** in `ui/screens/`
7. **Create reusable components** in `ui/components/`
8. **Add to navigation** in `ui/navigation/`
9. **Wire dependencies** in `di/`

### 14.2 ViewModel Template

```kotlin
@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val myUseCase: MyUseCase,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
    
    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent: Flow<NavigationEvent> = _navigationEvent.receiveAsFlow()
    
    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = ScreenState.Loading
                val result = myUseCase.execute()
                _uiState.value = ScreenState.Success(result)
            } catch (e: Exception) {
                _uiState.value = ScreenState.Error(e)
            }
        }
    }
}
```

### 14.3 Composable Screen Template

```kotlin
@Composable
fun MyFeatureScreen(
    viewModel: MyFeatureViewModel = hiltViewModel(),
    onNavigate: (destination: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> onNavigate(event.destination)
                // Handle other events
            }
        }
    }
    
    when (uiState) {
        is ScreenState.Loading -> LoadingIndicator()
        is ScreenState.Success -> {
            val data = (uiState as ScreenState.Success).data
            FeatureContent(data = data)
        }
        is ScreenState.Error -> ErrorDialog()
        is ScreenState.Empty -> EmptyStateView()
    }
}
```

---

## 15. Version Control & Git Strategy

- Feature branches for each screen/feature
- Commit messages: `feat: add [feature name]` format
- Code reviews before merge to main
- Tagged releases at phase completion

---

## 16. Next Steps (Phase 13+)

1. **Phase 13**: Expand Eskişehir dataset with comprehensive POI data
2. **Phase 14**: Testing framework setup and automated tests
3. **Phase 15**: Analytics and performance monitoring
4. **Phase 16**: User acceptance testing and refinement

---

## Appendix: Key Dependencies Versions

```kotlin
// Compose
androidx.compose.ui:ui: 1.5.x
androidx.compose.material3:material3: 1.1.x

// Navigation
androidx.navigation:navigation-compose: 2.7.x

// State Management
androidx.lifecycle:lifecycle-viewmodel-compose: 2.7.x
androidx.lifecycle:lifecycle-runtime-compose: 2.7.x

// Networking
com.squareup.retrofit2:retrofit: 2.9.x
com.squareup.okhttp3:okhttp: 4.12.x

// Database
androidx.room:room-runtime: 2.6.x
androidx.datastore:datastore-preferences: 1.1.x

// DI
com.google.dagger:hilt-android: 2.51.x

// Async
org.jetbrains.kotlinx:kotlinx-coroutines-android: 1.8.x

// Image Loading
io.coil-kt:coil-compose: 2.5.x
```

---

**Document Status**: Complete and Ready for Implementation ✅

**Last Updated**: May 2026
**Phase**: 12 (Mobile Re-Architecture)
**Author**: AI Assistant (GitHub Copilot)
