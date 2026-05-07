package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.District
import kotlin.random.Random

object LocationGenerator {
    
    // District bounds in Eskişehir (latitude, longitude)
    // Precision: 6 decimals = ~0.1 meter accuracy
    data class DistrictBounds(
        val district: District,
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double,
        val centerLatitude: Double,
        val centerLongitude: Double
    )
    
    private val districtBounds = mapOf(
        // Odunpazarı: Old city center, museums, bazaar
        District.ODUNPAZARI to DistrictBounds(
            District.ODUNPAZARI,
            minLatitude = 39.7388,
            maxLatitude = 39.7510,
            minLongitude = 30.5076,
            maxLongitude = 30.5248,
            centerLatitude = 39.7449,
            centerLongitude = 30.5162
        ),
        
        // Sazova: Modern parks, venues
        District.SAZOVA to DistrictBounds(
            District.SAZOVA,
            minLatitude = 39.7702,
            maxLatitude = 39.8015,
            minLongitude = 30.5805,
            maxLongitude = 30.6215,
            centerLatitude = 39.7858,
            centerLongitude = 30.6010
        ),
        
        // Yunuseli: Residential, shops
        District.YUNUSELI to DistrictBounds(
            District.YUNUSELI,
            minLatitude = 39.7220,
            maxLatitude = 39.7390,
            minLongitude = 30.4810,
            maxLongitude = 30.5010,
            centerLatitude = 39.7305,
            centerLongitude = 30.4910
        ),
        
        // Eskişehir Center: Downtown, commerce
        District.ESKISEHIR_CENTER to DistrictBounds(
            District.ESKISEHIR_CENTER,
            minLatitude = 39.7500,
            maxLatitude = 39.7680,
            minLongitude = 30.5200,
            maxLongitude = 30.5450,
            centerLatitude = 39.7590,
            centerLongitude = 30.5325
        ),
        
        // Tepebaşı: Parks, green spaces
        District.TEPEBASΙ to DistrictBounds(
            District.TEPEBASΙ,
            minLatitude = 39.7600,
            maxLatitude = 39.7850,
            minLongitude = 30.5000,
            maxLongitude = 30.5200,
            centerLatitude = 39.7725,
            centerLongitude = 30.5100
        ),
        
        // Alpaslan: Modern development area
        District.ALPASLAN to DistrictBounds(
            District.ALPASLAN,
            minLatitude = 39.8100,
            maxLatitude = 39.8350,
            minLongitude = 30.5500,
            maxLongitude = 30.5850,
            centerLatitude = 39.8225,
            centerLongitude = 30.5675
        ),
        
        // Hoşnudiye: Residential, services
        District.HOŞNUDIYE to DistrictBounds(
            District.HOŞNUDIYE,
            minLatitude = 39.8400,
            maxLatitude = 39.8650,
            minLongitude = 30.5200,
            maxLongitude = 30.5500,
            centerLatitude = 39.8525,
            centerLongitude = 30.5350
        ),
        
        // Bahçelievler: Mixed development
        District.BAHÇELIEVLER to DistrictBounds(
            District.BAHÇELIEVLER,
            minLatitude = 39.7100,
            maxLatitude = 39.7300,
            minLongitude = 30.5300,
            maxLongitude = 30.5550,
            centerLatitude = 39.7200,
            centerLongitude = 30.5425
        ),
        
        // Mihalıcılar: Suburban area
        District.MIHALICILAR to DistrictBounds(
            District.MIHALICILAR,
            minLatitude = 39.8000,
            maxLatitude = 39.8250,
            minLongitude = 30.4700,
            maxLongitude = 30.5000,
            centerLatitude = 39.8125,
            centerLongitude = 30.4850
        ),
        
        // Siteler: Industrial/residential
        District.SITELER to DistrictBounds(
            District.SITELER,
            minLatitude = 39.7850,
            maxLatitude = 39.8100,
            minLongitude = 30.4500,
            maxLongitude = 30.4800,
            centerLatitude = 39.7975,
            centerLongitude = 30.4650
        )
    )
    
    /**
     * Generate a random coordinate within the bounds of a district
     * @param district Target district
     * @return Pair of (latitude, longitude) with 6 decimal precision
     */
    fun generateRandomCoordinate(district: District): Pair<Double, Double> {
        val bounds = districtBounds[district] ?: return Pair(39.7449, 30.5162)
        
        val random = Random
        val latitude = bounds.minLatitude + 
                      random.nextDouble() * (bounds.maxLatitude - bounds.minLatitude)
        val longitude = bounds.minLongitude + 
                       random.nextDouble() * (bounds.maxLongitude - bounds.minLongitude)
        
        // Round to 6 decimals for precision (~0.1m accuracy)
        return Pair(
            kotlin.math.round(latitude * 1_000_000) / 1_000_000,
            kotlin.math.round(longitude * 1_000_000) / 1_000_000
        )
    }
    
    /**
     * Generate multiple random coordinates in a district
     */
    fun generateRandomCoordinates(district: District, count: Int): List<Pair<Double, Double>> {
        return (0 until count).map { generateRandomCoordinate(district) }
    }
    
    /**
     * Get the center coordinates of a district
     */
    fun getDistrictCenter(district: District): Pair<Double, Double> {
        val bounds = districtBounds[district] ?: return Pair(39.7449, 30.5162)
        return Pair(bounds.centerLatitude, bounds.centerLongitude)
    }
    
    /**
     * Get bounds for a district
     */
    fun getDistrictBounds(district: District): DistrictBounds? {
        return districtBounds[district]
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     * @return Distance in kilometers
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }
    
    /**
     * Get all available districts
     */
    fun getAllDistricts(): List<District> {
        return districtBounds.keys.toList()
    }
}

/**
 * Usage Example:
 * 
 * val coordinate = LocationGenerator.generateRandomCoordinate(District.ODUNPAZARI)
 * println("Location: ${coordinate.first}, ${coordinate.second}")
 * 
 * val center = LocationGenerator.getDistrictCenter(District.SAZOVA)
 * val distance = LocationGenerator.calculateDistance(
 *     39.7449, 30.5162,
 *     center.first, center.second
 * )
 * println("Distance: $distance km")
 */
