# Phase 12: State Management Guidelines

## Overview

This document defines the standardized state management architecture for the Eskişehir Events mobile application using MVVM pattern with Unidirectional Data Flow (UDF).

**Core Principle**: All state flows from the domain/data layers upward through ViewModels to the UI, with user actions flowing back through the ViewModel event handlers.

---

## 1. State Management Architecture

### 1.1 Three-Layer Architecture

```
┌──────────────────────────────┐
│    UI Layer (Composables)    │ ← Displays state & receives user actions
├──────────────────────────────┤
│   ViewModel Layer (MVVM)     │ ← Manages state, processes events, emits effects
├──────────────────────────────┤
│  Domain/Data Layer           │ ← Business logic, repositories, use cases
└──────────────────────────────┘
```

### 1.2 Data Flow Pattern

```
User Action (Click, Input)
        ↓
ViewModel Event Handler
        ↓
Use Case Execution
        ↓
Repository Operation
        ↓
Data Layer Response
        ↓
ViewModel Updates State
        ↓
UI Recomposition
```

---

## 2. Core State Management Concepts

### 2.1 State (UI State)

**Definition**: Represents the current screen/feature state that UI needs to render.

**Properties**:
- Immutable
- Single source of truth
- Persists across recompositions
- Emitted via `StateFlow`

**Example**:

```kotlin
// 1. Define sealed class for screen states
sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Success(
        val featuredPlaces: List<POI>,
        val nearbyPlaces: List<POI>,
        val popularToday: List<POI>
    ) : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
    object Empty : HomeScreenState()
}

// 2. Define UI state data class
data class HomeUiState(
    val screenState: HomeScreenState = HomeScreenState.Loading,
    val isRefreshing: Boolean = false,
    val selectedCategory: String? = null
)

// 3. ViewModel exposes state
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                screenState = HomeScreenState.Loading
            )
            try {
                val featured = getHomeFeaturedUseCase.execute()
                val nearby = getNearbyUseCase.execute()
                val popular = getPopularUseCase.execute()
                
                _uiState.value = _uiState.value.copy(
                    screenState = HomeScreenState.Success(
                        featuredPlaces = featured,
                        nearbyPlaces = nearby,
                        popularToday = popular
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    screenState = HomeScreenState.Error(e.message ?: "Unknown error")
                )
            }
        }
    }
}
```

### 2.2 Events (One-Time Actions)

**Definition**: User actions or one-time events that trigger state changes.

**Properties**:
- One-time delivery (not replayed to new subscribers)
- Examples: Navigation, Dialog dismissal, Success messages
- Emitted via `Channel` or `SharedFlow`

**Characteristics**:
- Not meant to be persisted
- Don't survive screen rotation
- Used for side effects (navigation, toasts, etc.)

**Example**:

```kotlin
sealed class HomeNavigationEvent {
    data class NavigateToPOIDetail(val poiId: String) : HomeNavigationEvent()
    object NavigateToDiscovery : HomeNavigationEvent()
    object NavigateToRecommendations : HomeNavigationEvent()
}

class HomeViewModel : ViewModel() {
    private val _navigationEvent = Channel<HomeNavigationEvent>()
    val navigationEvent: Flow<HomeNavigationEvent> = _navigationEvent.receiveAsFlow()
    
    fun onPOICardClicked(poiId: String) {
        viewModelScope.launch {
            _navigationEvent.send(HomeNavigationEvent.NavigateToPOIDetail(poiId))
        }
    }
}
```

### 2.3 Effects (Side Effects)

**Definition**: Non-UI side effects that don't affect state but need to be handled (analytics, logging, etc.).

**Properties**:
- Emitted via `SharedFlow`
- Replay policy controls behavior
- Examples: Analytics events, error tracking

**Example**:

```kotlin
sealed class HomeUiEffect {
    data class ShowToast(val message: String) : HomeUiEffect()
    data class LogAnalytics(val event: String, val params: Map<String, String>) : HomeUiEffect()
}

class HomeViewModel : ViewModel() {
    private val _effectFlow = MutableSharedFlow<HomeUiEffect>()
    val effectFlow: SharedFlow<HomeUiEffect> = _effectFlow.asSharedFlow()
    
    private fun emitEffect(effect: HomeUiEffect) {
        viewModelScope.launch {
            _effectFlow.emit(effect)
        }
    }
}
```

---

## 3. ViewModel Template

### 3.1 Standard ViewModel Structure

