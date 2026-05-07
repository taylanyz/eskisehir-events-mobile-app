package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.*
import kotlin.random.Random

class POISeedDataGenerator(
    val targetCount: Int = 100,
    val poisPerDistrict: Int = 10  // ~10 POIs per district, 10 districts = 100 total
) {
    
    // Category distribution percentages
    private val categoryDistribution = mapOf(
        POICategory.MUSEUM to 0.20f,
        POICategory.HISTORICAL_SITE to 0.15f,
        POICategory.MOSQUE to 0.10f,
        POICategory.PARK to 0.15f,
        POICategory.RESTAURANT to 0.10f,
        POICategory.CAFE to 0.10f,
        POICategory.SHOPPING_CENTER to 0.05f,
        POICategory.BAZAAR to 0.05f,
        POICategory.LIBRARY to 0.02f,
        POICategory.CINEMA to 0.02f,
        POICategory.LANDMARK to 0.05f,
        POICategory.SCENIC_VIEWPOINT to 0.01f
    )
    
    /**
     * Select a random category based on distribution weights
     */
    fun selectCategory(): POICategory {
        val random = Random.nextDouble()
        var cumulative = 0.0
        
        for ((category, weight) in categoryDistribution) {
            cumulative += weight
            if (random <= cumulative) {
                return category
            }
        }
        
        return POICategory.OTHER
    }
    
    /**
     * Generate a single POI
     */
    fun generatePOI(
        district: District,
        category: POICategory? = null,
        sequenceNumber: Int = 0
    ): POI {
        val selectedCategory = category ?: selectCategory()
        val (turkishName, englishName) = TurkishNameGenerator.generateName(selectedCategory)
        val (latitude, longitude) = LocationGenerator.generateRandomCoordinate(district)
        val address = AttributeGenerator.generateAddress(district)
        val tags = AttributeGenerator.generateTags(selectedCategory)
        val hours = AttributeGenerator.generateOperatingHours()
        val locationType = when (selectedCategory) {
            POICategory.PARK, POICategory.GARDEN, POICategory.ZOO,
            POICategory.SCENIC_VIEWPOINT -> LocationType.OUTDOOR
            POICategory.MUSEUM, POICategory.LIBRARY, POICategory.CINEMA,
            POICategory.SHOPPING_CENTER -> LocationType.INDOOR
            else -> LocationType.MIXED
        }
        
        val accessibility = AttributeGenerator.generateAccessibilityFeatures(
            category = selectedCategory,
            locationType = locationType
        )
        
        val (turkishDesc, englishDesc) = AttributeGenerator.generateDescription(
            category = selectedCategory,
            turkishName = turkishName,
            district = district
        )
        
        val estimatedDuration = AttributeGenerator.generateEstimatedDuration(selectedCategory)
        
        val priceLevel = when (selectedCategory) {
            POICategory.MUSEUM, POICategory.ZOO, POICategory.CINEMA -> PriceLevel.MODERATE
            POICategory.RESTAURANT -> PriceLevel.MODERATE
            POICategory.CAFE -> PriceLevel.BUDGET
            POICategory.PARK, POICategory.MOSQUE, POICategory.HISTORICAL_SITE,
            POICategory.BAZAAR, POICategory.TRADITIONAL_MARKET, POICategory.LANDMARK -> PriceLevel.FREE
            else -> PriceLevel.FREE
        }
        
        val estimatedCost = when (priceLevel) {
            PriceLevel.FREE -> null
            PriceLevel.BUDGET -> Random.nextFloat() * 50
            PriceLevel.MODERATE -> 50 + Random.nextFloat() * 100
            PriceLevel.EXPENSIVE -> 150 + Random.nextFloat() * 150
            PriceLevel.LUXURY -> 300 + Random.nextFloat() * 200
        }
        
        // Create POI without scores first
        val poiWithoutScores = POI(
            name = turkishName,
            englishName = englishName,
            category = selectedCategory,
            district = district,
            latitude = latitude,
            longitude = longitude,
            address = address,
            description = turkishDesc,
            englishDescription = englishDesc,
            operatingHours = hours,
            priceLevel = priceLevel,
            estimatedCost = estimatedCost,
            estimatedVisitDuration = estimatedDuration,
            tags = tags,
            locationType = locationType,
            accessibility = accessibility
        )
        
        // Calculate scores
        val scores = POIScoreCalculator.calculateAllScores(selectedCategory, poiWithoutScores)
        
        // Return complete POI with scores
        return poiWithoutScores.copy(scores = scores)
    }
    
    /**
     * Generate complete seed dataset with POIs distributed across districts
     */
    fun generateSeedDataset(): List<POI> {
        val allPOIs = mutableListOf<POI>()
        val districts = listOf(
            District.ODUNPAZARI,
            District.SAZOVA,
            District.YUNUSELI,
            District.ESKISEHIR_CENTER,
            District.TEPEBASΙ,
            District.ALPASLAN,
            District.HOŞNUDIYE,
            District.BAHÇELIEVLER,
            District.MIHALICILAR,
            District.SITELER
        )
        
        var sequenceNumber = 0
        for (district in districts) {
            for (i in 0 until poisPerDistrict) {
                val poi = generatePOI(
                    district = district,
                    sequenceNumber = sequenceNumber++
                )
                allPOIs.add(poi)
            }
        }
        
        return allPOIs.take(targetCount)  // Ensure exact count
    }
    
    /**
     * Generate dataset with statistics
     */
    fun generateWithStatistics(): Pair<List<POI>, DatasetStatistics> {
        val pois = generateSeedDataset()
        val stats = calculateStatistics(pois)
        return Pair(pois, stats)
    }
    
    /**
     * Calculate dataset statistics for validation
     */
    private fun calculateStatistics(pois: List<POI>): DatasetStatistics {
        val categoryDistribution = pois.groupingBy { it.category }.eachCount()
        val districtDistribution = pois.groupingBy { it.district }.eachCount()
        
        val avgPopularityScore = pois.map { it.scores.popularityScore }.average().toFloat()
        val avgCrowdScore = pois.map { it.scores.crowdProxyScore }.average().toFloat()
        val avgSustainability = pois.map { it.scores.sustainabilityScore }.average().toFloat()
        val avgLocalBusiness = pois.map { it.scores.localBusinessScore }.average().toFloat()
        
        val priceLevelDist = pois.groupingBy { it.priceLevel }.eachCount()
        val accessibleCount = pois.count { it.accessibility.wheelchairAccessible }
        val childFriendlyCount = pois.count { it.accessibility.childFriendly }
        
        return DatasetStatistics(
            totalPOIs = pois.size,
            categoryDistribution = categoryDistribution,
            districtDistribution = districtDistribution,
            averagePopularityScore = avgPopularityScore,
            averageCrowdScore = avgCrowdScore,
            averageSustainabilityScore = avgSustainability,
            averageLocalBusinessScore = avgLocalBusiness,
            priceLevelDistribution = priceLevelDist,
            wheelchairAccessibleCount = accessibleCount,
            childFriendlyCount = childFriendlyCount
        )
    }
}

