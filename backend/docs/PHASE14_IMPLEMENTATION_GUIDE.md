# Phase 14 - Implementation Guide

**Status**: Starting Implementation  
**Priority**: Critical  
**Timeline**: 6-8 weeks

---

## 1. Quick Start Checklist

### Immediate Actions (This Week)
- [ ] Verify backend compilation success
- [ ] Generate 100 POI seed dataset
- [ ] Test API endpoints with sample data
- [ ] Set up test infrastructure
- [ ] Create unit test skeleton

### This Sprint (Week 1-2)
- [ ] Implement unit tests (backend 50+, mobile 20+)
- [ ] Implement integration tests
- [ ] Establish performance baseline
- [ ] Begin user study recruitment

---

## 2. Phase 13 Completion: Seed Data Generation

### 2.1 Generate 100 POIs

**Option A: Using Kotlin CLI**
```bash
# From mobile app directory
cd mobile
./gradlew generateSeedData

# Expected output:
# ✅ Generated 100 POIs
# ✅ Saved to data/pois-seed.json
# ✅ Distribution: 10-15 per district
# ✅ All categories represented
```

**Option B: Using Java CLI**
```bash
# From backend directory
cd backend
mvn clean package -DskipTests
java -cp target/classes:/path/to/deps com.eskisehir.eventapi.generator.POISeedDataGeneratorCLI \
  --output data/pois-seed.json \
  --count 100 \
  --city Eskişehir
```

**Option C: Using Spring Boot Application Runner**
```bash
# Via application startup property
export GENERATE_POI_SEED_DATA_ON_STARTUP=true
export POI_SEED_OUTPUT_PATH=data/pois-seed.json
mvn spring-boot:run
```

### 2.2 Verify Generated Data

```bash
# Check file exists and has valid JSON
cat data/pois-seed.json | jq '.' | head -20

# Validate structure
jq '[.[] | keys] | unique' data/pois-seed.json

# Count total POIs
jq 'length' data/pois-seed.json
# Expected: 100

# Distribution by district
jq 'group_by(.district) | map({district: .[0].district, count: length})' data/pois-seed.json

# Distribution by category
jq 'group_by(.category) | map({category: .[0].category, count: length})' data/pois-seed.json
```

### 2.3 Load into Database

```bash
# Start PostgreSQL (if using Docker)
docker run -d \
  --name postgres-eskisehir \
  -e POSTGRES_DB=eskisehir_events \
  -e POSTGRES_USER=dev \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15

# Wait for startup
sleep 5

# Run Flyway migrations
mvn clean flyway:migrate -Dflyway.user=dev -Dflyway.password=password

# Load seed data via REST API
curl -X POST http://localhost:8080/api/admin/pois/load-seed-data \
  -H "Content-Type: application/json" \
  -d @data/pois-seed.json

# Or via database loader service
java -cp target/classes:/path/to/deps \
  com.eskisehir.eventapi.service.POISeedDataLoaderService \
  --file data/pois-seed.json --database postgresql://localhost:5432/eskisehir_events
```

### 2.4 Validate Loaded Data

```bash
# Query all POIs
curl http://localhost:8080/api/v1/pois | jq 'length'
# Expected: 100

# Check statistics
curl http://localhost:8080/api/v1/pois/stats | jq '.'
# Expected output:
{
  "totalPOIs": 100,
  "totalCategories": 25,
  "totalDistricts": 10,
  "categoryDistribution": {...},
  "districtDistribution": {...},
  "averagePopularityScore": 55.2,
  ...
}

# Test geographic query
curl "http://localhost:8080/api/v1/pois/location/bounds?minLat=39.7&maxLat=39.8&minLon=30.5&maxLon=30.6" | jq 'length'
```

---

## 3. Phase 14 Test Implementation

### 3.1 Set Up Test Infrastructure

**Dependency Setup** (pom.xml)
```xml
<!-- Testing dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>3.0.6</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.17.6</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.17.6</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

**Create Test Configuration Class**
```java
// Location: src/test/java/com/eskisehir/eventapi/config/TestConfig.java

