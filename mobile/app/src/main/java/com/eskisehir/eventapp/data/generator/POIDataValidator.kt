package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.POI
import com.eskisehir.eventapp.data.model.PriceLevel

object POIDataValidator {
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>,
        val warnings: List<String>
    )
    
    data class DatasetValidationResult(
        val isValid: Boolean,
        val totalPOIs: Int,
        val validPOIs: Int,
        val invalidPOIs: Int,
        val errors: Map<String, List<String>>,
        val warnings: Map<String, List<String>>,
        val statistics: DatasetQualityStats
    )
    
    data class DatasetQualityStats(
        val completenessPercentage: Float,
        val categoryDistributionBalance: Float,
        val districtDistributionBalance: Float,
        val scoreDistributionBalance: Float,
        val duplicateCount: Int
    )
    
    /**
     * Validate a single POI
     */
    fun validatePOI(poi: POI): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Mandatory field validation
        if (poi.id.isBlank()) errors.add("ID is empty")
        if (poi.name.isBlank()) errors.add("Turkish name is empty")
        if (poi.address.isBlank()) errors.add("Address is empty")
        
        // Geographic validation
        if (poi.latitude !in -90.0..90.0) errors.add("Invalid latitude: ${poi.latitude}")
        if (poi.longitude !in -180.0..180.0) errors.add("Invalid longitude: ${poi.longitude}")
        
        // Precision validation (6 decimals)
        val latParts = poi.latitude.toString().split(".")
        if (latParts.size == 2 && latParts[1].length > 6) {
            warnings.add("Latitude exceeds 6 decimal precision")
        }
        
        // Score validation
        if (poi.scores.popularityScore !in 0f..100f) {
            errors.add("Popularity score out of range: ${poi.scores.popularityScore}")
        }
        if (poi.scores.crowdProxyScore !in 0f..100f) {
            errors.add("Crowd score out of range: ${poi.scores.crowdProxyScore}")
        }
        if (poi.scores.sustainabilityScore !in 0f..100f) {
            errors.add("Sustainability score out of range: ${poi.scores.sustainabilityScore}")
        }
        if (poi.scores.localBusinessScore !in 0f..100f) {
            errors.add("Local business score out of range: ${poi.scores.localBusinessScore}")
        }
        
        // Optional field validation
        if (poi.englishName != null && poi.englishName.isBlank()) {
            warnings.add("English name is provided but empty")
        }
        
        if (poi.estimatedCost != null && poi.estimatedCost < 0) {
            errors.add("Estimated cost cannot be negative: ${poi.estimatedCost}")
        }
        
        if (poi.estimatedVisitDuration != null && poi.estimatedVisitDuration!! <= 0) {
            errors.add("Estimated visit duration must be positive: ${poi.estimatedVisitDuration}")
        }
        
        // Tag validation
        if (poi.tags.isEmpty()) {
            warnings.add("No tags provided")
        }
        
        // Price level validation
        if (poi.priceLevel == PriceLevel.FREE && poi.estimatedCost != null && poi.estimatedCost!! > 0) {
            warnings.add("Price level is FREE but estimated cost is ${poi.estimatedCost}")
        }
        
        // Accessibility validation
        if (poi.accessibility.wheelchairAccessible && poi.accessibility.parkingAvailable == false) {
            warnings.add("Wheelchair accessible but no parking mentioned")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Validate entire dataset
     */
    fun validateDataset(pois: List<POI>): DatasetValidationResult {
        val errors = mutableMapOf<String, MutableList<String>>()
        val warnings = mutableMapOf<String, MutableList<String>>()
        var validCount = 0
        
        // Validate each POI
        pois.forEach { poi ->
            val result = validatePOI(poi)
            if (result.isValid) {
                validCount++
            } else {
                errors[poi.id] = result.errors.toMutableList()
            }
            if (result.warnings.isNotEmpty()) {
                warnings[poi.id] = result.warnings.toMutableList()
            }
        }
        
        // Dataset-level validation
        val duplicateIds = pois.groupingBy { it.id }.eachCount().filter { it.value > 1 }
        if (duplicateIds.isNotEmpty()) {
            errors["dataset"] = mutableListOf("Duplicate IDs found: $duplicateIds")
        }
        
        // Distribution validation
        val categoryDist = pois.groupingBy { it.category }.eachCount()
        val districtDist = pois.groupingBy { it.district }.eachCount()
        
        if (categoryDist.size < 5) {
            warnings["dataset"] = (warnings["dataset"] ?: mutableListOf()) as MutableList
            warnings["dataset"]!!.add("Limited category diversity: only ${categoryDist.size} categories")
        }
        
        // Calculate statistics
        val stats = calculateQualityStats(pois, validCount, duplicateIds.size)
        
        val isValid = errors.isEmpty() && validCount == pois.size
        
        return DatasetValidationResult(
            isValid = isValid,
            totalPOIs = pois.size,
            validPOIs = validCount,
            invalidPOIs = pois.size - validCount,
            errors = errors,
            warnings = warnings,
            statistics = stats
        )
    }
    
    /**
     * Calculate data quality statistics
     */
    private fun calculateQualityStats(
        pois: List<POI>,
        validCount: Int,
        duplicateCount: Int
    ): DatasetQualityStats {
        // Completeness: check for missing optional fields
        var completenessSum = 0f
        pois.forEach { poi ->
            var fieldCount = 5f  // mandatory fields (id, name, category, district, address)
            var totalFields = 5f
            
            if (poi.englishName != null && poi.englishName.isNotBlank()) fieldCount++
            if (poi.description != null && poi.description.isNotBlank()) fieldCount++
            if (poi.tags.isNotEmpty()) fieldCount++
            if (poi.estimatedCost != null) fieldCount++
            if (poi.estimatedVisitDuration != null) fieldCount++
            
            totalFields += 5  // optional fields
            completenessSum += (fieldCount / totalFields)
        }
        val completeness = (completenessSum / pois.size) * 100
        
        // Category distribution balance (ideal: all categories represented equally)
        val categoryDist = pois.groupingBy { it.category }.eachCount()
        val avgCategoryCount = pois.size.toFloat() / categoryDist.size
        val categoryVariance = categoryDist.values.map { (it - avgCategoryCount) * (it - avgCategoryCount) }
            .average()
        val categoryBalance = 100f - (Math.sqrt(categoryVariance) / avgCategoryCount * 50).toFloat().coerceIn(0f, 100f)
        
        // District distribution balance
        val districtDist = pois.groupingBy { it.district }.eachCount()
        val avgDistrictCount = pois.size.toFloat() / districtDist.size
        val districtVariance = districtDist.values.map { (it - avgDistrictCount) * (it - avgDistrictCount) }
            .average()
        val districtBalance = 100f - (Math.sqrt(districtVariance) / avgDistrictCount * 50).toFloat().coerceIn(0f, 100f)
        
        // Score distribution balance (should be diverse, not all same)
        val popScores = pois.map { it.scores.popularityScore }
        val scoreVariance = popScores.map { (it - popScores.average()) * (it - popScores.average()) }
            .average()
        val scoreBalance = Math.sqrt(scoreVariance).toFloat()
        
        return DatasetQualityStats(
            completenessPercentage = completeness.coerceIn(0f, 100f),
            categoryDistributionBalance = categoryBalance,
            districtDistributionBalance = districtBalance,
            scoreDistributionBalance = scoreBalance.coerceIn(0f, 100f),
            duplicateCount = duplicateCount
        )
    }
    
    /**
     * Print validation report
     */
    fun printValidationReport(result: DatasetValidationResult) {
        println("=== POI Dataset Validation Report ===")
        println("Status: ${if (result.isValid) "✓ VALID" else "✗ INVALID"}")
        println("Total POIs: ${result.totalPOIs}")
        println("Valid POIs: ${result.validPOIs}")
        println("Invalid POIs: ${result.invalidPOIs}")
        
        if (result.errors.isNotEmpty()) {
            println("\nErrors (${result.errors.size}):")
            result.errors.forEach { (id, errorList) ->
                println("  $id:")
                errorList.forEach { println("    - $it") }
            }
        }
        
        if (result.warnings.isNotEmpty()) {
            println("\nWarnings (${result.warnings.size}):")
            result.warnings.forEach { (id, warningList) ->
                warningList.forEach { println("  - $it") }
            }
        }
        
        println("\nData Quality Statistics:")
        with(result.statistics) {
            println("  - Completeness: ${"%.1f".format(completenessPercentage)}%")
            println("  - Category Distribution Balance: ${"%.1f".format(categoryDistributionBalance)}%")
            println("  - District Distribution Balance: ${"%.1f".format(districtDistributionBalance)}%")
            println("  - Score Distribution Balance: ${"%.1f".format(scoreDistributionBalance)}%")
            println("  - Duplicate Count: $duplicateCount")
        }
    }
}

/**
 * Usage Example:
 * 
 * val pois = POISeedDataGenerator().generateSeedDataset()
 * val validationResult = POIDataValidator.validateDataset(pois)
 * POIDataValidator.printValidationReport(validationResult)
 * 
 * if (validationResult.isValid) {
 *     println("✓ Dataset is valid and ready for use!")
 * } else {
 *     println("✗ Dataset has ${validationResult.errors.size} errors")
 * }
 */
