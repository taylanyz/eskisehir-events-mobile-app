# Phase 13: POI Proxy Scoring Rules

## Overview

Comprehensive guide to calculating proxy metrics for Points of Interest in Eskişehir. These scoring rules generate realistic, statistically sound metrics for recommendation engine training and route optimization.

**Version**: 1.0  
**Status**: Complete ✅  
**Last Updated**: May 6, 2026  

---

## 1. Popularity Score (0-100)

### 1.1 Definition
Popularity Score represents the relative visit frequency and tourist appeal of a POI. Higher scores indicate more visited locations.

### 1.2 Base Scoring Formula

```
popularityScore = (categoryWeight × 0.3) + (ratingInfluence × 0.2) + (reviewCountInfluence × 0.3) + (seasonalityFactor × 0.2)
```

### 1.3 Component Calculations

#### 1.3.1 Category Weight (0-100)
Base popularity tier by category type:

| Category | Weight | Rationale |
|----------|--------|-----------|
| MUSEUM | 92 | Major tourist attractions |
| HISTORICAL_SITE | 95 | Core tourism draw |
| MOSQUE | 88 | Religious + historical significance |
| CHURCH | 85 | Religious + historical |
| PARK | 72 | Regular local use |
| RIVERSIDE_SPOT | 68 | Seasonal recreational |
| CAFE | 55 | Local community spaces |
| RESTAURANT | 60 | Regular destination |
| BAKERY | 40 | Convenience, not destination |
| MARKET | 65 | Mix of tourist/local |
| SHOPPING_CENTER | 50 | Standard commercial |
| CULTURAL_VENUE | 80 | Event-driven appeal |
| HOTEL | 65 | Accommodation-driven |
| OTHER | 45 | Default category |

#### 1.3.2 Rating Influence (0-100)
Based on average user rating and review count:

```
if reviewCount == 0:
    ratingInfluence = 50  # Neutral starting point
else if reviewCount < 10:
    ratingInfluence = (averageRating / 5.0) × 60  # Lower weight for few reviews
else if reviewCount < 50:
    ratingInfluence = (averageRating / 5.0) × 80  # Moderate weight
else:
    ratingInfluence = (averageRating / 5.0) × 100  # Full weight for many reviews
```

**Examples**:
- 4.5/5.0 with 100+ reviews: (4.5/5.0) × 100 = **90.0**
- 4.0/5.0 with 5 reviews: (4.0/5.0) × 60 = **48.0**
- No reviews yet: **50.0**

#### 1.3.3 Review Count Influence (0-100)
Normalized review count with logarithmic scaling:

```
reviewCountInfluence = min(100, log(reviewCount + 1) × 20)
```

**Lookup Table**:
| Reviews | Influence |
|---------|-----------|
| 0 | 0.0 |
| 1 | 6.0 |
| 5 | 19.1 |
| 10 | 25.8 |
| 50 | 42.9 |
| 100 | 49.6 |
| 500 | 62.2 |
| 1000+ | 69.9 |

#### 1.3.4 Seasonality Factor (0-100)
Adjustment for seasonal tourism patterns:

| District | Season | Factor | Example |
|----------|--------|--------|---------|
| ODUNPAZARI | Winter | 0.85 | Historical sites still popular |
| ODUNPAZARI | Summer | 1.15 | Peak tourist season |
| SAZOVA | Summer | 1.20 | Parks/outdoor venues peak |
| SAZOVA | Winter | 0.70 | Reduced outdoor activity |
| PARKS | Spring/Fall | 1.10 | Moderate weather |
| RIVERSIDE | Summer | 1.25 | Water recreation |

**Default**: 1.0 (no seasonal adjustment)

### 1.4 Examples

