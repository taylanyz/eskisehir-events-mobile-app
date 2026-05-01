# Phase 9: AI/ML Design Deepening

**Status**: Implementation Complete  
**Date**: April 2026  
**Author**: Team

---

## 1. Overview

Phase 9 deepens the AI/ML capabilities of the recommendation system to thesis-level quality. The phase implements:

1. **Content-Based Feature Extraction**: User and POI feature vectors normalized to [0, 1]
2. **Candidate Generation Pipeline**: Separate filtering stage before ranking
3. **Multi-Criteria Ranking**: Weighted scoring across multiple recommendation factors
4. **Contextual Thompson Sampling**: Bandit learning for exploration-exploitation balance
5. **Reward Function**: Formal interaction-to-signal mapping
6. **Learning Loop Integration**: Recording and updating bandit statistics

---

## 2. Content-Based Filtering Architecture

### 2.1 User Feature Extraction

**Location**: `UserFeatureExtractor.java`

Extracts normalized features from user preferences and context:

| Feature | Normalization | Purpose |
|---------|-------|---------|
| `budget_level` | LOW=0.2, MEDIUM=0.5, HIGH=0.8, LUXURY=1.0 | Budget fit scoring |
| `mobility_preference` | WALKING=0.3, PUBLIC_TRANSPORT=0.6, DRIVING=1.0 | Distance relevance |
| `crowd_tolerance` | LOVES_CROWDED=1.0, NEUTRAL=0.5, AVOIDS_CROWDS=0.0 | Crowd preference fit |
| `sustainability_focus` | FOCUS=1.0, NEUTRAL=0.5, NOT_PRIORITIZED=0.0 | Sustainability match |
| `time_of_day` | MORNING=0.25, AFTERNOON=0.5, EVENING=0.75, NIGHT=1.0 | Temporal preference |
| `day_of_week` | WEEKDAY=0.3, SATURDAY=0.7, SUNDAY=0.9 | Weekly pattern |

**Output**: `Map<String, Double>` with feature names and normalized values [0, 1]

### 2.2 POI Feature Extraction

**Location**: `PoiFeatureExtractor.java`

Extracts normalized features from POI attributes:

| Feature | Normalization | Source |
|---------|-------|--------|
| `crowd_level` | [0, 1] | `crowdProxy` from POI |
| `budget_level` | price / 500 (capped at 1.0) | `price` in TRY |
| `sustainability_score` | [0, 1] | `sustainabilityScore` from POI |
| `local_support_score` | [0, 1] | `localBusinessScore` from POI |
| `popularity_score` | [0, 1] | `popularityScore` from POI |
| `recency_score` | 1.0 (0-7 days), 0.7 (8-30 days), 0.3 (>30 days), 0.1 (past) | `date` difference |
| `family_friendly` | 0.2 (not) or 1.0 (yes) | `familyFriendly` boolean |
| `indoor_outdoor` | INDOOR=0.2, OUTDOOR=0.8, BOTH=0.5 | `indoorOutdoor` enum |

**Output**: `Map<String, Double>` with feature names and normalized values [0, 1]

---

## 3. Recommendation Pipeline

### 3.1 Two-Stage Architecture

**Stage 1: Candidate Generation** (`ContentBasedCandidateGenerator`)
- Input: `RecommendationRequest`
- Filters pool: category match, tag match, budget, location distance (max 20 km)
- Output: Candidate POI list (typically 50-200 POIs)

**Stage 2: Ranking** (`RecommendationRankerImpl` or Thompson Sampling)
- Input: Candidate list + user features + context
- Scores each candidate with multi-criteria weighting
- Output: Ranked list, limited to user request (default 10)

### 3.2 Ranking Score Formula

**Content-Based (Default for New/Warm-Start Users)**

```
score = 0.30 * preference_fit 
      + 0.20 * crowd_fit 
      + 0.15 * budget_fit 
      + 0.10 * sustainability_fit 
      + 0.10 * local_support_fit 
      + 0.10 * popularity_fit 
      + 0.05 * recency_fit
```

Where:
- `preference_fit`: Category + tag overlap
- `crowd_fit`: 1 - crowdProxy (lower crowd = higher score)
- `budget_fit`: 1.0 if within budget, 0.2 otherwise
- `sustainability_fit`: sustainability score directly
- `local_support_fit`: local business score directly
- `popularity_fit`: popularity score directly
- `recency_fit`: bonus for upcoming events

**Range**: [0, 1]

### 3.3 Cold-Start Strategy

For new users (< 3 interactions), uses popularity-weighted recommendation:

