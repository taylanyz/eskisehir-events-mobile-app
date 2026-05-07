package com.eskisehir.eventapp.data.local.dao

import androidx.room.*
import com.eskisehir.eventapp.data.model.POI
import kotlinx.coroutines.flow.Flow

/**
 * POI Data Access Object (DAO)
 * Provides database operations for POI entities
 */
@Dao
interface POIDAO {
    
    // CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOI(poi: POI): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiplePOIs(pois: List<POI>): List<Long>
    
    @Update
    suspend fun updatePOI(poi: POI): Int
    
    @Delete
    suspend fun deletePOI(poi: POI): Int
    
    @Query("DELETE FROM poi WHERE id = :id")
    suspend fun deletePOIById(id: String): Int
    
    @Query("DELETE FROM poi")
    suspend fun deleteAllPOIs(): Int
    
    // Read operations
    @Query("SELECT * FROM poi")
    suspend fun getAllPOIs(): List<POI>
    
    @Query("SELECT * FROM poi WHERE id = :id LIMIT 1")
    suspend fun getPOIById(id: String): POI?
    
    @Query("SELECT * FROM poi WHERE category = :category")
    suspend fun getPOIsByCategory(category: String): List<POI>
    
    @Query("SELECT * FROM poi WHERE district = :district")
    suspend fun getPOIsByDistrict(district: String): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE name LIKE '%' || :query || '%' 
           OR englishName LIKE '%' || :query || '%'
           OR address LIKE '%' || :query || '%'
    """)
    suspend fun searchPOIs(query: String): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE wheelchairAccessible = 1
        ORDER BY averageScore DESC
    """)
    suspend fun getAccessiblePOIs(): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE childFriendly = 1
        ORDER BY popularityScore DESC
    """)
    suspend fun getFamilyFriendlyPOIs(): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE priceLevel = 'FREE'
        ORDER BY averageScore DESC
    """)
    suspend fun getFreePOIs(): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        ORDER BY popularityScore DESC 
        LIMIT :limit
    """)
    suspend fun getPopularPOIs(limit: Int = 10): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE sustainabilityScore >= :minScore
        ORDER BY sustainabilityScore DESC
    """)
    suspend fun getSustainablePOIs(minScore: Float = 70f): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE localBusinessScore >= :minScore
        ORDER BY localBusinessScore DESC
    """)
    suspend fun getLocalBusinessPOIs(minScore: Float = 75f): List<POI>
    
    @Query("""
        SELECT * FROM poi 
        WHERE latitude BETWEEN :minLat AND :maxLat
          AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY averageScore DESC
    """)
    suspend fun getPOIsInBounds(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): List<POI>
    
    /**
     * Geographic distance query using Haversine formula
     * Finds POIs within radiusKm of the given coordinates
     */
    @Query("""
        SELECT * FROM poi 
        WHERE (
            6371 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS((:latitude - latitude) / 2)), 2) +
                COS(RADIANS(:latitude)) * COS(RADIANS(latitude)) * 
                POWER(SIN(RADIANS((:longitude - longitude) / 2)), 2)
            ))
        ) <= :radiusKm
        ORDER BY (
            6371 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS((:latitude - latitude) / 2)), 2) +
                COS(RADIANS(:latitude)) * COS(RADIANS(latitude)) * 
                POWER(SIN(RADIANS((:longitude - longitude) / 2)), 2)
            ))
        ) ASC
    """)
    suspend fun getPOIsNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): List<POI>
    
    // Reactive/Flow operations
    @Query("SELECT * FROM poi")
    fun observeAllPOIs(): Flow<List<POI>>
    
    @Query("SELECT * FROM poi WHERE district = :district")
    fun observePOIsByDistrict(district: String): Flow<List<POI>>
    
    @Query("""
        SELECT * FROM poi 
        ORDER BY popularityScore DESC 
        LIMIT :limit
    """)
    fun observePopularPOIs(limit: Int = 10): Flow<List<POI>>
    
    // Statistics
    @Query("SELECT COUNT(*) FROM poi")
    suspend fun getTotalPOICount(): Int
    
    @Query("SELECT COUNT(DISTINCT district) FROM poi")
    suspend fun getDistrictCount(): Int
    
    @Query("SELECT COUNT(DISTINCT category) FROM poi")
    suspend fun getCategoryCount(): Int
    
    @Query("""
        SELECT 
            AVG(popularityScore) as popularityScore,
            AVG(crowdProxyScore) as crowdProxyScore,
            AVG(sustainabilityScore) as sustainabilityScore,
            AVG(localBusinessScore) as localBusinessScore
        FROM poi
    """)
    suspend fun getAverageScores(): Map<String, Float>
}