#### Example 1: Major Museum
```
Museum: Historical significance, 150 reviews, 4.6/5.0 avg
  categoryWeight = 92
  ratingInfluence = (4.6/5.0) × 100 = 92.0
  reviewCountInfluence = min(100, log(151) × 20) = 100.0
  seasonalityFactor = 1.0
  
popularityScore = (92 × 0.3) + (92.0 × 0.2) + (100.0 × 0.3) + (1.0 × 0.2)
                = 27.6 + 18.4 + 30.0 + 0.2
                = 76.2 → rounds to 76.0
```

#### Example 2: New Local Cafe
```
Cafe: Recently opened, 2 reviews, 4.8/5.0 avg
  categoryWeight = 55
  ratingInfluence = (4.8/5.0) × 60 = 57.6
  reviewCountInfluence = min(100, log(3) × 20) = 12.2
  seasonalityFactor = 1.0
  
popularityScore = (55 × 0.3) + (57.6 × 0.2) + (12.2 × 0.3) + (1.0 × 0.2)
                = 16.5 + 11.52 + 3.66 + 0.2
                = 31.88 → rounds to 32.0
```

#### Example 3: Historic Park
```
Park: Restored recently, 80 reviews, 4.3/5.0, summer season
  categoryWeight = 72
  ratingInfluence = (4.3/5.0) × 90 = 77.4 (50 < 80 < reviews)
  reviewCountInfluence = min(100, log(81) × 20) = 85.6
  seasonalityFactor = 1.20 (summer parks)
  
popularityScore = (72 × 0.3) + (77.4 × 0.2) + (85.6 × 0.3) + (1.2 × 0.2)
                = 21.6 + 15.48 + 25.68 + 0.24
                = 63.0 → rounds to 63.0
```

### 1.5 Implementation Notes
- Minimum: 0.0 (absolutely no appeal)
- Maximum: 100.0 (most popular attractions)
- Update Frequency: Weekly (aggregate review data)
- Storage: Float with 1 decimal precision
- Cache: 24-hour cache for performance

---

## 2. Crowd Proxy Score (0-100)

### 2.1 Definition
Crowd Proxy Score estimates the relative crowd density at a given POI at a typical mid-afternoon weekday time (14:00-16:00 UTC+3).

### 2.2 Base Scoring Formula

```
crowdProxyScore = (categoryBaseline × 0.25) + (timeOfDayFactor × 0.25) + (dayOfWeekFactor × 0.20) + (capacityFactor × 0.15) + (seasonalFactor × 0.15)
```

### 2.3 Component Calculations

#### 2.3.1 Category Baseline (0-100)
Expected crowd levels by category:

| Category | Baseline | Reasoning |
|----------|----------|-----------|
| MUSEUM | 75 | Draws consistent tourist crowds |
| HISTORICAL_SITE | 70 | Tourist attraction |
| MOSQUE | 35 | Only busy during prayer times |
| PARK | 55 | Weather dependent, varies hourly |
| RIVERSIDE_SPOT | 60 | Social hub, moderate crowds |
| CAFE | 65 | Social gathering place |
| RESTAURANT | 72 | Popular dining destination |
| MARKET | 80 | Inherently crowded spaces |
| SHOPPING_CENTER | 70 | Busy commercial space |
| CULTURAL_VENUE | 50 | Event-dependent |
| HOTEL | 40 | Internal visitors only |
| CHURCH | 40 | Lower regular traffic |
| LIBRARY | 45 | Generally quieter |
| GALLERY | 50 | Moderate, variable crowds |
| OTHER | 50 | Default baseline |

#### 2.3.2 Time of Day Factor (0-100)

Crowd multiplier based on time of day. Default calculation assumes 14:00-16:00 (mid-afternoon):

```
if hour < 6:
    timeOfDay = 0.1  # Very early morning
else if hour < 9:
    timeOfDay = 0.4  # Morning
else if hour < 12:
    timeOfDay = 0.8  # Late morning
else if hour < 14:
    timeOfDay = 0.9  # Noon to 2 PM
else if hour < 17:
    timeOfDay = 1.0  # Peak afternoon (reference time)
else if hour < 19:
    timeOfDay = 0.85 # Early evening
else if hour < 22:
    timeOfDay = 0.7  # Evening
else:
    timeOfDay = 0.2  # Late night
```

