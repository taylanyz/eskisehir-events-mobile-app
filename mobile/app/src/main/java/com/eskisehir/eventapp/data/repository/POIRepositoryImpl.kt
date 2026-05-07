package com.eskisehir.eventapp.data.repository

import com.eskisehir.eventapp.data.local.dao.POIDAO
import com.eskisehir.eventapp.data.model.POI
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * POI Repository Implementation using Room Database
 * Implements data access operations for POI entities
 */
class POIRepositoryImpl @Inject constructor(
    private val poiDAO: POIDAO
) : POIRepository {
    
    // Read operations
    override suspend fun getAllPOIs(): List<POI> = poiDAO.getAllPOIs()
    
    override suspend fun getPOIById(id: String): POI? = poiDAO.getPOIById(id)
    
    override suspend fun getPOIsByCategory(category: String): List<POI> =
        poiDAO.getPOIsByCategory(category)
    
    override suspend fun getPOIsByDistrict(district: String): List<POI> =
        poiDAO.getPOIsByDistrict(district)
    
    override suspend fun searchPOIs(query: String): List<POI> =
        poiDAO.searchPOIs(query)
    
    override suspend fun getPOIsNearby(latitude: Double, longitude: Double, radiusKm: Double): List<POI> =
        poiDAO.getPOIsNearby(latitude, longitude, radiusKm)
    
    // Filter operations
    override suspend fun getAccessiblePOIs(): List<POI> = poiDAO.getAccessiblePOIs()
    
    override suspend fun getFamilyFriendlyPOIs(): List<POI> = poiDAO.getFamilyFriendlyPOIs()
    
    override suspend fun getFreePOIs(): List<POI> = poiDAO.getFreePOIs()
    
    override suspend fun getPopularPOIs(limit: Int): List<POI> = poiDAO.getPopularPOIs(limit)
    
    override suspend fun getSustainablePOIs(minScore: Float): List<POI> =
        poiDAO.getSustainablePOIs(minScore)
    
    override suspend fun getLocalBusinessPOIs(minScore: Float): List<POI> =
        poiDAO.getLocalBusinessPOIs(minScore)
    
    // Write operations
    override suspend fun insertPOI(poi: POI): Long = poiDAO.insertPOI(poi)
    
    override suspend fun insertMultiplePOIs(pois: List<POI>): List<Long> =
        poiDAO.insertMultiplePOIs(pois)
    
    override suspend fun updatePOI(poi: POI) {
        poiDAO.updatePOI(poi)
    }
    
    override suspend fun deletePOI(id: String): Int = poiDAO.deletePOIById(id)
    
    override suspend fun deleteAllPOIs() {
        poiDAO.deleteAllPOIs()
    }
    
    // Reactive/Flow operations
    override fun observeAllPOIs(): Flow<List<POI>> = poiDAO.observeAllPOIs()
    
    override fun observePOIsByDistrict(district: String): Flow<List<POI>> =
        poiDAO.observePOIsByDistrict(district)
    
    override fun observePopularPOIs(limit: Int): Flow<List<POI>> =
        poiDAO.observePopularPOIs(limit)
    
    // Statistics
    override suspend fun getTotalPOICount(): Int = poiDAO.getTotalPOICount()
    
    override suspend fun getDistrictCount(): Int = poiDAO.getDistrictCount()
    
    override suspend fun getCategoryCount(): Int = poiDAO.getCategoryCount()
    
    override suspend fun getAverageScores(): Map<String, Float> = poiDAO.getAverageScores()
}
