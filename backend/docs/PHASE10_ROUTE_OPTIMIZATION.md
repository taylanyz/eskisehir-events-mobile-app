# Phase 10: Multi-Criteria Route Optimization Redesign

**Date**: May 2, 2026  
**Status**: Design Phase  
**Objective**: Transform route planning from simple path construction into formal, thesis-defensible multi-objective optimization

---

## 1. Problem Formulation

### 1.1 Core Problem Class
This is a variant of the **Orienteering Problem (OP)** / **Team Orienteering Problem with Time Windows (TOPTW)** combined with **multi-objective optimization**.

**Classic Orienteering Problem**: Given a set of nodes (POIs) with rewards and travel distances, find a path starting from origin, visiting a subset of nodes within a distance/time budget, maximizing total reward.

**Our Variant - Multi-Objective Orienteering Problem (MOOP)**:
- Multiple competing objectives: maximize preference fit + minimize crowd exposure + minimize budget usage + minimize carbon footprint + maximize local business support
- Soft constraints: user preferences (e.g., "must include this category", "avoid overcrowded")
- Time windows: closing times, opening times (soft)
- Dynamic context: weather, time of day, transportation mode
- User profile integration: learned user preferences from bandit model

### 1.2 Problem Definition

**Input**:
- Starting point: $O$ (origin, typically user location)
- Candidate POI set: $P = \{p_1, p_2, ..., p_n\}$ (pre-filtered from recommendation stage)
- User profile: $U$ (preferences, mobility, budget, sustainability focus, crowd tolerance)
- Context: $C$ (weather, time of day, day of week, transport mode)
- Budget constraints: time_max $T_{max}$, distance_max $D_{max}$, monetary_max $M_{max}$
- User parameters: weights $w_1, ..., w_k$ for objectives

**Output**:
- Ordered subset: $R = [p_1, p_2, ..., p_m]$ where $m \leq n$
- Route metadata: total distance, total duration, total cost, predicted satisfaction score, explainability summary

### 1.3 Objectives vs Constraints

**Hard Constraints** (must satisfy):
- Total travel duration ≤ $T_{max}$
- Total travel distance ≤ $D_{max}$
- Total monetary cost ≤ $M_{max}$

**Soft Objectives** (maximize weighted combination):
1. **Preference Fit** ($obj_1$): Average user preference score for visited POIs
2. **Crowd Avoidance** ($obj_2$): Minimize aggregate crowd exposure across route
3. **Budget Efficiency** ($obj_3$): Leave buffer in monetary budget (user prefers to not spend max)
4. **Sustainability** ($obj_4$): Minimize carbon footprint
5. **Local Support** ($obj_5$): Maximize local business representation
6. **Diversity** ($obj_6$): Encourage different POI categories (anti-repetition)

**Aggregate Objective Function**:
$$F(R, U, C) = \sum_{i=1}^{6} w_i(U, C) \cdot obj_i(R, U, C)$$

Where weights $w_i$ are derived from user profile and context.

### 1.4 Distance and Duration Calculation

**Distance**: Haversine distance between POI pairs (mock or real API fallback: OpenRouteService, OSRM)

**Duration**: 
- Travel time = distance / avg_speed(transport_mode)
- Visit time = POI.default_visit_duration (typically 30-120 min)
- Setup/overhead = 5 min per transition

$$T_{total} = \sum_{i=1}^{m-1} d(p_i, p_{i+1}) / v(transport) + \sum_{i=1}^{m} visit\_duration(p_i) + 5 \cdot (m-1)$$

---

## 2. Optimization Factors and Normalization

### 2.1 Individual Factors

| Factor | Type | Range | Meaning |
|--------|------|-------|---------|
| **distance** | continuous | [0, ∞) km | Total travel distance |
| **duration** | continuous | [0, ∞) minutes | Total travel time + visit time |
| **budget_usage** | ratio | [0, 1] | Fraction of monetary budget spent |
| **crowd_exposure** | weighted_avg | [0, 1] | Average crowd level * frequency |
| **sustainability** | continuous | [0, ∞) kg CO₂ | Estimated carbon footprint |
| **preference_fit** | weighted_avg | [0, 1] | Average POI preference score |
| **local_support** | ratio | [0, 1] | Fraction of local/independent POIs |
| **diversity** | ratio | [0, 1] | Category diversity (Shannon entropy / max) |

### 2.2 Normalization Strategy