**Time-based lookup**: Adjusts category baseline for specific hours.

#### 2.3.3 Day of Week Factor (0-100)

Weekly crowd patterns:

```
dayOfWeekMap = {
    "Monday": 1.0,      # Reference day
    "Tuesday": 0.95,    # Slightly quieter
    "Wednesday": 0.93,  # Mid-week dip
    "Thursday": 1.05,   # Preparing for weekend
    "Friday": 1.20,     # Weekend begins
    "Saturday": 1.35,   # Peak weekend
    "Sunday": 1.25      # High weekend
}
```

**Note**: Applied as multiplier to time-of-day factor.

#### 2.3.4 Capacity Factor (0-100)

Based on estimated venue capacity:

```
estimatedCapacity = (estimatedVisitDuration / 60) × peakHourlyThroughput

if estimatedCapacity < 30:
    capacityFactor = 0.3  # Very small venue, easily full
else if estimatedCapacity < 100:
    capacityFactor = 0.5  # Small venue
else if estimatedCapacity < 300:
    capacityFactor = 0.7  # Medium venue
else if estimatedCapacity < 1000:
    capacityFactor = 0.85 # Large venue
else:
    capacityFactor = 1.0  # Very large capacity
```

#### 2.3.5 Seasonality Factor (0-100)

Tourist season multiplier:

```
if month in [12, 1, 2]:  # Winter
    seasonalFactor = 0.85
else if month in [3, 4, 5]:  # Spring
    seasonalFactor = 1.10
else if month in [6, 7, 8]:  # Summer
    seasonalFactor = 1.25
else:  # Fall (9, 10, 11)
    seasonalFactor = 1.05
```

### 2.4 Examples

#### Example 1: Museum on Saturday
```
Museum: Mid-afternoon Saturday, Spring
  categoryBaseline = 75
  timeOfDayFactor = 1.0 (14:00-16:00 reference)
  dayOfWeekFactor = 1.35 (Saturday)
  capacityFactor = 0.85 (large venue)
  seasonalFactor = 1.10 (spring)

crowdProxyScore = (75 × 0.25) + (100 × 0.25) + (135 × 0.20) + (85 × 0.15) + (110 × 0.15)
                = 18.75 + 25.0 + 27.0 + 12.75 + 16.5
                = 100.0 → capped at 100.0
```

#### Example 2: Park on Weekday Morning
```
Park: 09:00 (late morning) on Wednesday, Summer
  categoryBaseline = 55
  timeOfDayFactor = 0.8 (80% of peak)
  dayOfWeekFactor = 0.93 (Wednesday)
  capacityFactor = 1.0 (large open space)
  seasonalFactor = 1.25 (summer)

crowdProxyScore = (55 × 0.25) + (80 × 0.25) + (93 × 0.20) + (100 × 0.15) + (125 × 0.15)
                = 13.75 + 20.0 + 18.6 + 15.0 + 18.75
                = 86.1
```

#### Example 3: Cafe on Quiet Weekday
```
Cafe: 23:00 on Tuesday, Winter
  categoryBaseline = 65
  timeOfDayFactor = 0.2 (20% of peak - late night)
  dayOfWeekFactor = 0.95 (Tuesday)
  capacityFactor = 0.5 (small venue)
  seasonalFactor = 0.85 (winter)

crowdProxyScore = (65 × 0.25) + (20 × 0.25) + (95 × 0.20) + (50 × 0.15) + (85 × 0.15)
                = 16.25 + 5.0 + 19.0 + 7.5 + 12.75
                = 60.5
```

### 2.5 Implementation Notes
- Default: Calculated at 14:00 weekday mid-season
- Update Frequency: Real-time calculation based on current time
- Range: 0.0 (empty) to 100.0+ (capped at 100)
- Cache: 1-hour cache by hour
- Recommendation: Use for "best times to visit" suggestions