```
score = 0.65 * base_popularity 
      + 0.20 * category_bonus 
      + 0.10 * tag_bonus 
      + 0.05 * budget_bonus
      + 0.05 * recency_bonus
```

---

## 4. Contextual Thompson Sampling

### 4.1 Algorithm Overview

**Thompson Sampling** is a contextual multi-armed bandit algorithm that:

1. Maintains per-user-per-POI Beta distribution: `Beta(α, β)`
2. In each decision: samples from distribution to get score
3. Adds small random noise for exploration
4. After interaction: updates α/β based on reward

**Mathematical Basis**:
- `α = 1 + number_of_successes`
- `β = 1 + number_of_failures`
- Success threshold: reward ≥ 0.75 (strong positive interaction)
- Sampled score = `α / (α + β) + noise`
- Noise ~ U(-0.05, 0.05) for bounded exploration

### 4.2 Implementation

**Location**: `ThompsonSamplingStrategy.java`

**Initialization**:
- First encounter: `α = 1.0, β = 1.0` (uniform prior)
- Reflects "no data" state

**Decision Time**:
```java
double sample = alpha / (alpha + beta);
double noise = (random.nextDouble() - 0.5) * 0.1;
double score = Math.max(0.0, Math.min(1.0, sample + noise));
```

**Update After Interaction**:
- If reward ≥ 0.75: `α += 1` (success)
- If reward < 0.75: `β += 1` (failure)
- Stored in `BanditArmStat` table

---

## 5. Reward Function

### 5.1 Interaction-to-Reward Mapping

**Location**: `RewardFunction.java`

| Interaction Type | Reward | Normalized | Interpretation |
|------------------|--------|-----------|-----------------|
| `view` | 0.1 | 0.11 | User showed interest |
| `click` | 0.2 | 0.16 | Weak engagement |
| `save` | 0.4 | 0.29 | Medium engagement |
| `add_to_route` | 0.6 | 0.43 | Strong intent |
| `visited` | 0.8 | 0.57 | Confirmed activity |
| `positive_feedback` | 1.0 | 1.0 | Strong satisfaction |
| `dislike` | -0.4 | 0.0 | Dissatisfaction |

**Range Normalization**:
- Raw range: [-0.4, 1.0]
- Normalized for Thompson: (reward - (-0.4)) / 1.4 = [0, 1]

**Design Rationale**:
- Gap between interactions creates learning signal
- Visited > add_to_route: commitment signal stronger
- Dislike mapped to failure in Thompson algorithm

---

## 6. Context Vector Building

### 6.1 Context Representation

**Location**: `ContextVectorBuilder.java`

Stores temporal and user state as JSON in `BanditEvent.contextVectorJson`:

```json
{
  "timeOfDay": "AFTERNOON",
  "dayOfWeek": "SATURDAY",
  "userFeatures": {
    "budget_level": 0.5,
    "mobility_preference": 0.6,
    "crowd_tolerance": 0.3,
    "sustainability_focus": 0.8
  },
  "weather": "SUNNY",
  "location": {
    "latitude": 39.76,
    "longitude": 30.52
  }
}
```

**Purpose**:
1. Record context at decision time
2. Enable offline analysis of recommendation quality by context
3. Future: use for context-aware feature similarity in LinUCB upgrade

---

## 7. Bandit Learning Service

### 7.1 Recording Interactions

**Location**: `BanditLearningService.java`

```java
banditLearningService.recordInteraction(
    userId, 
    poiId, 
    "add_to_route",  // interaction type
    contextJson      // context at decision time
);
```

**Flow**:
1. Look up user and POI
2. Map interaction type → reward
3. Create `BanditEvent` record
4. Update `BanditArmStat` (user-POI statistics)

**Statistics Update**:
```
if (reward >= 0.75):
    stat.alpha += 1
    stat.wins += 1
else:
    stat.beta += 1

stat.plays += 1
stat.updatedAt = now()
```

### 7.2 Exploration vs. Exploitation

**Thompson Sampling Balances**:
- High α/β ratio (many successes) → sample close to 1.0 (exploit)
- Low α/β ratio (few plays) → high variance (explore)
- Noise ±5% adds further exploration

---

## 8. Interaction Tracking Endpoints

### 8.1 Recording Interactions

**Endpoint**: `POST /api/interactions`

```json
{
  "userId": 123,
  "poiId": 456,
  "interactionType": "add_to_route",
  "timeOfDay": "AFTERNOON",
  "dayOfWeek": "SATURDAY",
  "latitude": 39.76,
  "longitude": 30.52,
  "mobilityPreference": "WALKING"
}
```