All factors normalized to $[0, 1]$ for fair weighting:

#### 2.2.1 Distance Normalization
$$norm\_distance = 1 - \min\left(\frac{d_{total}}{d_{reference}}, 1\right)$$
where $d_{reference} = 20$ km (reference Eskişehir distance for typical tour)

**Intuition**: Shorter is better; score 1 at 0km, drops to 0 at 20km+

#### 2.2.2 Duration Normalization
$$norm\_duration = 1 - \min\left(\frac{t_{total}}{t_{max}}, 1\right)$$
where $t_{max}$ = user's time budget

**Intuition**: Efficient routes score high; at-budget routes score 0.

#### 2.2.3 Budget Usage Normalization
$$norm\_budget = 1 - budget\_usage\_ratio$$

**Intuition**: Leaving budget is better (risk buffer); spending all = 0, spending 50% = 0.5

#### 2.2.4 Crowd Exposure Normalization
$$norm\_crowd = 1 - weighted\_avg\_crowd\_level$$

**Intuition**: Lower crowds score higher. $[0, 1] \to [1, 0]$

#### 2.2.5 Sustainability Normalization
$$norm\_sustainability = 1 - \min\left(\frac{co2\_kg}{co2\_reference}, 1\right)$$
where $co2\_reference = 5$ kg (reference carbon for Eskişehir tour)

**Intuition**: Lower emissions better; 0 kg = score 1, 5+ kg = score 0.

#### 2.2.6 Preference Fit Normalization
$$norm\_preference = avg(user\_preference\_scores)$$

**Range**: Already $[0, 1]$ from Phase 9 feature extraction.