@Configuration
@TestProfile
public class TestConfig {
    
    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(5432);
    }
}
```

**Create Test Base Class**
```java
// Location: src/test/java/com/eskisehir/eventapi/BaseTest.java

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {
    
    @Autowired
    protected WebApplicationContext context;
    
    @Autowired
    protected MockMvc mockMvc;
    
    protected ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
```

### 3.2 Create Unit Test Skeleton

**Backend Unit Tests**
```bash
# Create test directory structure
mkdir -p src/test/java/com/eskisehir/eventapi/{
  service,
  repository,
  controller,
  dto,
  algorithm,
  validator
}

# Create test files
touch src/test/java/com/eskisehir/eventapi/service/POISeedDataServiceTest.java
touch src/test/java/com/eskisehir/eventapi/repository/POIPhase13RepositoryTest.java
touch src/test/java/com/eskisehir/eventapi/controller/PoiControllerTest.java
touch src/test/java/com/eskisehir/eventapi/dto/PoiResponseTest.java
touch src/test/java/com/eskisehir/eventapi/algorithm/POIScoreCalculatorTest.java
```

**Mobile Unit Tests** (Kotlin)
```bash
# Create test directory
mkdir -p mobile/app/src/test/java/com/eskisehir/events/ui/viewmodel
mkdir -p mobile/app/src/test/java/com/eskisehir/events/domain/usecase
mkdir -p mobile/app/src/androidTest/java/com/eskisehir/events/ui/screen

# Create test files
touch mobile/app/src/test/java/com/eskisehir/events/ui/viewmodel/PoiListViewModelTest.kt
touch mobile/app/src/test/java/com/eskisehir/events/ui/viewmodel/RecommendationViewModelTest.kt
touch mobile/app/src/test/java/com/eskisehir/events/domain/usecase/GenerateRecommendationsUseCaseTest.kt
touch mobile/app/src/androidTest/java/com/eskisehir/events/ui/screen/PoiListScreenTest.kt
```

### 3.3 Implement Critical Unit Tests

**POISeedDataService Tests**
```java
// src/test/java/com/eskisehir/eventapi/service/POISeedDataServiceTest.java

@SpringBootTest
@ActiveProfiles("test")
public class POISeedDataServiceTest extends BaseTest {

    @Autowired
    private POISeedDataService poiService;

    @Autowired
    private POIPhase13Repository poiRepository;

    @BeforeEach
    void setUp() {
        // Create test POIs
        createTestPOIs();
    }

    @Test
    void testFindByGeographicBounds_WithinBounds_ReturnsPOIs() {
        // Given: POIs within geographic bounds
        Double minLat = 39.7, maxLat = 39.8, minLon = 30.5, maxLon = 30.6;
        
        // When
        List<POI> result = poiService.findByGeographicBounds(minLat, maxLat, minLon, maxLon);
        
        // Then
        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.forEach(poi -> {
            assertTrue(poi.getLatitude() >= minLat && poi.getLatitude() <= maxLat);
            assertTrue(poi.getLongitude() >= minLon && poi.getLongitude() <= maxLon);
        });
    }

    @Test
    void testFindMostPopularPOIs_WithLimit_ReturnsTopK() {
        // Given: request for top 5 POIs
        Integer limit = 5;
        
        // When
        List<POI> result = poiService.findMostPopularPOIs(limit);
        
        // Then
        assertEquals(limit, result.size());
        // Verify sorted by popularity (descending)
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getPopularityScore() >= result.get(i + 1).getPopularityScore());
        }
    }

    @Test
    void testGetStatistics_CalculatesDistribution() {
        // When
        POIStatisticsDto stats = poiService.getStatistics();
        
        // Then
        assertNotNull(stats);
        assertEquals(3, stats.getTotalDistricts()); // 3 test districts
        assertTrue(stats.getCategoryDistribution().size() > 0);
        assertTrue(stats.getDistrictDistribution().size() > 0);
        assertTrue(stats.getAveragePopularityScore() >= 0);
    }

    private void createTestPOIs() {
        List<POI> testPOIs = new ArrayList<>();
        
        // Create POI #1: Popular museum
        POI poi1 = POI.builder()
            .id("poi-001")
            .name("Eskişehir Arkeoloji Müzesi")
            .category(POI.POICategory.MUSEUM)
            .district(POI.District.ODUNPAZARI)
            .latitude(39.75)
            .longitude(30.55)
            .popularityScore(85f)
            .build();
        testPOIs.add(poi1);
        
        // Create POI #2: Local cafe
        POI poi2 = POI.builder()
            .id("poi-002")
            .name("Kahveci Kardeşler")
            .category(POI.POICategory.CAFE)
            .district(POI.District.SAZOVA)
            .latitude(39.76)
            .longitude(30.56)
            .popularityScore(72f)
            .build();
        testPOIs.add(poi2);
        
        // Create POI #3: Park
        POI poi3 = POI.builder()
            .id("poi-003")
            .name("Sazova Parkı")
            .category(POI.POICategory.PARK)
            .district(POI.District.SAZOVA)
            .latitude(39.74)
            .longitude(30.54)
            .popularityScore(65f)
            .build();
        testPOIs.add(poi3);
        
        poiRepository.saveAll(testPOIs);
    }
}
```

**PoiController Integration Tests**
```java
// src/test/java/com/eskisehir/eventapi/controller/PoiControllerTest.java

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PoiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllPois_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/pois"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void testGetPoiById_WithValidId_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/pois/poi-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    void testGetStatistics_ReturnsDistribution() throws Exception {
        mockMvc.perform(get("/api/v1/pois/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPOIs", greaterThan(0)))
            .andExpect(jsonPath("$.categoryDistribution", notNullValue()));
    }
}
```

### 3.4 Mobile Unit Tests (Kotlin)

**ViewModel Tests**
```kotlin
// mobile/app/src/test/java/com/eskisehir/events/ui/viewmodel/PoiListViewModelTest.kt

