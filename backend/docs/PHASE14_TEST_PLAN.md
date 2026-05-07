# Phase 14 - Testing, Evaluation and Thesis Experiment Design

**Status**: ACTIVE  
**Priority**: Critical  
**Goal**: Sistemi yazılım, AI ve kullanıcı memnuniyeti açısından ölçülebilir hale getirmek  
**Timeline**: 3-4 weeks

---

## 1. Technical Testing Strategy

### 1.1 Unit Testing

#### Backend Unit Tests

**PoiService Tests**
```java
// Location: src/test/java/com/eskisehir/eventapi/service/POISeedDataServiceTest.java
- testFindByGeographicBounds_WithinBounds_ReturnsPOIs()
- testFindByGeographicBounds_OutOfBounds_ReturnsEmpty()
- testFindMostPopularPOIs_WithLimit_ReturnsTopK()
- testFindMostPopularPOIs_NoLimit_ReturnsDefault10()
- testFindByCategory_WithValidCategory_ReturnsPOIs()
- testFindByCategory_WithInvalidCategory_ReturnsEmpty()
- testFindByDistrict_WithValidDistrict_ReturnsPOIs()
- testFindAccessiblePOIs_FilteredCorrectly()
- testFindFamilyFriendlyPOIs_FilteredCorrectly()
- testFindFreePOIs_FilteredByPriceLevel()
- testGetStatistics_CalculatesDistribution()
- testGetAvailableDistricts_UniqueValues()
- testGetAvailableCategories_UniqueValues()
```

**Score Calculator Tests**
```java
// Location: src/test/java/com/eskisehir/eventapi/algorithm/POIScoreCalculatorTest.java
- testCalculatePopularityScore_WithValidInputs()
- testCalculatePopularityScore_Normalize0To100()
- testCalculateCrowdProxyScore_TimeBasedFactors()
- testCalculateSustainabilityScore_EnvironmentalFactors()
- testCalculateLocalBusinessScore_TagBased()
- testAverageScoreCalculation()
- testScoreEdgeCases_NullValues()
- testScoreEdgeCases_ExtremeValues()
```

**DTO Validation Tests**
```java
// Location: src/test/java/com/eskisehir/eventapi/dto/POIDtoTest.java
- testPoiResponseConversion_FromLegacyPoi()
- testPoiResponseConversion_FromPhase13POI()
- testPoiStatisticsDtoConstruction()
- testGeographicBoundsDtoValidation()
```

#### Mobile Unit Tests (Kotlin)

**ViewModel Tests**
```kotlin
// Location: mobile/app/src/test/java/com/eskisehir/events/ui/viewmodel/
- PoiListViewModelTest
- RecommendationViewModelTest
- RouteDetailsViewModelTest
- UserPreferenceViewModelTest
```

### 1.2 Integration Testing

#### Database Integration Tests

**POI Repository Tests**
```java
// Location: src/test/java/com/eskisehir/eventapi/repository/POIPhase13RepositoryTest.java

@SpringBootTest
@TestcontainersTest  // PostgreSQL test container
public class POIPhase13RepositoryTest {
    
    - testSaveAndRetrievePOI()
    - testFindByCategory_ReturnsCategoryPOIs()
    - testFindByDistrict_ReturnsDistrictPOIs()
    - testGeographicBoundsQuery_PostGISAccuracy()
    - testIndexPerformance_LargeDataset()
    - testDatabaseConstraints_ValidatesEnums()
    - testMigrationFromH2ToPostgreSQL()
}
```

#### API Integration Tests

**End-to-End Endpoint Tests**
```java
// Location: src/test/java/com/eskisehir/eventapi/integration/
- PoiControllerIntegrationTest
  - testGetAllPoisEndpoint()
  - testSearchPoisEndpoint()
  - testGeographicBoundsEndpoint()
  - testStatisticsEndpoint()
  - testFiltersEndpoint()
  
- RecommendationControllerIntegrationTest
  - testGenerateRecommendations()
  - testApplyUserPreferences()
  - testScoreConsistency()

- RouteControllerIntegrationTest
  - testGenerateRoute()
  - testMultiObjectiveOptimization()
  - testRouteValidation()
```

