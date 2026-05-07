package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.POI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object POIDataSerializer {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    /**
     * Export POIs to JSON format
     */
    fun exportToJson(pois: List<POI>): String {
        return gson.toJson(pois)
    }
    
    /**
     * Export POIs to JSON file
     */
    fun exportToJsonFile(pois: List<POI>, filePath: String): Boolean {
        return try {
            val json = exportToJson(pois)
            File(filePath).writeText(json)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Export POIs to CSV format
     * Format: id, name, englishName, category, district, latitude, longitude, address, priceLevel, estimatedCost, tags
     */
    fun exportToCsv(pois: List<POI>): String {
        val csv = StringBuilder()
        
        // Header
        csv.append("id,name,englishName,category,district,latitude,longitude,address,")
        csv.append("operatingHours,priceLevel,estimatedCost,estimatedVisitDuration,tags,locationType,")
        csv.append("wheelchairAccessible,publicTransitAccess,childFriendly,petFriendly,")
        csv.append("popularityScore,crowdScore,sustainabilityScore,localBusinessScore\n")
        
        // Rows
        pois.forEach { poi ->
            val tagsStr = poi.tags.joinToString("|")
            csv.append("\"${poi.id}\",")
            csv.append("\"${poi.name}\",")
            csv.append("\"${poi.englishName ?: ""}\",")
            csv.append("\"${poi.category}\",")
            csv.append("\"${poi.district}\",")
            csv.append("${poi.latitude},")
            csv.append("${poi.longitude},")
            csv.append("\"${poi.address}\",")
            csv.append("\"${poi.operatingHours.monday ?: ""}\",")
            csv.append("\"${poi.priceLevel}\",")
            csv.append("${poi.estimatedCost ?: ""},")
            csv.append("${poi.estimatedVisitDuration ?: ""},")
            csv.append("\"$tagsStr\",")
            csv.append("\"${poi.locationType}\",")
            csv.append("${poi.accessibility.wheelchairAccessible},")
            csv.append("${poi.accessibility.publicTransitAccess},")
            csv.append("${poi.accessibility.childFriendly},")
            csv.append("${poi.accessibility.petFriendly},")
            csv.append("${poi.scores.popularityScore},")
            csv.append("${poi.scores.crowdProxyScore},")
            csv.append("${poi.scores.sustainabilityScore},")
            csv.append("${poi.scores.localBusinessScore}\n")
        }
        
        return csv.toString()
    }
    
    /**
     * Export POIs to CSV file
     */
    fun exportToCsvFile(pois: List<POI>, filePath: String): Boolean {
        return try {
            val csv = exportToCsv(pois)
            File(filePath).writeText(csv)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Export POIs as Kotlin data class for direct inclusion in source code
     */
    fun exportToKotlinDataClass(pois: List<POI>, varName: String = "samplePOIs"): String {
        val kotlin = StringBuilder()
        kotlin.append("// Auto-generated POI data\n")
        kotlin.append("val $varName = listOf<POI>(\n")
        
        pois.forEachIndexed { index, poi ->
            kotlin.append("    POI(\n")
            kotlin.append("        name = \"${poi.name}\",\n")
            kotlin.append("        englishName = \"${poi.englishName ?: ""}\",\n")
            kotlin.append("        category = POICategory.${poi.category},\n")
            kotlin.append("        district = District.${poi.district},\n")
            kotlin.append("        latitude = ${poi.latitude},\n")
            kotlin.append("        longitude = ${poi.longitude},\n")
            kotlin.append("        address = \"${poi.address}\",\n")
            kotlin.append("        description = \"${poi.description?.take(100) ?: ""}\",\n")
            kotlin.append("        tags = listOf(${poi.tags.joinToString(",") { "\"$it\"" }}),\n")
            kotlin.append("        locationType = LocationType.${poi.locationType},\n")
            kotlin.append("        priceLevel = PriceLevel.${poi.priceLevel}\n")
            kotlin.append("    )")
            
            if (index < pois.size - 1) kotlin.append(",")
            kotlin.append("\n")
        }
        
        kotlin.append(")\n")
        return kotlin.toString()
    }
    
    /**
     * Export POIs to SQL INSERT statements
     */
    fun exportToSqlInsert(pois: List<POI>, tableName: String = "poi"): String {
        val sql = StringBuilder()
        sql.append("-- Auto-generated SQL INSERT statements\n")
        sql.append("-- Table: $tableName\n\n")
        
        pois.forEach { poi ->
            sql.append("INSERT INTO $tableName (")
            sql.append("id, name, english_name, category, district, latitude, longitude, ")
            sql.append("address, description, price_level, estimated_cost, ")
            sql.append("estimated_visit_duration, tags, location_type, ")
            sql.append("wheelchair_accessible, popularity_score, crowd_score, ")
            sql.append("sustainability_score, local_business_score, created_at, updated_at")
            sql.append(") VALUES (\n")
            
            sql.append("'${poi.id}', ")
            sql.append("'${poi.name}', ")
            sql.append("'${poi.englishName ?: ""}', ")
            sql.append("'${poi.category}', ")
            sql.append("'${poi.district}', ")
            sql.append("${poi.latitude}, ")
            sql.append("${poi.longitude}, ")
            sql.append("'${poi.address.replace("'", "''")}', ")
            sql.append("'${poi.description?.replace("'", "''") ?: ""}', ")
            sql.append("'${poi.priceLevel}', ")
            sql.append("${poi.estimatedCost ?: "NULL"}, ")
            sql.append("${poi.estimatedVisitDuration ?: "NULL"}, ")
            sql.append("'${poi.tags.joinToString(",")}', ")
            sql.append("'${poi.locationType}', ")
            sql.append("${poi.accessibility.wheelchairAccessible}, ")
            sql.append("${poi.scores.popularityScore}, ")
            sql.append("${poi.scores.crowdProxyScore}, ")
            sql.append("${poi.scores.sustainabilityScore}, ")
            sql.append("${poi.scores.localBusinessScore}, ")
            sql.append("${System.currentTimeMillis()}, ")
            sql.append("${System.currentTimeMillis()}")
            sql.append("\n);\n\n")
        }
        
        return sql.toString()
    }
    
    /**
     * Import POIs from JSON string
     */
    fun importFromJson(jsonString: String): List<POI> {
        return try {
            val poiArray = gson.fromJson(jsonString, Array<POI>::class.java)
            poiArray.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Import POIs from JSON file
     */
    fun importFromJsonFile(filePath: String): List<POI> {
        return try {
            val json = File(filePath).readText()
            importFromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

/**
 * Usage Example:
 * 
 * val pois = POISeedDataGenerator().generateSeedDataset()
 * 
 * // Export to JSON
 * POIDataSerializer.exportToJsonFile(pois, "pois.json")
 * 
 * // Export to CSV
 * POIDataSerializer.exportToCsvFile(pois, "pois.csv")
 * 
 * // Export to Kotlin code
 * val kotlinCode = POIDataSerializer.exportToKotlinDataClass(pois, "ESKISEHIR_POIS")
 * File("POISampleData.kt").writeText(kotlinCode)
 * 
 * // Export to SQL
 * val sqlCode = POIDataSerializer.exportToSqlInsert(pois, "poi")
 * File("insert_pois.sql").writeText(sqlCode)
 */
