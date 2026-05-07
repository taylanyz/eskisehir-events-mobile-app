package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.*
import java.util.*
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

object POIScoreCalculator {
    
    // Category baseline popularity scores (0-100)
    private val categoryPopularityBaseline = mapOf(
        POICategory.MUSEUM to 92f,
        POICategory.HISTORICAL_SITE to 95f,
        POICategory.MOSQUE to 85f,
        POICategory.PARK to 75f,
        POICategory.RESTAURANT to 70f,
        POICategory.CAFE to 55f,
        POICategory.SHOPPING_CENTER to 65f,
        POICategory.BAZAAR to 80f,
        POICategory.LIBRARY to 60f,
        POICategory.CINEMA to 65f,
        POICategory.ZOO to 90f,
        POICategory.LANDMARK to 88f,
        POICategory.SCENIC_VIEWPOINT to 82f,
        POICategory.TRADITIONAL_MARKET to 78f
    )
    
    // Category baseline crowd scores
    private val categoryCrowdBaseline = mapOf(
        POICategory.MUSEUM to 75f,
        POICategory.HISTORICAL_SITE to 65f,
        POICategory.MOSQUE to 35f,
        POICategory.PARK to 70f,
        POICategory.RESTAURANT to 72f,
        POICategory.CAFE to 60f,
        POICategory.SHOPPING_CENTER to 80f,
        POICategory.BAZAAR to 80f,
        POICategory.LIBRARY to 25f,
        POICategory.CINEMA to 85f,
        POICategory.ZOO to 78f,
        POICategory.LANDMARK to 60f,
        POICategory.SCENIC_VIEWPOINT to 50f,
        POICategory.TRADITIONAL_MARKET to 78f
    )
    
    // Sustainability scoring components
    private val greenSpaceBonus = 20f
    private val mixedLocationBonus = 10f
    private val ecoTagsBonus = 15f
    
    // Sustainability category baseline
    private val categorySustainabilityBaseline = mapOf(
        POICategory.MUSEUM to 75f,
        POICategory.HISTORICAL_SITE to 80f,
        POICategory.PARK to 95f,
        POICategory.LIBRARY to 70f,
        POICategory.BAZAAR to 65f,
        POICategory.TRADITIONAL_MARKET to 68f,
        POICategory.GARDEN to 98f,
        POICategory.NATURE_RESERVE to 100f
    )
    
    // Local business baseline
    private val categoryLocalBusinessBaseline = mapOf(
        POICategory.RESTAURANT to 85f,
        POICategory.CAFE to 75f,
        POICategory.BAZAAR to 90f,
        POICategory.TRADITIONAL_MARKET to 95f,
        POICategory.BAKERY to 92f,
        POICategory.ANTIQUE_SHOP to 88f,
        POICategory.SHOPPING_CENTER to 30f
    )
    
    /**
     * Calculate Popularity Score
     * Formula: (categoryWeight × 0.3) + (ratingInfluence × 0.2) + (reviewCountInfluence × 0.3) + (seasonalityFactor × 0.2)
     * Range: 0-100
     */
    fun calculatePopularityScore(
        category: POICategory,
        reviewCount: Int = Random.nextInt(0, 500),
        rating: Float = Random.nextFloat() * 5f,
        isSeasonal: Boolean = false
    ): Float {
        val categoryWeight = categoryPopularityBaseline[category] ?: 50f
        
        // Rating influence (0-100, normalized from 0-5 stars)
        val ratingInfluence = (rating / 5f) * 100f
        
        // Review count influence with logarithmic scaling
        val reviewCountInfluence = if (reviewCount > 0) {
            ln((reviewCount + 1).toFloat()) / ln(1000f) * 100f
        } else {
            0f
        }
        
        // Seasonality factor (tourism season: summer 1.25, winter 0.85)
        val seasonalityFactor = if (isSeasonal) 1.15f else 0.95f
        
        val score = (categoryWeight * 0.3f) +
                   (ratingInfluence * 0.2f) +
                   (reviewCountInfluence * 0.3f) +
                   (seasonalityFactor * 0.2f)
        
        return score.coerceIn(0f, 100f)
    }
    
