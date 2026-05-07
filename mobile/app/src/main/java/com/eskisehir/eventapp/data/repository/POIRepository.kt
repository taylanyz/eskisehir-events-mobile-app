package com.eskisehir.eventapp.data.repository

import com.eskisehir.eventapp.data.model.POI
import kotlinx.coroutines.flow.Flow

/**
 * POI Repository Interface
 * Defines contract for POI data access
 */
interface POIRepository {
    // Read operations
    suspend fun getAllPOIs(): List<POI>
    suspend fun getPOIById(id: String): POI?
    suspend fun getPOIsByCategory(category: String): List<POI>
    suspend fun getPOIsByDistrict(district: String): List<POI>
    suspend fun searchPOIs(query: String): List<POI>
    suspend fun getPOIsNearby(latitude: Double, longitude: Double, radiusKm: Double): List<POI>
    
    // Filter operations
    suspend fun getAccessiblePOIs(): List<POI>
    suspend fun getFamilyFriendlyPOIs(): List<POI>
    suspend fun getFreePOIs(): List<POI>
    suspend fun getPopularPOIs(limit: Int = 10): List<POI>
    suspend fun getSustainablePOIs(minScore: Float = 70f): List<POI>
    suspend fun getLocalBusinessPOIs(minScore: Float = 75f): List<POI>
    
    // Write operations
    suspend fun insertPOI(poi: POI): Long
    suspend fun insertMultiplePOIs(pois: List<POI>): List<Long>
    suspend fun updatePOI(poi: POI)
    suspend fun deletePOI(id: String): Int
    suspend fun deleteAllPOIs()
    
    // Reactive/Flow operations
    fun observeAllPOIs(): Flow<List<POI>>
    fun observePOIsByDistrict(district: String): Flow<List<POI>>
    fun observePopularPOIs(limit: Int = 10): Flow<List<POI>>
    
    // Statistics
    suspend fun getTotalPOICount(): Int
    suspend fun getDistrictCount(): Int
    suspend fun getCategoryCount(): Int
    suspend fun getAverageScores(): Map<String, Float>
}