#### 2.2.7 Local Support Normalization
$$norm\_local = \frac{\#local\_pois}{\#total\_pois}$$

**Range**: $[0, 1]$ naturally.

#### 2.2.8 Diversity Normalization
$$norm\_diversity = \frac{H(categories)}{H_{max}} \text{ where } H = -\sum p_c \log p_c$$

**Range**: Shannon entropy normalized to $[0, 1]$.

### 2.3 Objective Functions (Final Form)

1. **Preference Fit**: $obj_1 = norm\_preference$
2. **Crowd Avoidance**: $obj_2 = norm\_crowd$
3. **Budget Efficiency**: $obj_3 = norm\_budget$
4. **Sustainability**: $obj_4 = norm\_sustainability$
5. **Local Support**: $obj_5 = norm\_local$
6. **Diversity**: $obj_6 = norm\_diversity$

All now in $[0, 1]$, directly comparable for weighted aggregation.

---

## 3. Weighted Aggregation and Adaptation

### 3.1 Initial Weight Defaults

Based on thesis framing (balanced user-centric + sustainability):

```
w_preference = 0.35  (primary: user satisfaction)
w_crowd = 0.20       (secondary: experience quality)
w_budget = 0.15      (tertiary: affordability)
w_sustainability = 0.12 (sustainability awareness)
w_local = 0.10       (local economy support)
w_diversity = 0.08   (variety and interest)
```

Sum = 1.0 (normalized weights)

### 3.2 Dynamic Weight Adjustment Based on User Profile

Adjustments derived from learned user preferences (Phase 9 bandit model):

```java
if (user.sustainability_focus > 0.7) {
    w_sustainability *= 1.5;  // Scale up
    w_budget *= 0.8;           // Trade cost efficiency for sustainability
}

if (user.crowd_tolerance < 0.3) {
    w_crowd *= 1.3;  // Avoid crowds is critical
}

if (user.budget_level == HIGH) {
    w_budget *= 0.5;  // Budget less constraining
}

// Renormalize so sum = 1.0
normalize_weights(w);
```

### 3.3 Context-Based Modulation

**Time-of-day effect**:
- Morning (6-9am): Increase $w\_crowd$ (avoid early rush)
- Peak (12-1pm, 6-7pm): Increase $w\_crowd$ (clear peak times)
- Late evening (9pm+): Decrease $w\_duration$ (user may have flex time)

**Weather effect**:
- Rain: Increase preference for indoor POIs, decrease distance preference
- Hot (>30°C): Increase crowd avoidance (thermal stress + crowding)

**Transportation mode**:
- Walking: Higher distance sensitivity, lower speed (≈5 km/h)
- Public transport: Moderate distance, include transfer times
- Driving: Lower distance sensitivity, include parking time and cost

---

## 4. Constraint Handling

### 4.1 Hard Constraints

**Time Budget Constraint**:
$$\sum_{i=1}^{m-1} \frac{d(p_i, p_{i+1})}{v(mode)} + \sum_{i=1}^{m} visit\_time(p_i) \leq T_{max}$$

**Distance Budget Constraint**:
$$\sum_{i=1}^{m-1} d(p_i, p_{i+1}) \leq D_{max}$$

**Monetary Budget Constraint**:
$$\sum_{i=1}^{m} cost(p_i) \leq M_{max}$$

### 4.2 Soft Preferences as Constraint Classes

**Must-See Category**: POI from category $C_{must}$ is required in route (high priority in objective, or convert to hard constraint for "essential" preference)

**Avoid Overcrowded**: POIs with crowd level > threshold are penalized; not hard-excluded (still might pick if high fit)

**Accessibility**: If user has mobility constraint, filter POIs pre-optimization

---

## 5. Solver Design

### 5.1 OR-Tools Java Binding Evaluation

**Pros**:
- Production-grade performance for vehicle routing, assignment, TSP, OP variants
- Well-tested C++ core with Java JNI bindings
- Supports time windows, capacities, costs
- Active development and community

**Cons**:
- JNI overhead for small problems
- Multi-objective typically requires custom wrapper (scalarization)
- Dependency on native libraries (.so, .dll)
- Learning curve for library API

**Decision**: Use OR-Tools for MVP + custom Java fallback heuristic for edge cases (cold start, quick response)

### 5.2 Primary Solver: Composite Scalarization + OR-Tools

**Step 1**: Scalarize multi-objective → single objective:
$$F_{scalar} = \sum_{i=1}^{6} w_i \cdot obj_i$$

**Step 2**: Translate to OR-Tools Vehicle Routing Problem (VRP) variant:
- Nodes: POIs in candidate set
- Vehicle: 1 (single route)
- Distance matrix: Haversine distances
- Time matrix: Travel time + visit duration
- Capacity: Monetary cost capacity
- Cost coefficients: Derived from objective weights

**Step 3**: Solve with OR-Tools local search (guided by scalarized objective)

**Step 4**: Post-process: Extract best route, compute full multi-objective scores

### 5.3 Fallback Heuristic: Nearest Neighbor + Local Search

For scenarios where OR-Tools unavailable or timeout:

**Nearest Neighbor Construction**:
1. Start at origin $O$
2. While budget allows:
   - Find nearest unvisited POI $p$ within budget
   - Add $p$ to route
   - Update remaining budget

**Local Search (2-opt)**:
1. Repeat until no improvement:
   - For each pair of POIs in route:
     - Try reversing segment between them
     - Accept if objective improves
2. Return best route found

**Time Limit**: 500ms hard stop to ensure responsiveness

### 5.4 Fallback Heuristic: Random Sampling

Emergency fallback (network down, OR-Tools JNI error):
- Sample 5 random routes respecting budget constraints
- Return highest-scoring route by scalarized objective

---

## 6. Route Explainability

### 6.1 Score Breakdown

For each generated route, provide structured explanation:

```json
{
  "route_id": "route_abc123",
  "origin": {"name": "Başak Caddesi", "lat": 39.77, "lng": 30.53},
  "pois": [
    {"id": "poi_1", "name": "Museum", "order": 1, "visit_duration": 60, "cost": 50, "crowd_level": 0.6},
    {"id": "poi_2", "name": "Park", "order": 2, "visit_duration": 45, "cost": 0, "crowd_level": 0.2}
  ],
  "metrics": {
    "total_distance_km": 8.5,
    "total_duration_minutes": 195,
    "total_cost_tl": 70,
    "co2_kg": 1.2,
    "local_poi_count": 1
  },
  "scores": {
    "preference_fit": 0.82,
    "crowd_avoidance": 0.75,
    "budget_efficiency": 0.65,
    "sustainability": 0.88,
    "local_support": 0.5,
    "diversity": 0.7,
    "final_composite_score": 0.72
  },
  "explanation": "This route balances your preference for cultural attractions (museums highly rated) with sustainable travel via public transport. Two POIs selected. Estimated CO2 footprint: 1.2 kg (low). Budget buffer: ₺30. Mild crowd exposure at peak hours."
}
```

### 6.2 Explanation Generation Logic

**Template-based narratives**:
- If sustainability score high: Highlight carbon-efficient selection
- If crowd score low: Warn about likely crowd exposure (guide alternative)
- If preference fit high: Confirm alignment with learned preferences
- If local support high: Highlight local business support

**Score drivers**:
- Highlight which POI contributes most to each objective
- Rank POIs by preference fit contribution

---

## 7. API Contract

### 7.1 RouteOptimizationController Endpoints

**POST /api/routes/optimize**
```json
{
  "user_id": "user_123",
  "origin_lat": 39.77,
  "origin_lng": 30.53,
  "candidate_poi_ids": ["poi_1", "poi_2", "poi_3", ...],
  "time_budget_minutes": 240,
  "distance_budget_km": 20,
  "monetary_budget_tl": 500,
  "transport_mode": "public_transport",  // or "walking", "driving"
  "context": {
    "weather": "sunny",
    "current_time": "2026-05-02T14:30:00Z",
    "day_of_week": "Thursday"
  },
  "weights_override": {  // optional user weight adjustment
    "w_sustainability": 0.20
  }
}
```

**Response (200 OK)**:
```json
{
  "route": {...},  // Full route object from 6.1
  "alternatives": [
    {"route": {...}, "score": 0.71},
    {"route": {...}, "score": 0.68}
  ],
  "optimization_stats": {
    "solver_used": "or_tools",
    "computation_time_ms": 245,
    "explored_candidates": 523
  }
}
```

---

## 8. Implementation Roadmap

### 8.1 Core Classes to Create

**`RouteOptimizationService.java`** (Orchestrator)
- Accepts optimization request
- Loads user profile + context
- Calls solver + post-processor
- Returns result with explanations

**`RouteOptimizer.java`** (Interface)
- Contract for solver implementations

**`OrToolsRouteOptimizer.java`** (Primary implementation)
- Wraps OR-Tools VRP
- Scalarization logic
- Timeout + error handling

**`GreedyRouteOptimizer.java`** (Fallback)
- Nearest neighbor + 2-opt heuristic
- Fast response guarantee

**`RouteExplainer.java`**
- Score breakdown generation
- Narrative explanation builder
- Template system for different scenario

**`RouteScoringService.java`**
- Individual objective calculation
- Normalization logic
- Weight aggregation

**DTOs**:
- `RouteOptimizationRequest`
- `RouteOptimizationResponse`
- `RouteScoreBreakdown`
- `RouteMetrics`

### 8.2 Configuration and Tuning

**`RouteOptimizationConfig.java`**:
- OR-Tools timeout (default 2 seconds)
- Distance reference (20 km)
- Default weights
- Carbon emission factor per transport mode

---

## 9. Testing Strategy

### 9.1 Unit Tests

- **Distance/Duration Calc**: Verify haversine, visit time, total aggregation
- **Normalization**: Test boundary cases (0, 1, over-budget scenarios)
- **Weight Aggregation**: Verify weighted sum with various weight sets
- **Heuristic Solver**: Compare NN+2opt against exhaustive small instances

### 9.2 Integration Tests

- **Full Optimization Flow**: Given request → verify route validity (budget compliance)
- **OR-Tools Integration**: Mock distance matrix, verify VRP formulation
- **Fallback Triggering**: Verify heuristic used when OR-Tools unavailable

### 9.3 Acceptance Tests

- **Scenario A (Thrifty)**: High budget sensitivity, low crowd tolerance → Route should maximize budget efficiency + crowd avoidance
- **Scenario B (Explorer)**: High diversity preference, sustainability focus → Route should balance category mix + low carbon
- **Scenario C (Social)**: Low crowd tolerance + high local support → Route should favor local, uncrowded venues

---

## 10. Future Enhancements

- **Multi-objective Optimization**: Pareto frontier generation (n alternative routes with different trade-offs)
- **Real-time Crowd Data**: Integrate live crowd sensors / Google Popular Times API
- **Advanced Routing**: Turn-by-turn directions with actual street networks (OSRM / Vroom)
- **Dynamic Replanning**: Monitor user progress, replan if behind/ahead of schedule
- **Accessibility Routing**: Wheelchair ramps, elevators, rest stops
- **Group Routing**: Multi-user synchronized routes with shared POIs

---

## References

- Souffriau, W., & Vansteenwegen, P. (2016). "Orienteering problems: definitions, variations and solution approaches." International Journal of Management Reviews.
- Google OR-Tools documentation: https://developers.google.com/optimization
- Scalarization techniques for multi-objective optimization.
- Carbon emissions calculation model (transport mode dependent).
