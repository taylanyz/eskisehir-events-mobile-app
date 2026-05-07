# Phase 14 - Evaluation Metrics Guide

**Document Purpose**: Detailed metric definitions, calculation methods, and tracking procedures for thesis evaluation

**Audience**: Development team, thesis advisor, evaluators

**Last Updated**: May 6, 2026

---

## 1. Recommendation System Metrics

### 1.1 Coverage Metrics

#### Cold-Start Coverage
```
Definition: Ability to make recommendations for new users with minimal history

Metric: Cold-Start Recommendation Coverage = (# users with ≥1 recommendation) / (# total new users)

Target: ≥ 95%

Calculation Method:
1. Create 10 new test user accounts
2. Set minimal preferences (only category + budget)
3. Request recommendations immediately
4. Check if system generates ≥5 recommendations
5. Success if all 10 users get recommendations

Notes:
- Tests system's ability to bootstrap from user profile alone
- Should rely on popularity baseline + preference matching
```

#### POI Coverage
```
Definition: What % of all POIs can be recommended under different user preferences

Metric: POI Coverage = (# recommendable POIs) / (# total POIs)

Target: ≥ 80%

Calculation Method:
1. For each user preference profile:
   - Create synthetic user with given preferences
   - Generate 100 recommendations
   - Track unique POIs recommended
2. Aggregate across 10 different user profiles
3. Calculate % of total POI database reached

Coverage by Category:
- Track separately for each of 29 POI categories
- All categories should have ≥50% coverage
```

#### Diversity in Recommendations
```
Definition: Average category diversity in top-K recommendations

Metric: Diversity = (# unique categories in top-K) / min(K, # total categories)

Target: ≥ 0.70 (70% of possible categories represented)

Calculation Method:
1. Generate recommendations for 100 diverse users
2. For each recommendation set (K=10):
   - Count unique POI categories
   - Divide by K (max possible)
3. Average across all users

Sub-metrics:
- Geographic diversity (districts represented)
- Price level diversity (budget spread)
- Type diversity (indoor/outdoor/mixed)
```

### 1.2 Personalization Metrics

#### Preference Adherence
```
Definition: % of recommendations matching explicit user preferences

Metric: Preference Match = (# recommendations matching filters) / (# total recommendations)

Target: ≥ 95%

Calculation Method:
1. For each user, set explicit filters:
   - Budget range: [min_budget, max_budget]
   - Categories: [selected_categories]
   - District: [district_preference]
   - Accessibility: [required/optional]
2. Generate 50 recommendations
3. Check: does recommendation fall within all filter ranges?
4. Calculate match percentage

Validation:
- Budget: 100% of items ≤ user_max_budget
- Category: 100% of items in selected categories
- District: 100% of items in preferred districts
```

#### Preference Learning Effectiveness
```
Definition: How well system learns user tastes over time

Metric: Learning Curve = Quality(t) / Quality(t-1)

Target: +5% improvement per week for first 4 weeks, then plateau

Calculation Method:
1. Track user over 4 weeks of interactions
2. Weekly recommendation quality scores:
   - Week 1 baseline quality = Q1
   - Week 2 quality = Q2
   - Calculate improvement = (Q2 - Q1) / Q1
3. Repeat for 10 users, average improvement
4. Expected: 5% > improvement > 1%

Convergence Target:
- After 4 weeks: 25% improvement over baseline
- After 8 weeks: ≤35% improvement (plateau reached)
```

### 1.3 Ranking Quality Metrics

#### Ranking Correlation
```
Definition: Agreement between recommendation ranking and user preference ranking

Metric: Spearman's Rank Correlation ρ

Target: ≥ 0.60

Calculation Method:
1. Show user top-10 recommendations
2. Ask user to rank by personal preference (1-10)
3. Calculate Spearman correlation between system ranking (1-10) and user ranking
4. Average across 50 users

Interpretation:
- ρ ≥ 0.70: Excellent alignment
- 0.50-0.70: Good alignment
- 0.30-0.50: Moderate alignment
- < 0.30: Poor alignment
```