```kotlin
@HiltViewModel
class [Feature]ViewModel @Inject constructor(
    // Use cases
    private val useCase1: UseCase1,
    private val useCase2: UseCase2,
    // Services
    private val errorHandler: ErrorHandler,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    
    // ============ STATE ============
    private val _uiState = MutableStateFlow<[Feature]UiState>(
        [Feature]UiState()
    )
    val uiState: StateFlow<[Feature]UiState> = _uiState.asStateFlow()
    
    // ============ EVENTS ============
    private val _navigationEvent = Channel<[Feature]NavigationEvent>()
    val navigationEvent: Flow<[Feature]NavigationEvent> = _navigationEvent.receiveAsFlow()
    
    // ============ EFFECTS ============
    private val _effectFlow = MutableSharedFlow<[Feature]UiEffect>()
    val effectFlow: SharedFlow<[Feature]UiEffect> = _effectFlow.asSharedFlow()
    
    // ============ INITIALIZATION ============
    init {
        loadInitialData()
    }
    
    // ============ PUBLIC METHODS (User Actions) ============
    fun onRefresh() {
        loadData()
    }
    
    fun onItemClicked(itemId: String) {
        viewModelScope.launch {
            _navigationEvent.send(
                [Feature]NavigationEvent.NavigateToDetail(itemId)
            )
        }
    }
    
    // ============ PRIVATE METHODS ============
    private fun loadInitialData() {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val data = useCase1.execute()
                
                _uiState.update { it.copy(isLoading = false, data = data) }
                emitEffect([Feature]UiEffect.ShowToast("Data loaded successfully"))
                
            } catch (e: NetworkException) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = errorHandler.mapException(e)
                )}
            }
        }
    }
    
    private fun emitEffect(effect: [Feature]UiEffect) {
        viewModelScope.launch {
            _effectFlow.emit(effect)
        }
    }
}
```

---

## 4. UI State Patterns

### 4.1 Standard UI State Data Class

```kotlin
data class DiscoveryUiState(
    // Screen state
    val screenState: ScreenState = ScreenState.Loading,
    
    // Data
    val places: List<POI> = emptyList(),
    
    // UI state
    val isRefreshing: Boolean = false,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.POPULAR,
    
    // Error handling
    val error: String? = null,
    
    // Loading states for specific operations
    val isLoadingMore: Boolean = false
)
```

### 4.2 Sealed Class for Screen States

All screens should use this standard pattern:

```kotlin
sealed class ScreenState {
    object Loading : ScreenState()
    data class Success<T>(val data: T) : ScreenState()
    data class Error(val exception: Exception) : ScreenState()
    object Empty : ScreenState()
}

// Usage in UI state
data class RouteUiState(
    val screenState: ScreenState = ScreenState.Loading,
    val isRefreshing: Boolean = false
)
```

### 4.3 State Update Patterns

**Use `update {}` lambda for atomic updates**:

```kotlin
// ✅ GOOD - Atomic update
_uiState.update { currentState ->
    currentState.copy(
        isLoading = false,
        data = newData,
        error = null
    )
}

// ❌ AVOID - Non-atomic update
_uiState.value = _uiState.value.copy(isLoading = false)
_uiState.value = _uiState.value.copy(data = newData)
_uiState.value = _uiState.value.copy(error = null)
```

---

## 5. Error State Management

### 5.1 Standard Error Types

```kotlin
sealed class UiError {
    object NetworkError : UiError() {
        val message: String = "Network error. Please check your connection"
    }
    
    object ServerError : UiError() {
        val message: String = "Server error. Please try later"
    }
    
    object TimeoutError : UiError() {
        val message: String = "Request timeout. Please try again"
    }
    
    data class ValidationError(val message: String) : UiError()
    
    data class UnknownError(val message: String) : UiError()
}
```

### 5.2 Exception Mapping

```kotlin
class ErrorHandler @Inject constructor() {
    fun mapException(exception: Exception): String = when (exception) {
        is IOException -> "Network error. Please check your connection"
        is SocketTimeoutException -> "Request timeout. Please try again"
        is ServerException -> "Server error. Please try later"
        is ValidationException -> exception.message ?: "Validation error"
        else -> "Unknown error occurred. Please try again"
    }
}
```

### 5.3 Error Display in UI

```kotlin
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (uiState.screenState) {
        is ScreenState.Error -> {
            ErrorDialog(
                message = (uiState.screenState as ScreenState.Error)
                    .exception.message ?: "Unknown error",
                onRetry = { viewModel.onRefresh() },
                onDismiss = { viewModel.onErrorDismissed() }
            )
        }
        // ... other states
    }
}
```

---

## 6. Offline & Caching Strategy

### 6.1 Offline Detection

```kotlin
class ConnectivityManager @Inject constructor(
    private val context: Context
) {
    fun isOnline(): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )
        return capabilities != null && (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        )
    }
}
```

### 6.2 Caching with Offline Support