### 1.3 API Testing

**Test Coverage**

| Endpoint | Method | Test Cases | Pass Rate |
|----------|--------|-----------|-----------|
| `/api/v1/pois` | GET | 5 | - |
| `/api/v1/pois/{id}` | GET | 4 | - |
| `/api/v1/pois/search` | GET | 6 | - |
| `/api/v1/pois/location/bounds` | GET | 5 | - |
| `/api/v1/pois/popular` | GET | 4 | - |
| `/api/v1/pois/stats` | GET | 3 | - |
| `/api/v1/recommendations` | POST | 8 | - |
| `/api/v1/routes/generate` | POST | 7 | - |
| `/api/v1/routes/{id}` | GET | 4 | - |

**API Performance Benchmarks**

```
Target Response Times (p95):
- Single POI fetch: <50ms
- POI list (100 items): <200ms
- Geographic query: <300ms
- Recommendation generation: <2000ms
- Route optimization: <5000ms
- Statistics calculation: <500ms

Target Throughput:
- 100 concurrent users
- 1000 requests/minute
- 99.9% success rate
```

### 1.4 Route Generation Tests

**Optimization Quality Tests**
```java
- testRouteGenerationFeasibility()
  - All POIs within bounds: ✓
  - All constraints satisfied: ✓
  - Optimal or near-optimal distance: ✓
  
- testBudgetConstraintEnforcement()
  - Total cost ≤ user budget: ✓
  - Price levels honored: ✓
  
- testTimeConstraintEnforcement()
  - Total duration ≤ time limit: ✓
  - Visit durations accurate: ✓
  
- testCrowdProxyValidation()
  - Crowd-avoidant route selected: ✓
  - Accessibility preserved: ✓
```

### 1.5 Mobile UI Tests

**Jetpack Compose Tests**
```kotlin
// Location: mobile/app/src/androidTest/java/

- PoiListScreenTest
  - testDisplaysPOIList()
  - testFilterFunctionality()
  - testSearchFunctionality()
  - testLoadingStateHandling()
  - testErrorStateHandling()
  - testOfflineFallback()

- RouteMapScreenTest
  - testMapInitialization()
  - testPoiMarkersDisplay()
  - testRouteLineDrawing()
  - testUserInteraction()
  - testZoomAndPan()

- FeedbackScreenTest
  - testFeedbackFormUI()
  - testTextInputValidation()
  - testRatingInput()
  - testSubmitFunctionality()
```

---

## 2. AI Evaluation Metrics

### 2.1 Recommendation Quality Metrics

#### Precision@K
```
Definition: % of top-K recommendations that user interacted with

Formula: Precision@K = (# relevant items in top-K) / K

Targets:
- Precision@5: ≥ 0.60 (60%)
- Precision@10: ≥ 0.50 (50%)

Calculation Method:
1. Generate recommendations for 100 test users
2. Track which recommendations user clicked/saved/visited
3. Calculate precision for K=5,10,15
4. Report mean ± std dev
```

#### Recall@K
```
Definition: % of all relevant items that appear in top-K

Formula: Recall@K = (# relevant items in top-K) / (total # relevant items)

Targets:
- Recall@5: ≥ 0.40 (40%)
- Recall@10: ≥ 0.60 (60%)

Calculation Method:
1. For each user, identify all POIs they would like (from feedback)
2. Check how many appear in top-K recommendations
3. Calculate recall for K=5,10,15
```

#### NDCG@K (Normalized Discounted Cumulative Gain)
```
Definition: Quality of ranking considering position importance

Formula: NDCG@K = DCG@K / IDCG@K

Where:
- DCG@K = Σ(relevance_i / log2(i+1)) for i=1 to K
- IDCG@K = max possible DCG@K

Targets:
- NDCG@5: ≥ 0.70
- NDCG@10: ≥ 0.75

Calculation Method:
1. Generate recommendations ranked by score
2. Assign relevance: 1=clicked, 0.5=viewed, 0.2=no interaction
3. Calculate DCG for actual ranking
4. Calculate ideal DCG (sorted by relevance)
5. Compute NDCG = DCG/IDCG
```