#### Position Bias Analysis
```
Definition: Check if high-ranked items get more attention regardless of quality

Method:
1. Generate A/B test:
   - Control: random shuffle of top 20 recommendations
   - Test: ordered by system ranking
2. Measure CTR for each position
3. Calculate position bias = CTR(rank 1) / CTR(rank 10)

Target: Position Bias ≥ 2.0
- Indicates system ranking has significant effect on selection
- If ratio = 1.0, ranking doesn't matter (bad sign)
- If ratio > 3.0, position bias too strong (diminishing returns on ranking quality)
```

---

## 2. Route Optimization Metrics

### 2.1 Optimality Metrics

#### Gap to Optimal
```
Definition: How close generated routes are to mathematically optimal solution

Since true optimal is hard to compute for real-world problems, we estimate:

1. For 10-POI routes (solvable by solver):
   - Use OR-Tools to find optimal
   - Compare our solution cost
   - Gap = (our_cost - optimal_cost) / optimal_cost

2. For larger routes (heuristic comparison):
   - Implement 2-opt local search baseline
   - Gap = (our_cost - baseline_cost) / baseline_cost

Target: ≥ Gap ≤ 15% (within 15% of optimal/baseline)

Calculation:
- Route cost = distance_km * 1 + time_minutes * 0.1 + violations_count * 100
```

#### Constraint Satisfaction
```
Definition: % of user constraints satisfied in final route

Metric: Constraint Satisfaction = (# satisfied constraints) / (# total constraints)

Constraints Tracked:
1. Budget: Σ(POI costs) ≤ user_budget
2. Time: Σ(travel + visit time) ≤ time_limit
3. Distance: total_distance ≤ max_distance (if specified)
4. Accessibility: ≥80% of POIs wheelchair accessible (if required)
5. Family-friendly: ≥70% of POIs child-friendly (if required)
6. Must-see: ALL must-see POIs included

Target: 100% for hard constraints (budget, time, must-see)

Calculation:
- Hard constraints: all-or-nothing satisfaction
- Soft constraints: measured as degree of satisfaction
- Overall: weighted average (hard constraints weighted 2x)
```

#### Multi-Objective Pareto Analysis
```
Definition: Check if route optimizes multiple objectives without dominance

Generate 100 routes with varying objective weights:
- Eco-focused: minimizes carbon (sustainability 100%, distance 0%, crowd 0%)
- Distance-focused: minimizes distance (distance 100%, eco 0%, crowd 0%)
- Crowd-avoidant: avoids crowds (crowd 100%, distance 0%)
- Balanced: equal weights (distance 33%, eco 33%, crowd 33%)

For each category:
- Metric 1: Eco score (0-100)
- Metric 2: Distance score (0-100, lower is better)
- Metric 3: Crowd score (0-100, lower is better)

Expected Pareto Front:
- Eco-focused route: High eco (80+), Medium distance (50), Medium crowd (50)
- Distance-focused: Low distance (30), Medium eco (50), High crowd (70)
- Crowd-avoidant: High crowd resistance (80), Medium distance (60), Low eco (40)
- Balanced: All metrics ~60

Validation: Routes should NOT be dominated (no route strictly worse on all metrics)
```

### 2.2 Robustness Metrics

#### Sensitivity Analysis
```
Definition: How sensitive optimal route is to small input changes

1. Generate baseline route with parameters:
   - Budget: B
   - Time limit: T
   - Preferences: P

2. Perturb each parameter by ±10%:
   - Route_tightBudget = optimize(budget=0.9*B)
   - Route_looseBudget = optimize(budget=1.1*B)
   - Same for Time and Preferences

3. Measure route stability:
   - POI change rate = (# different POIs) / (total POIs)
   - Distance change = |dist_tight - dist_loose| / dist_baseline

Target:
- Budget ±10%: POI change ≤30%, Distance change ≤15%
- Time ±10%: POI change ≤25%, Distance change ≤12%
- Preferences ±10%: POI change ≤40%, Distance change ≤20%

Interpretation: Small input changes should produce similar routes (stability)
```

#### Edge Case Handling
```
Test cases that should be handled gracefully:

1. Tight Budget (≤50₺)
   - Should include ≥70% free POIs
   - Should not fail (fallback available)

2. Very Short Time (≤1 hour)
   - Should include ≥1 POI
   - Should minimize travel time

3. Conflicting Constraints
   - Budget 50₺ + Luxury POI required
   - System should: warn user + relax constraint + ask confirmation
   - Should NOT crash or return empty

4. All Crowded POIs
   - If all POIs crowded, pick least crowded
   - Should NOT filter out all options

5. Geographic Isolation
   - If user in remote area, extend search radius
   - Should NOT return empty

Success Metric: ≥95% of edge cases handled gracefully
```