---

## 3. Sustainability Score (0-100)

### 3.1 Definition
Sustainability Score measures environmental and social responsibility, combining ecological impact, cultural preservation, community engagement, and accessibility.

### 3.2 Base Scoring Formula

```
sustainabilityScore = (environmentalImpact × 0.35) + (localCommunityBenefit × 0.30) + (culturalPreservation × 0.20) + (accessibilityScore × 0.15)
```

### 3.3 Component Calculations

#### 3.3.1 Environmental Impact (0-100)

Measures ecological footprint and green initiatives:

```
baseScore = 50  # Neutral starting point

# Green space contribution
if indoorOutdoor == "OUTDOOR":
    baseScore += 20
else if indoorOutdoor == "MIXED":
    baseScore += 10

# Sustainability features
sustainabilityBonus = 0
if tags.contains("eco-friendly"):
    sustainabilityBonus += 15
if tags.contains("green-space"):
    sustainabilityBonus += 10
if tags.contains("renewable-energy"):
    sustainabilityBonus += 10
if tags.contains("locally-sourced"):
    sustainabilityBonus += 8

# Size/impact penalty
if estimatedVisitDuration > 300:  # Full day attraction
    sizeModifier = 0.90  # Slight penalty for major venue
else:
    sizeModifier = 1.0

environmentalImpact = min(100, (baseScore + sustainabilityBonus) × sizeModifier)
```

#### 3.3.2 Local Community Benefit (0-100)

Measures support for local economy and community:

```
baseScore = 50

# Category-based baseline
if category in ["MARKET", "LOCAL_BUSINESS", "CAFE", "CRAFT_WORKSHOP", "BAKERY"]:
    baseScore = 70  # Local business hub
else if category in ["RESTAURANT"]:
    baseScore = 65  # Food service sector
else if category in ["CULTURAL_VENUE", "GALLERY", "THEATER"]:
    baseScore = 80  # Cultural support
else if category in ["HOTEL", "GUESTHOUSE"]:
    baseScore = 60  # Tourism job creation
else:
    baseScore = 50

# Employment impact
if estimatedVisitDuration > 120:
    baseScore += 10  # Likely full-time staff

# Local ownership
if tags.contains("family-owned"):
    baseScore += 10
if tags.contains("cooperative"):
    baseScore += 12

localCommunityBenefit = min(100, baseScore)
```

#### 3.3.3 Cultural Preservation (0-100)

Measures preservation of Turkish culture and heritage:

```
baseScore = 50

# Cultural significance
if category in ["MUSEUM", "HISTORICAL_SITE", "MOSQUE", "CHURCH"]:
    baseScore = 85
else if category in ["CULTURAL_VENUE", "GALLERY", "THEATER"]:
    baseScore = 80
else if category in ["BAZAAR", "MARKET", "CRAFT_WORKSHOP"]:
    baseScore = 70
else if tags.contains("heritage"):
    baseScore = 75

# Educational value
if tags.contains("educational"):
    baseScore += 10
if tags.contains("historical-documentation"):
    baseScore += 8

# Community engagement
if phoneNumber and website and email:  # Good information access
    baseScore += 5

culturalPreservation = min(100, baseScore)
```

#### 3.3.4 Accessibility Score (0-100)

Measures accessibility for diverse populations:

```
baseScore = 50

# Wheelchair accessibility
if wheelchairAccessible:
    baseScore += 20
else if accessibilityLevel >= 3:
    baseScore += 10

# Diverse audience support
if familyFriendly:
    baseScore += 10
if childrenFriendly:
    baseScore += 5
if seniorFriendly:
    baseScore += 10

# Transportation access
if publicTransitAccess:
    baseScore += 10
if parkingAvailable:
    baseScore += 5

# Facilities
if restRoomAvailable:
    baseScore += 5
if foodServiceAvailable:
    baseScore += 3

accessibilityScore = min(100, baseScore)
```