/**
 * Data class to hold dataset statistics
 */
data class DatasetStatistics(
    val totalPOIs: Int,
    val categoryDistribution: Map<POICategory, Int>,
    val districtDistribution: Map<District, Int>,
    val averagePopularityScore: Float,
    val averageCrowdScore: Float,
    val averageSustainabilityScore: Float,
    val averageLocalBusinessScore: Float,
    val priceLevelDistribution: Map<PriceLevel, Int>,
    val wheelchairAccessibleCount: Int,
    val childFriendlyCount: Int
) {
    fun printSummary() {
        println("=== POI Dataset Statistics ===")
        println("Total POIs: $totalPOIs")
        println("\nAverage Scores:")
        println("  - Popularity: ${"%.2f".format(averagePopularityScore)}")
        println("  - Crowd: ${"%.2f".format(averageCrowdScore)}")
        println("  - Sustainability: ${"%.2f".format(averageSustainabilityScore)}")
        println("  - Local Business: ${"%.2f".format(averageLocalBusinessScore)}")
        println("\nCategory Distribution:")
        categoryDistribution.forEach { (cat, count) ->
            println("  - $cat: $count")
        }
        println("\nDistrict Distribution:")
        districtDistribution.forEach { (dist, count) ->
            println("  - $dist: $count")
        }
        println("\nAccessibility:")
        println("  - Wheelchair Accessible: $wheelchairAccessibleCount")
        println("  - Child Friendly: $childFriendlyCount")
    }
}

/**
 * Usage Example:
 * 
 * val generator = POISeedDataGenerator(targetCount = 100, poisPerDistrict = 10)
 * val (pois, stats) = generator.generateWithStatistics()
 * stats.printSummary()
 * 
 * // Export to different formats
 * val poiList = generator.generateSeedDataset()
 * println("Generated ${poiList.size} POIs")
 */