#### Click-Through Rate (CTR)
```
Definition: % of displayed recommendations that user clicks

Formula: CTR = (# clicks) / (# impressions)

Targets:
- Minimum CTR: ≥ 0.15 (15%)
- Average CTR: 0.20-0.30 (20-30%)

Tracking:
- Impression: POI shown to user
- Click: User clicked POI details
- View: User spent >3 seconds viewing
- Interaction: Click or view
```

#### Route Acceptance Rate
```
Definition: % of generated routes user actually uses

Formula: Acceptance = (# accepted routes) / (# generated routes)

Targets:
- Minimum: ≥ 0.70 (70%)
- Target: ≥ 0.85 (85%)

Sub-metrics:
- Acceptance by route complexity (1-3 POIs vs 10+)
- Acceptance by constraints (flexible vs strict)
- Acceptance by optimization factors (eco vs crowd vs popular)
```

### 2.2 AI Model Metrics

#### Thompson Sampling Performance
```
Metric: Regret Minimization Over Time

Track:
- Cumulative regret (difference from optimal choices)
- Exploration vs exploitation ratio
- Convergence speed

Target:
- Regret grows sub-linearly (O(log T) rate)
- Exploration rate drops to <5% by week 2
```

#### Bandit Context Impact
```
Metric: Context Vector Effectiveness

Track for each context dimension:
- Weather impact on recommendation quality
- Time of day impact
- User mood/budget shift impact

Target:
- ≥70% variance explained by context
- Each context feature contributes >5% to recommendation quality
```

---

## 3. Route Quality Metrics

### 3.1 Distance and Duration

**Total Route Distance**
```
Targets:
- Average route distance: 15-25 km (Eskişehir city center)
- Median route distance: 18 km
- 95th percentile: ≤40 km

Calculation:
1. Generate 100 random routes with 5-10 POIs each
2. Calculate total distance using haversine formula
3. Report mean, median, std dev, percentiles

Validation:
- Compare against Google Maps routing (±5% tolerance)
```

**Total Route Duration**
```
Targets:
- Average route duration: 3-5 hours
- Includes travel + visit time
- Buffer for unforeseen delays: 10-15%

Calculation:
- Travel time from Google Maps API
- Visit time from POI attributes
- Total = Σ(travel + visit)
```

### 3.2 Budget Compliance

**Budget Adherence**
```
Targets:
- 100% of routes respect budget constraint
- Average spending: 85-95% of budget
- No routes exceed budget

Calculation:
1. Generate 100 routes with varying budgets (50₺-500₺)
2. Check if Σ(POI costs) ≤ user budget
3. Report % compliance and avg budget utilization

Edge Cases:
- Free POIs only (budget unused)
- Luxury POIs (tight constraint)
```

### 3.3 Crowd Exposure

**Crowd Proxy Quality**
```
Targets:
- Crowd-avoidant routes: avg crowd score 25-35/100
- Crowd-tolerant routes: avg crowd score 60-75/100
- Correlation with user preference: ≥0.80

Calculation:
1. Generate routes with different crowd preferences
2. Calculate average crowd_proxy_score
3. Compare with user preference weight
4. Measure correlation
```

### 3.4 Sustainability Impact

**Carbon Impact Estimation**
```
Targets:
- Eco-optimized routes: 20-30% lower carbon than greedy
- Sustainable POI ratio: ≥60% of selected POIs
- Green transport adoption: ≥70%

Calculation:
1. Estimate CO2 per km (based on transport mode)
2. Sum for entire route
3. Compare eco-optimized vs non-optimized
4. Report reduction percentage

Formula:
- Car: 0.21 kg CO2/km
- Transit: 0.05 kg CO2/km
- Walk/Bike: 0 kg CO2/km
```

---

## 4. User Satisfaction and Thesis Evaluation

### 4.1 User Satisfaction Survey Design

**Survey Structure** (Turkish, ~10-15 minutes)

#### Part 1: System Usability (SUS - System Usability Scale)
- 10 questions, 5-point Likert
- Targets: ≥70/100 SUS score