### 3.4 Examples

#### Example 1: Traditional Bazaar
```
Bazaar: Family-owned, heritage site, outdoor, accessible parking

environmentalImpact:
  - outdoor: +20
  - local-tourism: +8
  - baseScore = 50 + 20 + 8 = 78

localCommunityBenefit:
  - bazaar category: 70
  - family-owned: +10
  - baseScore = 80

culturalPreservation:
  - bazaar category: 70
  - heritage tag: +75 = 85 (capped)
  - educational: +10 → 80

accessibilityScore:
  - baseScore: 50
  - parkingAvailable: +5
  - publicTransitAccess: +10
  - restRoom: +5
  - = 70

sustainabilityScore = (78 × 0.35) + (80 × 0.30) + (80 × 0.20) + (70 × 0.15)
                    = 27.3 + 24.0 + 16.0 + 10.5
                    = 77.8 → 78.0
```

#### Example 2: Shopping Center
```
ShoppingCenter: Modern, accessible, good transport, but limited heritage

environmentalImpact:
  - indoor: 50
  - no eco-tags: 0
  - large venue: × 0.90 = 45

localCommunityBenefit:
  - shopping center: 50
  - employment: +10
  - = 60

culturalPreservation:
  - commercial venue: 50

accessibilityScore:
  - baseScore: 50
  - wheelchairAccessible: +20
  - publicTransitAccess: +10
  - parkingAvailable: +5
  - foodService: +3
  - = 88

sustainabilityScore = (45 × 0.35) + (60 × 0.30) + (50 × 0.20) + (88 × 0.15)
                    = 15.75 + 18.0 + 10.0 + 13.2
                    = 56.95 → 57.0
```

#### Example 3: Eco-Friendly Cafe
```
Cafe: Outdoor seating, eco-friendly, family-owned, accessible

environmentalImpact:
  - outdoor mixed: 15
  - eco-friendly: +15
  - locally-sourced: +8
  - = (50 + 15 + 15 + 8) = 88

localCommunityBenefit:
  - cafe category: 70
  - family-owned: +10
  - short duration: 0
  - = 80

culturalPreservation:
  - market/cafe area: 70
  - heritage area: +5
  - = 75

accessibilityScore:
  - baseScore: 50
  - familyFriendly: +10
  - publicTransitAccess: +10
  - = 70

sustainabilityScore = (88 × 0.35) + (80 × 0.30) + (75 × 0.20) + (70 × 0.15)
                    = 30.8 + 24.0 + 15.0 + 10.5
                    = 80.3 → 80.0
```

### 3.5 Implementation Notes
- Update Frequency: Quarterly (based on new data)
- Range: 0.0 (unsustainable) to 100.0 (exemplary)
- Usage: Filter for sustainability-focused routes
- Review: Annual update based on category changes

---

## 4. Local Business Score (0-100)

### 4.1 Definition
Local Business Score measures the direct economic benefit to local Eskişehir communities, reflecting local ownership, employment, supply chains, and community engagement.

### 4.2 Base Scoring Formula

```
localBusinessScore = (ownershipLocality × 0.35) + (employmentQuality × 0.25) + (supplySelfSufficiency × 0.25) + (communityEngagement × 0.15)
```

### 4.3 Component Calculations

#### 4.3.1 Ownership Locality (0-100)

```
baseScore = 0

# Primary ownership
if tags.contains("local-family-business"):
    baseScore = 95
else if tags.contains("local-company"):
    baseScore = 85
else if tags.contains("local-cooperative"):
    baseScore = 90
else if tags.contains("municipal"):
    baseScore = 80
else if tags.contains("regional-company"):
    baseScore = 60
else if tags.contains("national-chain"):
    baseScore = 30
else if tags.contains("international"):
    baseScore = 10
else:
    baseScore = 50  # Unknown

ownershipLocality = baseScore
```

#### 4.3.2 Employment Quality (0-100)