    /**
     * Calculate Crowd Proxy Score
     * Formula: (categoryBaseline × 0.25) + (timeOfDay × 0.25) + (dayOfWeek × 0.20) + (capacity × 0.15) + (seasonality × 0.15)
     * Range: 0-100
     */
    fun calculateCrowdProxyScore(
        category: POICategory,
        hourOfDay: Int = Random.nextInt(0, 24),
        dayOfWeek: Int = Random.nextInt(0, 7),  // 0=Monday, 6=Sunday
        estimatedCapacity: Float = 1f,  // 0.3-1.0 scale
        isSummerSeason: Boolean = false
    ): Float {
        val categoryBaseline = categoryCrowdBaseline[category] ?: 50f
        
        // Time of day factor (peak hours 11:00-18:00)
        val timeOfDayFactor = when {
            hourOfDay in 6..10 -> 0.4f    // Early morning
            hourOfDay in 11..14 -> 0.9f   // Lunch peak
            hourOfDay in 15..18 -> 1.0f   // Afternoon peak
            hourOfDay in 19..21 -> 0.7f   // Evening
            else -> 0.1f                  // Very early/late
        }
        
        // Day of week factor
        val dayOfWeekFactor = when (dayOfWeek) {
            0, 1, 2, 3 -> 0.85f   // Mon-Thu (quieter)
            4 -> 0.95f             // Friday
            5 -> 1.35f             // Saturday (busy)
            6 -> 1.25f             // Sunday
            else -> 1.0f
        }
        
        // Capacity modifier (smaller venues = higher crowd density perception)
        val capacityFactor = 0.3f + (estimatedCapacity * 0.7f)
        
        // Seasonal tourism factor
        val seasonalityFactor = if (isSummerSeason) 1.25f else 0.85f
        
        val score = (categoryBaseline * 0.25f) +
                   (timeOfDayFactor * 100f * 0.25f) +
                   (dayOfWeekFactor * 100f * 0.20f) +
                   (capacityFactor * 100f * 0.15f) +
                   (seasonalityFactor * 100f * 0.15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    /**
     * Calculate Sustainability Score
     * Formula: (environmental × 0.35) + (localBenefit × 0.30) + (cultural × 0.20) + (accessibility × 0.15)
     * Range: 0-100
     */
    fun calculateSustainabilityScore(
        category: POICategory,
        locationType: LocationType,
        hasEcoTags: Boolean = false,
        isLocalOwned: Boolean = false,
        hasAccessibility: Boolean = false
    ): Float {
        // Environmental component
        val environmentalScore = when (locationType) {
            LocationType.OUTDOOR -> 20f
            LocationType.MIXED -> 10f
            LocationType.INDOOR -> 0f
        } + (if (hasEcoTags) ecoTagsBonus else 0f)
        
        // Local benefit component
        val localBenefitScore = if (isLocalOwned) 30f else 15f + 
                               categorySustainabilityBaseline[category]?.div(3f) ?: 5f
        
        // Cultural preservation component (higher for historical/cultural)
        val culturalScore = when (category) {
            POICategory.MUSEUM -> 85f
            POICategory.HISTORICAL_SITE -> 80f
            POICategory.MOSQUE -> 75f
            POICategory.TRADITIONAL_MARKET -> 70f
            POICategory.BAZAAR -> 70f
            POICategory.PARK -> 60f
            else -> 40f
        }
        
        // Accessibility component
        val accessibilityScore = if (hasAccessibility) 20f else 5f
        
        val score = (environmentalScore * 0.35f) +
                   (localBenefitScore * 0.30f) +
                   (culturalScore * 0.20f) +
                   (accessibilityScore * 0.15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    /**
     * Calculate Local Business Score
     * Formula: (ownership × 0.35) + (employment × 0.25) + (supply × 0.25) + (engagement × 0.15)
     * Range: 0-100
     */
    fun calculateLocalBusinessScore(
        category: POICategory,
        isLocallyOwned: Boolean = true,
        isCooperative: Boolean = false,
        employeeCount: Int = Random.nextInt(2, 50),
        hasLocalSupply: Boolean = true,
        hasCommunityEvents: Boolean = false
    ): Float {
        // Ownership component (family/local = high, international = low)
        val ownershipScore = when {
            isCooperative -> 90f
            isLocallyOwned -> 85f
            else -> 10f
        }
        
        // Employment component (more employees = higher local benefit)
        val employmentScore = when {
            employeeCount >= 20 -> 75f
            employeeCount >= 10 -> 65f
            employeeCount >= 5 -> 50f
            else -> 30f
        }
        
        // Supply chain component (locally-sourced materials)
        val supplyScore = if (hasLocalSupply) 30f else 10f
        
        // Community engagement component
        val engagementScore = if (hasCommunityEvents) 15f else 5f + 
                             categoryLocalBusinessBaseline[category]?.div(10f) ?: 5f
        
        val score = (ownershipScore * 0.35f) +
                   (employmentScore * 0.25f) +
                   (supplyScore * 0.25f) +
                   (engagementScore * 0.15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    /**
     * Calculate all proxy scores for a POI
     */
    fun calculateAllScores(category: POICategory, poi: POI): ProxyScores {
        val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) % 7
        val isSummer = Calendar.getInstance().get(Calendar.MONTH) in 5..8
        
        val popularityScore = calculatePopularityScore(
            category = category,
            reviewCount = Random.nextInt(0, 300),
            rating = 3.5f + Random.nextFloat() * 1.5f,
            isSeasonal = isSummer
        )
        
        val crowdScore = calculateCrowdProxyScore(
            category = category,
            hourOfDay = hourOfDay,
            dayOfWeek = dayOfWeek,
            estimatedCapacity = 0.6f + Random.nextFloat() * 0.4f,
            isSummerSeason = isSummer
        )
        
        val sustainability = calculateSustainabilityScore(
            category = category,
            locationType = poi.locationType,
            hasEcoTags = "eco" in poi.tags || "green" in poi.tags,
            isLocalOwned = true,
            hasAccessibility = poi.accessibility.wheelchairAccessible
        )
        
        val localBusiness = calculateLocalBusinessScore(
            category = category,
            isLocallyOwned = true,
            employeeCount = Random.nextInt(3, 30),
            hasLocalSupply = Random.nextBoolean(),
            hasCommunityEvents = Random.nextBoolean()
        )
        
        return ProxyScores(
            popularityScore = popularityScore,
            crowdProxyScore = crowdScore,
            sustainabilityScore = sustainability,
            localBusinessScore = localBusiness
        )
    }
}

/**
 * Usage Example:
 * 
 * val popScore = POIScoreCalculator.calculatePopularityScore(
 *     category = POICategory.MUSEUM,
 *     reviewCount = 250,
 *     rating = 4.5f
 * )
 * 
 * val crowdScore = POIScoreCalculator.calculateCrowdProxyScore(
 *     category = POICategory.MUSEUM,
 *     hourOfDay = 14,  // 2 PM peak
 *     dayOfWeek = 5    // Saturday
 * )
 */
