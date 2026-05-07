package com.eskisehir.eventapp.data.model

import java.util.UUID

// POI Categories - 29 types
enum class POICategory {
    // Museums & Culture
    MUSEUM, HISTORICAL_SITE, CULTURAL_CENTER, ART_GALLERY,
    // Religious Sites
    MOSQUE, CHURCH, SYNAGOGUE, TEMPLE,
    // Parks & Nature
    PARK, GARDEN, NATURE_RESERVE, ZOO,
    // Food & Dining
    RESTAURANT, CAFE, BAKERY, TRADITIONAL_MARKET,
    // Shopping
    SHOPPING_CENTER, BAZAAR, ANTIQUE_SHOP, BOOKSTORE,
    // Recreation
    CINEMA, THEATER, SPORTS_FACILITY, SWIMMING_POOL,
    // Education
    LIBRARY, UNIVERSITY, EDUCATIONAL_INSTITUTION,
    // Other
    LANDMARK, SCENIC_VIEWPOINT, OTHER
}

// Districts in Eskişehir - 10 areas
enum class District {
    ODUNPAZARI, SAZOVA, YUNUSELI, ESKISEHIR_CENTER, 
    TEPEBASΙ, ALPASLAN, HOŞNUDIYE, BAHÇELIEVLER,
    MIHALICILAR, SITELER
}

// Price Levels - 5 categories
enum class PriceLevel {
    FREE,           // ₺0
    BUDGET,         // ₺1-50
    MODERATE,       // ₺51-150
    EXPENSIVE,      // ₺151-300
    LUXURY          // ₺300+
}

// Indoor/Outdoor classification - 3 types
enum class LocationType {
    INDOOR, OUTDOOR, MIXED
}

// Accessibility Features
data class AccessibilityFeatures(
    val wheelchairAccessible: Boolean = false,
    val publicTransitAccess: Boolean = false,
    val parkingAvailable: Boolean = false,
    val restrooms: Boolean = false,
    val childFriendly: Boolean = false,
    val seniorFriendly: Boolean = false,
    val petFriendly: Boolean = false
)

// Operating Hours
data class OperatingHours(
    val monday: String? = null,      // "09:00-18:00"
    val tuesday: String? = null,
    val wednesday: String? = null,
    val thursday: String? = null,
    val friday: String? = null,
    val saturday: String? = null,
    val sunday: String? = null,
    val closedDays: List<String> = emptyList(),  // e.g., ["Ramazan", "New Year"]
    val notes: String? = null
)

// Contact Information
data class ContactInfo(
    val phoneNumber: String? = null,
    val email: String? = null,
    val website: String? = null,
    val instagram: String? = null,
    val facebook: String? = null
)

// Proxy Scores (0-100 scale)
data class ProxyScores(
    val popularityScore: Float = 0f,      // Based on category, rating, reviews
    val crowdProxyScore: Float = 0f,      // Time of day, day of week, capacity
    val sustainabilityScore: Float = 0f,  // Environmental, cultural, local benefit
    val localBusinessScore: Float = 0f    // Ownership, employment, supply chain
)

// Main POI Data Model
data class POI(
    // Core Identification
    val id: String = UUID.randomUUID().toString(),
    val name: String,                      // Turkish name
    val englishName: String? = null,
    val category: POICategory,
    val district: District,
    
    // Location & Geography (6 decimal precision = ~0.1m accuracy)
    val latitude: Double,                  // e.g., 39.742156
    val longitude: Double,                 // e.g., 30.515234
    val address: String,
    
    // Description & Details
    val description: String? = null,       // Turkish description
    val englishDescription: String? = null,
    
    // Operations
    val operatingHours: OperatingHours = OperatingHours(),
    
    // Pricing
    val priceLevel: PriceLevel = PriceLevel.FREE,
    val estimatedCost: Float? = null,      // Average cost in TL
    val estimatedVisitDuration: Int? = null,  // in minutes
    
    // Classification
    val tags: List<String> = emptyList(),  // e.g., ["historical", "family-friendly"]
    val locationType: LocationType = LocationType.MIXED,
    
    // Accessibility & Amenities
    val accessibility: AccessibilityFeatures = AccessibilityFeatures(),
    
    // Contact
    val contact: ContactInfo = ContactInfo(),
    
    // Proxy Scores (calculated)
    val scores: ProxyScores = ProxyScores(),
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dataSourceNotes: String? = null
) {
    // Validation helper
    fun isValid(): Boolean {
        return name.isNotBlank() &&
               latitude in -90.0..90.0 &&
               longitude in -180.0..180.0 &&
               address.isNotBlank()
    }
    
    // Human-readable location string
    fun getLocationString(): String = "$address, ${district.name}"
}

// Example POI data for testing/demo
fun createSamplePOI(): POI {
    return POI(
        name = "Kurşunlu Camii",
        englishName = "Kurşunlu Mosque",
        category = POICategory.MOSQUE,
        district = District.ODUNPAZARI,
        latitude = 39.7423,
        longitude = 30.5152,
        address = "Kurşunlu Cami Sokak, Odunpazarı/Eskişehir",
        description = "Osmanlı döneminden kalma tarihi camii, şehrin en eski yapılarından biridir.",
        englishDescription = "Historic mosque from Ottoman period, one of the oldest structures in the city.",
        priceLevel = PriceLevel.FREE,
        tags = listOf("historical", "religious", "ottoman"),
        locationType = LocationType.INDOOR,
        accessibility = AccessibilityFeatures(
            wheelchairAccessible = true,
            publicTransitAccess = true,
            restrooms = true,
            seniorFriendly = true
        ),
        operatingHours = OperatingHours(
            monday = "open",
            tuesday = "open",
            wednesday = "open",
            thursday = "open",
            friday = "open",
            saturday = "open",
            sunday = "open"
        ),
        contact = ContactInfo(
            phoneNumber = "+90 222 123 4567"
        ),
        scores = ProxyScores(
            popularityScore = 82f,
            crowdProxyScore = 45f,
            sustainabilityScore = 88f,
            localBusinessScore = 92f
        )
    )
}