```
baseScore = 50

# Employment count proxy (based on venue size & type)
if category in ["MARKET", "BAZAAR", "SHOPPING_CENTER"]:
    estimatedEmployees = 15  # Large venues
    baseScore = 75
else if category in ["RESTAURANT", "HOTEL", "CULTURAL_VENUE"]:
    estimatedEmployees = 8
    baseScore = 70
else if category in ["CAFE", "GALLERY", "WORKSHOP"]:
    estimatedEmployees = 3
    baseScore = 60
else if category in ["MUSEUM", "PARK"]:
    estimatedEmployees = 10
    baseScore = 75
else:
    estimatedEmployees = 2
    baseScore = 50

# Employment quality indicators
if tags.contains("supports-artisans"):
    baseScore += 15
if tags.contains("apprenticeship"):
    baseScore += 10
if tags.contains("fair-wages"):
    baseScore += 8

employmentQuality = min(100, baseScore)
```

#### 4.3.3 Supply Self-Sufficiency (0-100)

```
baseScore = 50

# Product/ingredient sourcing
if tags.contains("locally-sourced"):
    baseScore += 30
else if tags.contains("regional-sourcing"):
    baseScore += 15

# Product types
if category in ["BAKERY", "RESTAURANT", "MARKET", "CAFE"]:
    # Food service likely sources locally
    baseScore += 20
else if category in ["CRAFT_WORKSHOP", "LOCAL_BUSINESS"]:
    baseScore += 15

# Traditional production
if tags.contains("traditional-craft"):
    baseScore += 10
if tags.contains("heritage-recipe"):
    baseScore += 8

supplySelfSufficiency = min(100, baseScore)
```

#### 4.3.4 Community Engagement (0-100)

```
baseScore = 50

# Visible community involvement
if tags.contains("community-events"):
    baseScore += 15
if tags.contains("local-art-support"):
    baseScore += 10
if tags.contains("cultural-preservation"):
    baseScore += 12

# Contact accessibility
if phoneNumber and website and email:
    baseScore += 8
if socialMedia:
    baseScore += 5

communityEngagement = min(100, baseScore)
```

### 4.4 Examples

#### Example 1: Family Craft Workshop
```
Workshop: Local family business, traditional crafts, apprenticeships

ownershipLocality:
  - local-family-business tag: 95

employmentQuality:
  - workshop category: 60
  - apprenticeship: +10
  - traditional-craft: +8 (implied quality)
  - = 78

supplySelfSufficiency:
  - craft-workshop: +15
  - traditional-craft: +10
  - locally-sourced: +30
  - = 50 + 15 + 10 + 30 = 95

communityEngagement:
  - cultural-preservation: +12
  - phone + website + email: +8
  - = 50 + 12 + 8 = 70

localBusinessScore = (95 × 0.35) + (78 × 0.25) + (95 × 0.25) + (70 × 0.15)
                   = 33.25 + 19.5 + 23.75 + 10.5
                   = 87.0
```

#### Example 2: International Hotel Chain
```
Hotel: International brand, employees, but no local community focus

ownershipLocality:
  - international tag: 10

employmentQuality:
  - hotel category: 70
  - no special employment: 0
  - = 70

supplySelfSufficiency:
  - hotel base: 50
  - regional-sourcing (typical): +15
  - = 65

communityEngagement:
  - no community tags: 50
  - website: +5
  - = 55

localBusinessScore = (10 × 0.35) + (70 × 0.25) + (65 × 0.25) + (55 × 0.15)
                   = 3.5 + 17.5 + 16.25 + 8.25
                   = 45.5
```

#### Example 3: Popular Local Restaurant
```
Restaurant: Local family business, traditional recipes, community events

ownershipLocality:
  - local-family-business: 95

employmentQuality:
  - restaurant: 70
  - fair-wages (inferred): +8
  - = 78

supplySelfSufficiency:
  - restaurant base: 50
  - locally-sourced: +30
  - heritage-recipe: +8
  - = 88

communityEngagement:
  - community-events: +15
  - phone + website + email: +8
  - social media: +5
  - = 50 + 15 + 8 + 5 = 78

localBusinessScore = (95 × 0.35) + (78 × 0.25) + (88 × 0.25) + (78 × 0.15)
                   = 33.25 + 19.5 + 22.0 + 11.7
                   = 86.45 → 86.0
```