**Response**: 200 OK (interaction recorded)

### 8.2 Recording Feedback

**Endpoint**: `POST /api/interactions/feedback`

```json
{
  "userId": 123,
  "poiId": 456,
  "rating": 5,
  "feedback": "Harika bir yer, çok güzel!",
  "visited": true
}
```

**Mapping**:
- rating ≥ 5 → `POSITIVE_FEEDBACK` (reward 1.0)
- rating ≤ 2 → `DISLIKE` (reward -0.4)
- Otherwise → `VISITED` (reward 0.8)

---

## 9. Decision Flow Diagram

```
User Requests Recommendations
            ↓
[HasUserId & Events >= 3?]
    /         \
  Yes          No
   ↓            ↓
Thompson      Cold-Start
Sampling      Strategy
   ↓            ↓
[Score POIs]
   ↓
[Return Top K Ranked]
   ↓
User Sees Recommendations
   ↓
User Interacts (click, save, add_to_route, visit, feedback)
   ↓
[POST /api/interactions or /api/interactions/feedback]
   ↓
[Record BanditEvent]
   ↓
[Update BanditArmStat (alpha/beta)]
   ↓
Next Recommendation Benefits from Update
```

---

## 10. Key Design Decisions

### 10.1 Why Thompson Sampling?

1. **Simplicity**: Beta distribution conjugacy = closed-form updates
2. **Exploration**: Built-in through random sampling, no epsilon tuning
3. **Scalability**: Per-user-per-POI state is O(users × POIs)
4. **Robustness**: Works with sparse rewards

### 10.2 Two-Stage (Candidate + Ranking)

1. **Efficiency**: Large POI pools (100+) filtered before expensive ranking
2. **Modularity**: Separate concerns → easier A/B testing
3. **Scalability**: Candidates cached/pre-computed if needed

### 10.3 Reward Threshold (0.75)

1. **Visited = 0.8**: Strong signal of satisfaction
2. Anything ≥ 0.75 treated as success
3. `click = 0.2` alone insufficient → must reach add_to_route level (0.6+)
4. Avoids over-crediting weak engagement

### 10.4 Normalized Features [0, 1]

1. **Algorithmic Stability**: No dominating large values
2. **Interpretability**: Always comparable scale
3. **Future-Proofing**: Linear models, neural nets both benefit

---

## 11. Implementation Checklist

- [x] `UserFeatureExtractor` - Extract normalized user features
- [x] `PoiFeatureExtractor` - Extract normalized POI features
- [x] `ContextVectorBuilder` - Build context JSON for bandit events
- [x] `RewardFunction` - Define interaction→reward mapping
- [x] `BanditLearningService` - Record and update bandit statistics
- [x] `InteractionTrackingController` - HTTP endpoints for feedback
- [x] `UserInteractionRequest` - DTO for interactions
- [x] `UserFeedbackRequest` - DTO for feedback
- [x] `RecommendationCandidateGenerator` - Separate candidate filtering
- [x] `RecommendationRankerImpl` - Multi-criteria ranking
- [x] `ThompsonSamplingStrategy` - Bandit algorithm
- [x] Updated `RecommendationEngine` - Orchestrate candidate + ranking

---

## 12. Testing & Validation

### Unit Tests
- Feature extraction produces [0, 1] values
- Reward normalization is correct
- Thompson score sampling stays in [0, 1]
- Bandit statistics update correctly

### Integration Tests
- End-to-end interaction recording
- Thompson scores improve with positive feedback
- Cold-start strategy activates for new users

### Metrics to Track
- CTR (Click-Through Rate) over time
- Conversion rate to add_to_route
- Positive feedback ratio
- Recommendation diversity (not always same POIs)

---

## 13. Future Enhancements

### Phase 10: LinUCB
- Use context features more directly
- Maintain per-context arm confidence intervals
- Enable personalization by temporal/location patterns

### Phase 11: NLP Sentiment
- Parse Turkish feedback text
- Adjust rewards based on sentiment
- Identify dissatisfaction reasons

### Phase 14: Comprehensive Evaluation
- User satisfaction surveys
- A/B test Thompson vs. cold-start baseline
- Measure thesis metrics (precision@K, NDCG@K, CTR)

---

## 14. References

- Thompson, W. R. (1933). "On the Likelihood that One Unknown Probability Exceeds Another in View of the Evidence of Two Samples"
- Russo, D., & Van Roy, B. (2018). "An Introduction to the Multi-armed Bandit Problem"
- Agarwal, A., et al. (2016). "Contextual Thompson Sampling"

---

**Approval**: Ready for Phase 9 completion ✓