#### Part 2: Recommendation Quality
- 5 questions on relevance
- 3 questions on diversity
- 4 questions on explanation clarity
- 3 questions on serendipity
- Targets: ≥4.0/5.0 average

#### Part 3: Route Quality
- 4 questions on optimality
- 3 questions on practicality
- 3 questions on safety/accessibility
- 4 questions on cultural/sustainability value
- Targets: ≥4.0/5.0 average

#### Part 4: Overall Experience
- 1 NPS question (0-10)
- 1 willingness to recommend (5-point)
- 2 open-ended feedback
- Targets: NPS ≥20, Recommend ≥4.0

#### Part 5: Comparative Questions
- Against baseline system
- Against competitor apps
- Overall satisfaction ranking

### 4.2 Baseline System Comparison

**Baseline Definition**: Non-personalized popular POI list + greedy nearest-neighbor routing

**Comparison Metrics**

| Metric | Our System Target | Baseline Expected | Improvement |
|--------|------------------|-------------------|------------|
| Recommendation CTR | 0.20-0.30 | 0.08-0.10 | 2-3x |
| Route Acceptance | 0.85 | 0.65 | +20% |
| User Satisfaction | 4.2/5.0 | 3.2/5.0 | +1.0 |
| NPS | ≥20 | <5 | +15 |
| Task Completion Time | <3 min | >5 min | 40% faster |
| Precision@10 | 0.50+ | 0.25 | 2x better |

**User Study Protocol**

1. **Recruitment**: 50 university students + 20 locals (n=70)
2. **Groups**: 35 test group (our system) + 35 control (baseline)
3. **Session Duration**: 45 minutes per participant
4. **Tasks**:
   - Generate 3 recommendations
   - Plan 2 routes
   - Provide feedback on quality
   - Compare systems
5. **Data Collection**: In-app logging + post-session survey

### 4.3 Experiment Design and Hypotheses

#### Primary Hypothesis (H1)
```
Statement:
"AI-personalized recommendations with multi-objective route optimization
 significantly improve user satisfaction compared to baseline popularity-based
 recommendations with nearest-neighbor routing."

Null Hypothesis (H0):
No significant difference in user satisfaction (p ≥ 0.05)

Alternative Hypothesis (H1):
Significant improvement in user satisfaction (p < 0.05)

Metrics:
- User satisfaction survey score (primary)
- NPS (secondary)
- Task completion time (secondary)

Statistical Test:
- Independent t-test (test vs control groups)
- Effect size: Cohen's d ≥ 0.80 (large effect)
```

#### Secondary Hypothesis (H2)
```
Statement:
"Multi-objective routing optimization better balances diverse user
 constraints (budget, distance, crowd, sustainability) than greedy approaches."

Null Hypothesis (H0):
No difference in constraint satisfaction rates

Alternative Hypothesis (H2):
Our system satisfies ≥15% more constraints on average

Metrics:
- Budget compliance rate
- Time constraint compliance
- Accessibility compliance
- Sustainability POI ratio

Statistical Test:
- Chi-square test for categorical outcomes
- Paired t-test for route quality differences
```

#### Tertiary Hypothesis (H3)
```
Statement:
"Contextual bandit learning with Thompson Sampling achieves better
 exploration-exploitation balance than non-adaptive approaches."

Null Hypothesis (H0):
Learning approach has no impact on convergence speed

Alternative Hypothesis (H3):
Thompson Sampling converges ≥2x faster to user preferences

Metrics:
- Recommendation quality improvement over time
- Number of interactions to 80% performance
- Final NDCG@10 score

Statistical Test:
- Learning curve analysis
- Convergence speed comparison
```

### 4.4 Thesis Experiment Outline

**Phase 14 Experiment (3 weeks)**

**Week 1: Preparation**
- [ ] Finalize baseline system
- [ ] Prepare test environment
- [ ] Set up logging infrastructure
- [ ] Create survey forms
- [ ] Recruit participants
- [ ] Train facilitators