### 4.5 Implementation Notes
- Default: 0.0 for international chains with no local community benefit
- Maximum: 95.0 (highest local community contribution)
- Update Frequency: Semi-annual (based on business changes)
- Consideration: Family businesses get higher ratings by design
- Ethics: Weighted toward supporting local economy

---

## 5. Implementation Algorithm

### 5.1 Kotlin Score Calculator

```kotlin
class POIScoreCalculator {
    
    fun calculatePopularityScore(
        category: String,
        averageRating: Float,
        reviewCount: Int,
        district: String,
        season: String
    ): Float {
        val categoryWeight = getCategoryWeight(category)
        val ratingInfluence = calculateRatingInfluence(averageRating, reviewCount)
        val reviewCountInfluence = calculateReviewInfluence(reviewCount)
        val seasonalityFactor = getSeasonalityFactor(season)
        
        return (categoryWeight * 0.3f) +
               (ratingInfluence * 0.2f) +
               (reviewCountInfluence * 0.3f) +
               (seasonalityFactor * 0.2f)
    }
    
    fun calculateCrowdProxyScore(
        category: String,
        estimatedDuration: Int,
        currentHour: Int,
        dayOfWeek: String,
        season: String,
        indoorOutdoor: String
    ): Float {
        val categoryBaseline = getCategoryBaseline(category)
        val timeOfDayFactor = getTimeOfDayFactor(currentHour)
        val dayOfWeekFactor = getDayOfWeekFactor(dayOfWeek)
        val capacityFactor = getCapacityFactor(estimatedDuration, indoorOutdoor)
        val seasonalFactor = getSeasonalFactor(season)
        
        val score = (categoryBaseline * 0.25f) +
                    (timeOfDayFactor * 0.25f) +
                    (dayOfWeekFactor * 0.20f) +
                    (capacityFactor * 0.15f) +
                    (seasonalFactor * 0.15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    fun calculateSustainabilityScore(
        category: String,
        indoorOutdoor: String,
        tags: List<String>,
        wheelchairAccessible: Boolean,
        familyFriendly: Boolean,
        publicTransitAccess: Boolean,
        estimatedDuration: Int
    ): Float {
        val environmentalImpact = calculateEnvironmentalImpact(indoorOutdoor, tags, estimatedDuration)
        val localBenefit = calculateLocalCommunityBenefit(category, tags, estimatedDuration)
        val culturalPreservation = calculateCulturalPreservation(category, tags)
        val accessibility = calculateAccessibilityScore(
            wheelchairAccessible, familyFriendly, 
            publicTransitAccess
        )
        
        return (environmentalImpact * 0.35f) +
               (localBenefit * 0.30f) +
               (culturalPreservation * 0.20f) +
               (accessibility * 0.15f)
    }
    
    fun calculateLocalBusinessScore(
        category: String,
        tags: List<String>,
        estimatedDuration: Int
    ): Float {
        val ownershipLocality = calculateOwnershipLocality(tags)
        val employmentQuality = calculateEmploymentQuality(category, tags)
        val supplySelfSufficiency = calculateSupplySelfSufficiency(category, tags)
        val communityEngagement = calculateCommunityEngagement(tags)
        
        return (ownershipLocality * 0.35f) +
               (employmentQuality * 0.25f) +
               (supplySelfSufficiency * 0.25f) +
               (communityEngagement * 0.15f)
    }
    
    // Helper methods...
    private fun getCategoryWeight(category: String): Float = mapOf(
        "MUSEUM" to 92f,
        "HISTORICAL_SITE" to 95f,
        "MOSQUE" to 88f,
        // ... more categories
    )[category] ?: 45f
}
```