```kotlin
class POIRepository @Inject constructor(
    private val poiApi: POIApi,
    private val poiDao: POIDao,
    private val connectivityManager: ConnectivityManager
) : IPOIRepository {
    
    override suspend fun searchPOIs(query: String): List<POI> {
        return if (connectivityManager.isOnline()) {
            try {
                val remoteData = poiApi.searchPOIs(query)
                // Cache the data
                remoteData.forEach { poiDao.insertPOI(it.toEntity()) }
                remoteData
            } catch (e: Exception) {
                // Fallback to cache on network failure
                poiDao.searchPOIs(query).map { it.toDomain() }
            }
        } else {
            // Use cached data when offline
            poiDao.searchPOIs(query).map { it.toDomain() }
        }
    }
}
```

### 6.3 Offline Mode Display

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    connectivityManager: ConnectivityManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline = connectivityManager.isOnline()
    
    if (!isOnline) {
        OfflineBanner()
    }
    
    when (uiState.screenState) {
        is ScreenState.Success -> {
            // Show cached data with offline indicator
            if (!isOnline) {
                Text(text = "Çevrimdışı Mod - Önbelleğe alınmış verileri görüntülüyorsunuz")
            }
            // Render content
        }
    }
}
```

---

## 7. Event Handling Patterns

### 7.1 Navigation Event Handling

```kotlin
@Composable
fun DiscoveryScreen(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is DiscoveryNavigationEvent.NavigateToPOIDetail -> {
                    navController.navigate(
                        "poi/${event.poiId}"
                    )
                }
                DiscoveryNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
}
```

### 7.2 Effect Handling

```kotlin
@Composable
fun FeedbackScreen(
    viewModel: FeedbackViewModel = hiltViewModel(),
    showToast: (String) -> Unit = {}
) {
    LaunchedEffect(viewModel.effectFlow) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is FeedbackUiEffect.ShowSuccess -> {
                    showToast(effect.message)
                }
                is FeedbackUiEffect.LogEvent -> {
                    // Log to analytics
                    analyticsService.logEvent(effect.event)
                }
            }
        }
    }
}
```

---

## 8. State Restoration & Configuration Changes

### 8.1 ViewModel Lifecycle Awareness

```kotlin
class RouteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routeUseCase: GetRouteUseCase
) : ViewModel() {
    
    // State survives configuration changes
    private val _uiState = MutableStateFlow<RouteUiState>(
        savedStateHandle.get<RouteUiState>("route_state")
            ?: RouteUiState()
    )
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()
    
    // Save state for restoration
    private fun saveState() {
        savedStateHandle["route_state"] = _uiState.value
    }
    
    override fun onCleared() {
        saveState()
        super.onCleared()
    }
}
```

### 8.2 State Persistence with Room

```kotlin
// For critical user data that should persist across app sessions
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val userId: String,
    val interests: List<String>,
    val budget: String,
    val transportation: String,
    val crowdTolerance: String
)

// ViewModel loads from Room on initialization
init {
    viewModelScope.launch {
        val preferences = getUserPreferencesUseCase.execute()
        _uiState.value = _uiState.value.copy(
            preferences = preferences
        )
    }
}
```

---

## 9. Performance Best Practices

### 9.1 Avoid Unnecessary Recompositions

```kotlin
// ✅ GOOD - State is properly memoized
@Composable
fun RouteCard(route: Route) {
    // Recomposes only when route changes
    val routeName = remember(route) { route.name.uppercase() }
    Text(text = routeName)
}