**Week 2: Execution**
- [ ] Run 70 user sessions (35 test, 35 control)
- [ ] Collect in-app metrics
- [ ] Administer post-session surveys
- [ ] Gather qualitative feedback

**Week 3: Analysis & Documentation**
- [ ] Clean and validate data
- [ ] Run statistical tests
- [ ] Generate visualizations
- [ ] Write thesis experiment section
- [ ] Create evaluation report

**Deliverables**
1. Complete user study dataset (anonymized)
2. Statistical analysis report
3. Thesis chapter on evaluation
4. Comparative performance report
5. Recommendations for future work

---

## 5. Testing Implementation Schedule

### Sprint 1 (Week 1-2)
- [ ] Unit test suite implementation (backend)
- [ ] Database integration tests
- [ ] API endpoint test coverage
- [ ] Mobile ViewModel tests

### Sprint 2 (Week 3)
- [ ] API integration tests
- [ ] Route optimization validation tests
- [ ] Mobile UI tests (critical paths)
- [ ] Performance baseline establishment

### Sprint 3 (Week 4)
- [ ] User study setup and recruitment
- [ ] Baseline system preparation
- [ ] Pre-deployment smoke testing
- [ ] User study execution (ongoing)

### Sprint 4 (Week 5-6)
- [ ] Complete user study
- [ ] Data analysis
- [ ] Documentation finalization
- [ ] Thesis evaluation section writing

---

## 6. Success Criteria

### Code Quality
- [ ] ≥80% test coverage (backend)
- [ ] ≥70% test coverage (mobile)
- [ ] Zero critical bugs
- [ ] All APIs documented

### Performance
- [ ] API response time p95 <500ms (single POI)
- [ ] Route generation <5 seconds
- [ ] Mobile app launch <2 seconds
- [ ] No OOM errors under load

### AI/ML
- [ ] Precision@10 ≥ 0.50
- [ ] NDCG@10 ≥ 0.70
- [ ] Route acceptance ≥ 0.85
- [ ] Bandit convergence within 2 weeks

### User Satisfaction
- [ ] SUS score ≥ 70
- [ ] Recommendation satisfaction ≥ 4.0/5.0
- [ ] NPS ≥ 20
- [ ] User satisfaction > baseline (p < 0.05)

---

## 7. Risk Mitigation

| Risk | Probability | Impact | Mitigation |
|------|------------|--------|-----------|
| User recruitment delays | High | Medium | Pre-recruit volunteers, incentivize participation |
| Database performance issues | Medium | High | Load test early, optimize queries, add indexes |
| Route optimization timeout | Medium | High | Implement heuristic fallback, set reasonable time limits |
| Statistical insignificance | Medium | High | Ensure sufficient sample size, measure effect sizes |
| Data quality issues | Low | Medium | Validate all inputs, implement constraints |

---

## 8. Deliverables Checklist

- [ ] Test Plan Document (this file) ✅
- [ ] Evaluation Metrics Guide
- [ ] Thesis Experiment Outline & Design
- [ ] Unit Test Suite (backend: 50+ tests)
- [ ] Integration Test Suite (20+ tests)
- [ ] API Test Suite (15+ tests)
- [ ] Mobile UI Test Suite (10+ tests)
- [ ] User Study Dataset & Analysis
- [ ] Statistical Report
- [ ] Thesis Evaluation Chapter
- [ ] Performance Benchmark Report
- [ ] Comparative Analysis Report

---

## 9. Next Steps

1. **Immediate** (This Week)
   - [ ] Create unit test skeletons
   - [ ] Set up test infrastructure
   - [ ] Finalize metric definitions

2. **Short-term** (Next 2 weeks)
   - [ ] Implement 80% of unit tests
   - [ ] Run integration tests
   - [ ] Establish performance baseline
   - [ ] Recruit user study participants

3. **Medium-term** (Weeks 3-4)
   - [ ] Complete test implementation
   - [ ] Execute user study
   - [ ] Analyze results
   - [ ] Document findings

---

**Status**: Ready for implementation  
**Owner**: Development team + Thesis advisor  
**Last Updated**: May 6, 2026  
**Next Review**: Weekly sprint planning