@HiltAndroidTest
class PoiListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PoiListViewModel
    private val mockPoiRepository = mockk<PoiRepository>()

    @Before
    fun setup() {
        viewModel = PoiListViewModel(mockPoiRepository)
    }

    @Test
    fun testLoadPois_EmitsLoadingThenSuccess() = runTest {
        // Given
        val testPois = listOf(
            POIEntity(id = "1", name = "Museum", category = "MUSEUM"),
            POIEntity(id = "2", name = "Cafe", category = "CAFE")
        )
        coEvery { mockPoiRepository.getAllPois() } returns Result.success(testPois)

        // When
        viewModel.loadPois()

        // Then
        val state = viewModel.uiState.value
        assertEquals(ScreenState.Success, state::class.simpleName)
        assertEquals(2, (state as ScreenState.Success).data?.size)
    }

    @Test
    fun testLoadPois_WithError_EmitsErrorState() = runTest {
        // Given
        val error = Exception("Network error")
        coEvery { mockPoiRepository.getAllPois() } returns Result.failure(error)

        // When
        viewModel.loadPois()

        // Then
        val state = viewModel.uiState.value
        assertEquals(ScreenState.Error, state::class.simpleName)
    }
}
```

### 3.5 API Performance Test

```java
// src/test/java/com/eskisehir/eventapi/performance/ApiPerformanceTest.java

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Timeout(1000) // milliseconds - must complete within 1 second
    void testGetAllPois_PerformsWithin1Second() throws Exception {
        mockMvc.perform(get("/api/v1/pois"))
            .andExpect(status().isOk());
    }

    @Test
    @Timeout(500) // milliseconds
    void testGetPoiById_PerformsWithin500Ms() throws Exception {
        mockMvc.perform(get("/api/v1/pois/poi-001"))
            .andExpect(status().isOk());
    }

    @Test
    @Timeout(2000)
    void testGetStatistics_PerformsWithin2Seconds() throws Exception {
        mockMvc.perform(get("/api/v1/pois/stats"))
            .andExpect(status().isOk());
    }
}
```

---

## 4. Running Tests

### Maven Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=POISeedDataServiceTest

# Run with coverage
mvn clean test jacoco:report
# Report at: target/site/jacoco/index.html

# Run performance tests only
mvn test -Dgroups=performance
```

### Gradle Commands (Mobile)
```bash
# Run all unit tests
./gradlew test

# Run instrumented tests (Android device required)
./gradlew connectedAndroidTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

---

## 5. Test Coverage Goals

**Phase 14 Week 1-2 Targets**:
- Backend unit tests: 50+ tests (80% coverage)
- Backend integration tests: 20+ tests (70% coverage)
- Mobile unit tests: 20+ tests (70% coverage)
- API tests: 15+ endpoints (100% coverage)

**Coverage Report Locations**:
- Backend: `backend/target/site/jacoco/index.html`
- Mobile: `mobile/app/build/reports/coverage/index.html`

---

## 6. CI/CD Integration

### GitHub Actions Workflow
```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: cd backend && mvn clean test

  mobile-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: cd mobile && ./gradlew test
```

---

## 7. Next Steps After Tests

1. **Week 3**: Deploy to staging environment
2. **Week 4**: Begin user study recruitment
3. **Week 5-6**: Run 4-week user study
4. **Week 7-8**: Analyze results and document

---

**Status**: Ready for implementation  
**Owner**: QA Lead + Development Team  
**Target Completion**: August 15, 2026