// ❌ AVOID - Recomposes on every parent recomposition
@Composable
fun RouteCard(route: Route) {
    val routeName = route.name.uppercase()  // Recomputed every recomposition
    Text(text = routeName)
}
```

### 9.2 Lazy State Collection

```kotlin
// ✅ GOOD - Collect state lazily with specific value
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Only recompose when screenState changes
    when (val state = uiState.screenState) {
        is ScreenState.Success -> { /* Render */ }
        // ...
    }
}
```

### 9.3 Debounce Search Queries

```kotlin
class DiscoveryViewModel : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                searchPOIUseCase.execute(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
```

---

## 10. Testing State Management

### 10.1 ViewModel Unit Tests

```kotlin
@RunWith(AndroidTestRunner::class)
class HomeViewModelTest {
    
    private val getHomeFeaturedUseCase = mockk<GetHomeFeaturedUseCase>()
    private val viewModel = HomeViewModel(getHomeFeaturedUseCase)
    
    @Test
    fun `loadData - success - updates state`() = runTest {
        // Arrange
        val mockPlaces = listOf(mockPOI())
        coEvery { getHomeFeaturedUseCase.execute() } returns mockPlaces
        
        // Act
        viewModel.loadData()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertIs<ScreenState.Success>(uiState.screenState)
        assertEquals(mockPlaces, (uiState.screenState as ScreenState.Success).data)
    }
    
    @Test
    fun `loadData - error - updates error state`() = runTest {
        // Arrange
        val exception = IOException("Network error")
        coEvery { getHomeFeaturedUseCase.execute() } throws exception
        
        // Act
        viewModel.loadData()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertIs<ScreenState.Error>(uiState.screenState)
    }
}
```

### 10.2 State Collection in Tests

```kotlin
@Test
fun `navigationEvent - emits correct event`() = runTest {
    val events = mutableListOf<NavigationEvent>()
    
    backgroundScope.launch {
        viewModel.navigationEvent.collect { events.add(it) }
    }
    
    viewModel.onPOIClicked("poi-123")
    
    assertEquals(1, events.size)
    assertIs<NavigationEvent.NavigateToPOIDetail>(events[0])
    assertEquals("poi-123", (events[0] as NavigationEvent.NavigateToPOIDetail).poiId)
}
```

---

## 11. Common Pitfalls & Solutions

### Pitfall 1: Mutating State Directly

```kotlin
// ❌ WRONG - Mutates state in place
_uiState.value.places.add(newPlace)

// ✅ CORRECT - Creates new list
_uiState.update { currentState ->
    currentState.copy(
        places = currentState.places + newPlace
    )
}
```

### Pitfall 2: Not Handling Coroutine Cancellation

```kotlin
// ❌ WRONG - Doesn't cancel coroutines on clear
override fun onCleared() {
    super.onCleared()
}

// ✅ CORRECT - ViewModelScope cancels automatically
// No need to explicitly cancel, viewModelScope handles it
```

### Pitfall 3: Using MutableState for Complex State

```kotlin
// ❌ WRONG - Mutable state not captured properly
var isLoading: Boolean = false

// ✅ CORRECT - Use StateFlow for reactive state
private val _isLoading = MutableStateFlow(false)
val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
```

### Pitfall 4: Collecting State Without Lifecycle Awareness

```kotlin
// ❌ WRONG - Leaks memory, collects even when paused
viewModel.uiState.collect { state ->
    updateUI(state)
}

// ✅ CORRECT - Lifecycle-aware collection
LaunchedEffect(viewModel.uiState) {
    viewModel.uiState.collect { state ->
        updateUI(state)
    }
}
```

---

## 12. Migration Guide from Old Pattern

If transitioning from a different state management pattern:

### Before: LiveData + ViewModel

```kotlin
class OldViewModel : ViewModel() {
    private val _data = MutableLiveData<List<POI>>()
    val data: LiveData<List<POI>> = _data
    
    fun loadData() {
        viewModelScope.launch {
            _data.value = repository.getPOIs()
        }
    }
}
```

### After: StateFlow + UDF

```kotlin
@HiltViewModel
class NewViewModel @Inject constructor(
    private val repository: POIRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = ScreenState.Loading
                val data = repository.getPOIs()
                _uiState.value = ScreenState.Success(data)
            } catch (e: Exception) {
                _uiState.value = ScreenState.Error(e)
            }
        }
    }
}
```

---

## 13. Debugging State Issues

### 13.1 State Flow Logging

```kotlin
class DebugStateFlow<T>(
    private val wrapped: StateFlow<T>,
    private val tag: String
) : StateFlow<T> by wrapped {
    
    override suspend fun collect(collector: FlowCollector<T>) {
        wrapped.collect { value ->
            Log.d(tag, "State: $value")
            collector.emit(value)
        }
    }
}
```

### 13.2 ViewModel State Inspector

```kotlin
// Add to ViewModel for debugging
fun printState() {
    Log.d("ViewModel", "Current UI State: ${_uiState.value}")
}

// In Compose for quick debugging
if (BuildConfig.DEBUG) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Debug State: ${uiState.value}")
}
```

---

## 14. Checklist for New Screens

- [ ] Define sealed class for ScreenState
- [ ] Define data class for UiState
- [ ] Create ViewModel with @HiltViewModel
- [ ] Expose uiState as StateFlow
- [ ] Expose navigationEvent as Flow<NavigationEvent>
- [ ] Implement error handling
- [ ] Implement loading states
- [ ] Implement empty states
- [ ] Implement offline handling
- [ ] Add unit tests
- [ ] Add UI tests
- [ ] Document navigation flows

---

## 15. References & Resources

- [Android Architecture Guide](https://developer.android.com/jetpack/guide)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Flow Documentation](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Jetpack Compose State Management](https://developer.android.com/jetpack/compose/state-management)

---

**Status**: Complete ✅  
**Last Updated**: May 2026  
**Phase**: 12 (State Management Guidelines)  