---

## 3. Mobile App Metrics

### 3.1 Usability Metrics

#### System Usability Scale (SUS)
```
10-question standard survey (5-point Likert):

Q1. I think I would like to use this system frequently.
Q2. I found the system unnecessarily complex.
Q3. I thought the system was easy to use.
Q4. I think I would need the support of a technical person to be able to use this system.
Q5. I found the various functions in this system were well integrated.
Q6. I thought there was too much inconsistency in this system.
Q7. I would imagine most people would learn to use this system very quickly.
Q8. I found the system very cumbersome to use.
Q9. I felt very confident using the system.
Q10. I needed to learn a lot of things before I could get going with this system.

Calculation:
1. Code responses 1-5 (Strongly Disagree to Strongly Agree)
2. Odd questions: score - 1
3. Even questions: 5 - score
4. SUS = (sum of scores) * 2.5

Interpretation:
- 85+: Excellent
- 75-84: Good
- 68-74: Acceptable
- 51-67: Marginal
- <51: Not acceptable

Target: ≥ 75 (Good)
```

#### Task Completion Rate
```
Definition: % of assigned tasks users complete successfully

Sample Tasks:
1. Find a POI by category (museums)
2. View POI details and save it
3. Generate recommendations with preferences
4. Create a route with 5+ POIs
5. Provide feedback on recommendation
6. Share route with friend (if social features exist)

Metric: Completion = (# tasks completed) / (# tasks assigned)

Target: ≥ 95%

Measurement:
- Track via in-app analytics
- Time to completion (target: <3 minutes per task)
- Errors encountered (target: <1 error per 10 users)
```

#### Error Recovery
```
Definition: Ability to recover from errors without frustration

Test Scenarios:
1. Invalid input (negative budget, future dates)
   - System should: show clear error message, suggest fix
   - Time to recovery: <1 second

2. Network timeout
   - System should: show offline mode, cache data
   - Time to recovery: graceful degradation

3. POI loading failure
   - System should: show retry button, use cached data
   - Time to recovery: <3 seconds

Metric: Successful Recovery Rate = (# recovered) / (# errors encountered)

Target: ≥ 98%
```

### 3.2 Performance Metrics

#### App Launch Time
```
Definition: Time from app icon tap to home screen interactive

Measurement:
1. Cold start (app not in memory): target <2 seconds
2. Warm start (app in cache): target <1 second
3. Hot start (app in foreground): target <500ms

Test Protocol:
- Measure 10 times each condition
- Report mean ± std dev
- Record on device: Pixel 6 (2020 mid-range hardware)

Acceptable Thresholds:
- Cold start: <3 seconds (95th percentile)
- Warm start: <2 seconds (95th percentile)
```

#### Screen Load Time
```
Definition: Time for UI elements to become interactive

Screens to Measure:
1. POI List (50-100 items): <1 second
2. POI Details: <500ms
3. Recommendations (10 items): <2 seconds
4. Route Map: <1.5 seconds
5. Feedback Form: <300ms

Measurement:
- Use Android Profiler
- Record for 20 typical user sessions
- Report p50, p95, p99 latencies
```

#### Memory Usage
```
Definition: RAM consumption during typical usage

Targets:
- Idle memory: <100 MB
- With POI list (100 items): <150 MB
- With map loaded: <200 MB
- Peak during route optimization: <250 MB

Test Protocol:
1. Measure baseline memory
2. Load each screen
3. Measure peak memory
4. Record using Android Studio
5. Verify no memory leaks

Acceptable: No crashes, no ANR (Application Not Responding)
```

---

## 4. Backend Performance Metrics

### 4.1 API Response Time