### 5.2 Validation Rules

```kotlin
class ScoreValidator {
    fun validateScores(poi: POI): List<String> {
        val errors = mutableListOf<String>()
        
        if (poi.popularityScore !in 0f..100f) {
            errors.add("popularityScore must be 0-100, got ${poi.popularityScore}")
        }
        if (poi.crowdProxyScore !in 0f..100f) {
            errors.add("crowdProxyScore must be 0-100, got ${poi.crowdProxyScore}")
        }
        if (poi.sustainabilityScore !in 0f..100f) {
            errors.add("sustainabilityScore must be 0-100, got ${poi.sustainabilityScore}")
        }
        if (poi.localBusinessScore !in 0f..100f) {
            errors.add("localBusinessScore must be 0-100, got ${poi.localBusinessScore}")
        }
        
        return errors
    }
}
```

---

## 6. Statistical Validation

### 6.1 Score Distribution Targets

Expected score distributions for realistic dataset:

```
Popularity Score Distribution:
- 0-25: 15% (lesser-known spots)
- 25-50: 25% (locally known)
- 50-75: 40% (popular)
- 75-100: 20% (major attractions)

Crowd Proxy Distribution:
- 0-20: 10% (very quiet)
- 20-40: 20% (quiet)
- 40-60: 30% (moderate)
- 60-80: 25% (busy)
- 80-100: 15% (very busy)

Sustainability Score Distribution:
- 0-33: 20% (low sustainability)
- 33-66: 50% (moderate sustainability)
- 66-100: 30% (high sustainability)

Local Business Score Distribution:
- 0-30: 25% (external/corporate)
- 30-60: 40% (mixed)
- 60-100: 35% (strong local presence)
```

### 6.2 Data Quality Checks

```kotlin
fun validateDataDistribution(pois: List<POI>) {
    val avgPopularity = pois.map { it.popularityScore }.average()
    val avgCrowdProxy = pois.map { it.crowdProxyScore }.average()
    val avgSustainability = pois.map { it.sustainabilityScore }.average()
    val avgLocalBusiness = pois.map { it.localBusinessScore }.average()
    
    require(avgPopularity in 45f..65f) { "Popularity avg ${avgPopularity} out of range" }
    require(avgCrowdProxy in 40f..60f) { "Crowd proxy avg ${avgCrowdProxy} out of range" }
    require(avgSustainability in 50f..70f) { "Sustainability avg ${avgSustainability} out of range" }
    require(avgLocalBusiness in 45f..70f) { "Local business avg ${avgLocalBusiness} out of range" }
}
```

---

## 7. Update & Maintenance

### 7.1 Score Update Schedule

- **Daily**: Crowd Proxy Score (real-time calculation)
- **Weekly**: Popularity Score (based on new reviews)
- **Quarterly**: Sustainability Score, Local Business Score
- **On-demand**: When POI information changes

### 7.2 Monitoring & Alerts

```kotlin
class ScoreMonitor {
    fun checkScoreAnomalies(poi: POI) {
        if (poi.popularityScore > 90 && poi.reviewCount < 10) {
            alert("High popularity with few reviews: ${poi.name}")
        }
        if (poi.sustainabilityScore > 85 && !poi.wheelchairAccessible) {
            alert("High sustainability without accessibility: ${poi.name}")
        }
        if (poi.localBusinessScore > 80 && poi.tags.contains("national-chain")) {
            alert("High local score for national chain: ${poi.name}")
        }
    }
}
```

---

## 8. References

- **Statistical Methods**: Logarithmic scaling, weighted averages
- **Tourism**: National tourism board benchmarks
- **Sustainability**: UN Sustainable Development Goals alignment
- **Community**: Local business association guidelines

---

**Status**: Complete ✅  
**Version**: 1.0  
**Last Updated**: May 6, 2026  
**Phase**: 13 (Proxy Scoring)  

