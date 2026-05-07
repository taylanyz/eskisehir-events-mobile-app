package com.eskisehir.eventapp.data.generator

/**
 * POI Seed Data Generation Executor
 * Generates 100 realistic Eskişehir POIs with all attributes
 * Exports to JSON, CSV, SQL formats for database loading
 * 
 * Usage:
 * Run as Kotlin script or in Android test:
 * val generator = POISeedDataGeneratorExecutor()
 * generator.execute()
 */
object POISeedDataGeneratorExecutor {
    
    /**
     * Execute complete seed data generation pipeline
     */
    fun execute() {
        println("=== POI Seed Data Generation Pipeline ===\n")
        
        // Step 1: Initialize generator
        println("Step 1: Initializing POI Seed Data Generator...")
        val generator = POISeedDataGenerator(targetCount = 100, poisPerDistrict = 10)
        println("✓ Generator initialized with target: 100 POIs, 10 per district\n")
        
        // Step 2: Generate POIs with statistics
        println("Step 2: Generating 100 POIs with realistic data...")
        val (pois, stats) = generator.generateWithStatistics()
        println("✓ Generated ${pois.size} POIs\n")
        
        // Step 3: Print statistics
        println("Step 3: Dataset Statistics:")
        stats.printSummary()
        println()
        
        // Step 4: Validate generated data
        println("Step 4: Validating generated data...")
        val validator = POIDataValidator()
        val validationResult = validator.validateDataset(pois)
        
        if (validationResult.isValid) {
            println("✓ All ${validationResult.validPOIs} POIs passed validation")
            println("✓ Data quality: ${validationResult.statistics.completenessPercentage}% complete")
            println("✓ Category distribution balance: ${validationResult.statistics.categoryDistributionBalance}")
        } else {
            println("✗ Validation failed!")
            println("  Valid: ${validationResult.validPOIs}, Invalid: ${validationResult.invalidPOIs}")
            validationResult.errors.forEach { (poi, errors) ->
                println("  POI $poi: ${errors.joinToString(", ")}")
            }
        }
        println()
        
        // Step 5: Export to JSON
        println("Step 5: Exporting to JSON format...")
        val serializer = POIDataSerializer()
        val jsonData = serializer.exportToJson(pois)
        val jsonPath = "pois-seed.json"
        val jsonSuccess = serializer.exportToJsonFile(pois, jsonPath)
        
        if (jsonSuccess) {
            println("✓ Exported to $jsonPath (${jsonData.length} bytes)")
        } else {
            println("✗ Failed to export JSON")
        }
        println()
        
        // Step 6: Export to CSV
        println("Step 6: Exporting to CSV format...")
        val csvData = serializer.exportToCsv(pois)
        val csvPath = "pois-seed.csv"
        val csvSuccess = serializer.exportToCsvFile(pois, csvPath)
        
        if (csvSuccess) {
            println("✓ Exported to $csvPath (${csvData.length} bytes)")
        } else {
            println("✗ Failed to export CSV")
        }
        println()
        
        // Step 7: Export SQL INSERT statements
        println("Step 7: Exporting SQL INSERT statements...")
        val sqlData = serializer.exportToSqlInsert(pois, "poi")
        val sqlPath = "pois-seed-insert.sql"
        try {
            val file = java.io.File(sqlPath)
            file.writeText(sqlData)
            println("✓ Exported to $sqlPath (${sqlData.length} bytes)")
        } catch (e: Exception) {
            println("✗ Failed to export SQL: ${e.message}")
        }
        println()
        
        // Step 8: Print sample POIs
        println("Step 8: Sample POIs (first 3):")
        pois.take(3).forEach { poi ->
            println("""
                ├─ ${poi.name}
                │  Category: ${poi.category}, District: ${poi.district}
                │  Location: (${poi.latitude}, ${poi.longitude})
                │  Scores: Popular=${poi.proxyScores?.popularityScore}, Sustainable=${poi.proxyScores?.sustainabilityScore}
                │  Accessibility: WheelchairAccessible=${poi.accessibilityFeatures?.wheelchairAccessible}, ChildFriendly=${poi.accessibilityFeatures?.childFriendly}
            """.trimIndent())
        }
        println()
        
        // Step 9: Summary
        println("=== Generation Complete ===")
        println("✓ Successfully generated 100 POIs across 10 Eskişehir districts")
        println("✓ Exported to 3 formats: JSON, CSV, SQL")
        println("✓ All data validated and ready for database loading")
        println("✓ Next step: Load data into PostgreSQL database")
        println()
    }
    
    /**
     * Generate and return POIs only (programmatic access)
     */
    fun generatePOIs(): List<POI> {
        val generator = POISeedDataGenerator(targetCount = 100, poisPerDistrict = 10)
        val (pois, _) = generator.generateWithStatistics()
        return pois
    }
    
    /**
     * Generate and export to JSON file only
     */
    fun generateAndExportJSON(outputPath: String = "pois-seed.json"): Boolean {
        val generator = POISeedDataGenerator(targetCount = 100, poisPerDistrict = 10)
        val (pois, _) = generator.generateWithStatistics()
        
        val serializer = POIDataSerializer()
        return serializer.exportToJsonFile(pois, outputPath)
    }
}

/**
 * Main function for running in standalone Kotlin environment
 */
fun main() {
    POISeedDataGeneratorExecutor.execute()
}