#### Percentile Response Times
```
Measure for each endpoint:
- p50 (median): 50% of requests faster than this
- p95: 95% of requests faster than this
- p99: 99% of requests faster than this
- p99.9: 99.9% of requests faster than this

Targets (for typical queries):
| Endpoint | p50 | p95 | p99 |
|----------|-----|-----|-----|
| GET /pois (100 items) | 50ms | 150ms | 300ms |
| GET /recommendations | 200ms | 800ms | 2000ms |
| POST /routes/generate | 500ms | 2000ms | 5000ms |
| GET /stats | 100ms | 300ms | 800ms |

Measurement Method:
1. Generate 10,000 requests from load test tool
2. Record response time for each
3. Calculate percentiles
4. Report results with min/max/mean/stddev
```

#### Database Query Performance
```
Measure slow queries (>100ms):

Key Queries:
1. findByGeographicBounds: target <100ms for Eskişehir
2. findByCategory: target <50ms
3. findMostPopularPOIs: target <80ms
4. getStatistics: target <200ms

Optimization:
- Index analysis: verify all indexes used
- Query plan: examine EXPLAIN output
- N+1 problem: check for batch queries
- Caching: implement Redis for frequently accessed data
```

### 4.2 Scalability Metrics

#### Concurrent User Load
```
Definition: System performance under increasing user load

Test Protocol:
1. Start with 10 concurrent users
2. Increase by 10 every 30 seconds
3. Continue until either:
   - System reaches SLA breach
   - Request failure rate > 1%
   - Response time p95 > target * 2
4. Record breaking point

Targets:
- Handles 100 concurrent users
- Handles 1000 requests/minute
- Maintains <200ms p95 response time

Expected Breakpoint: 200-300 concurrent users before infrastructure scaling needed
```

#### Database Connection Pooling
```
- Pool size: 20-50 connections
- Queue timeout: 5 seconds
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes

Monitor:
- Active connections
- Queued requests
- Connection wait time

Alert if:
- > 80% pool utilization
- Queue wait > 1 second
- Idle connections not cycling
```

---

## 5. Metric Tracking and Dashboarding

### 5.1 Real-time Monitoring

**Backend Metrics Dashboard**
```
Tools: Prometheus + Grafana

Dashboards:
1. Request Metrics
   - Requests/second
   - Error rate (%)
   - Response times (p50, p95, p99)
   - Slow queries

2. System Metrics
   - CPU usage
   - Memory usage
   - Disk I/O
   - Database connection pool

3. Business Metrics
   - Recommendations generated/day
   - Routes optimized/day
   - User interactions/day
   - Feedback submissions/day

Alerts:
- Error rate > 1%
- Response time p95 > 500ms
- Memory > 80% of limit
- DB connections > 80% of pool
```

**Mobile Analytics**
```
Events to Track:
1. App lifecycle: launch, background, crash
2. User actions: search, save, feedback
3. Performance: screen load time, crash type
4. Network: API failures, retry attempts
5. Engagement: session duration, DAU, MAU

Implementation: Firebase Analytics + custom events
```

### 5.2 Weekly Metrics Review

**Metrics to Report Weekly**
```
Format: Metric | Target | Current | Status | Trend

Example:
| Recommendation CTR | ≥0.20 | 0.18 | ⚠️ | ↘ (-10% from last week) |
| API p95 latency | ≤200ms | 180ms | ✅ | → (stable) |
| Route acceptance | ≥0.85 | 0.82 | ⚠️ | ↗ (+5% from last week) |
```

---

## 6. Metric Validation and Quality

### 6.1 Data Quality Checks

- [ ] No null values in required fields
- [ ] Timestamp ordering validated
- [ ] Outlier detection (>3 std dev)
- [ ] Duplicate event detection
- [ ] Sample size validation (≥30 for statistical significance)

### 6.2 Statistical Validation

- [ ] Shapiro-Wilk test for normality
- [ ] T-test assumptions verified
- [ ] Confidence intervals calculated (95%)
- [ ] Effect sizes reported (Cohen's d, r)
- [ ] Multiple comparison correction (Bonferroni)

---

## 7. Metric Improvement Actions

If metric falls below target:

```
1. Investigate root cause
2. Implement fix (code, tuning, or data)
3. A/B test (if applicable)
4. Monitor for 1 week
5. Document learnings
6. Update process if needed
```

---

**Document Status**: Active - Updated Weekly  
**Owner**: QA Lead + Analytics Team  
**Next Review**: Weekly metrics meeting (Fridays 2 PM)
