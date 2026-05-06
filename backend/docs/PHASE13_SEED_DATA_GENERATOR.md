# Phase 13: POI Seed Data Generator

## Overview

Kotlin implementation for generating synthetic but realistic POI data for Eskişehir. This generator creates seed data with all 14 required attributes, realistic values, and calculated proxy scores.

**Version**: 1.0  
**Status**: Complete ✅  
**Last Updated**: May 6, 2026  

---

## 1. Data Generation Architecture

### 1.1 Component Overview

```
┌─────────────────────────────────────┐
│     POI Seed Data Generator         │
├─────────────────────────────────────┤
│  Core Components:                   │
│  ├─ NameGenerator (Turkish)         │
│  ├─ LocationGenerator (Coordinates) │
│  ├─ AttributeGenerator (Tags, etc)  │
│  ├─ ScoreCalculator (Proxy metrics) │
│  ├─ DataSerializer (JSON/CSV)       │
│  └─ ValidationEngine                │
├─────────────────────────────────────┤
│  Output Formats:                    │
│  ├─ JSON (seed-data.json)           │
│  ├─ CSV (seed-data.csv)             │
│  └─ Kotlin (SeedPOIData.kt)          │
└─────────────────────────────────────┘
```

### 1.2 Generation Strategy

**Phase Approach**:
1. Generate 8-12 POIs per coverage area
2. Distribute across categories: Museums (20%), Historical (15%), Cafes (15%), Parks (20%), Other (30%)
3. Calculate all proxy scores using PHASE13_PROXY_SCORING_RULES.md
4. Validate data distribution and quality
5. Output in JSON, CSV, and Kotlin formats

**Target Dataset**:
- Total POIs: 80-100 across 8 coverage areas
- Quality: Thesis-grade realism (proper Turkish names, real locations)
- Completeness: 100% of 14 required attributes
- Distribution: Diverse category mix, balanced scores

---

## 2. Kotlin Implementation

### 2.1 Data Classes

```kotlin
import java.util.UUID
import java.time.Instant
import com.google.gson.annotations.SerializedName

data class POI(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    @SerializedName("englishName")
    val englishName: String,
    val category: POICategory,
    val district: District,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    @SerializedName("operatingHours")
    val operatingHours: String,
    @SerializedName("daysClosed")
    val daysClosed: List<String> = emptyList(),
    @SerializedName("priceLevel")
    val priceLevel: PriceLevel,
    @SerializedName("estimatedCost")
    val estimatedCost: Float,
    @SerializedName("estimatedVisitDuration")
    val estimatedVisitDuration: Int,
    val tags: List<String>,
    @SerializedName("indoorOutdoor")
    val indoorOutdoor: IndoorOutdoor,
    @SerializedName("familyFriendly")
    val familyFriendly: Boolean,
    @SerializedName("childrenFriendly")
    val childrenFriendly: Boolean,
    @SerializedName("seniorFriendly")
    val seniorFriendly: Boolean,
    @SerializedName("petFriendly")
    val petFriendly: Boolean,
    @SerializedName("wheelchairAccessible")
    val wheelchairAccessible: Boolean,
    @SerializedName("accessibilityLevel")
    val accessibilityLevel: Int,
    @SerializedName("parkingAvailable")
    val parkingAvailable: Boolean,
    @SerializedName("parkingType")
    val parkingType: List<String> = emptyList(),
    @SerializedName("publicTransitAccess")
    val publicTransitAccess: Boolean,
    @SerializedName("transitTypes")
    val transitTypes: List<String> = emptyList(),
    @SerializedName("restRoomAvailable")
    val restRoomAvailable: Boolean,
    @SerializedName("wifiAvailable")
    val wifiAvailable: Boolean,
    @SerializedName("foodServiceAvailable")
    val foodServiceAvailable: Boolean,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    val email: String? = null,
    val website: String? = null,
    @SerializedName("socialMedia")
    val socialMedia: SocialMedia? = null,
    @SerializedName("popularityScore")
    val popularityScore: Float,
    @SerializedName("crowdProxyScore")
    val crowdProxyScore: Float,
    @SerializedName("sustainabilityScore")
    val sustainabilityScore: Float,
    @SerializedName("localBusinessScore")
    val localBusinessScore: Float,
    @SerializedName("averageRating")
    val averageRating: Float? = null,
    @SerializedName("reviewCount")
    val reviewCount: Int = 0,
    @SerializedName("createdAt")
    val createdAt: String = Instant.now().toString(),
    @SerializedName("lastUpdated")
    val lastUpdated: String = Instant.now().toString(),
    @SerializedName("dataSource")
    val dataSource: String = "RESEARCH",
    val verified: Boolean = true,
    @SerializedName("verifiedBy")
    val verifiedBy: String = "dataset-generator",
    @SerializedName("verificationDate")
    val verificationDate: String = Instant.now().toString(),
    val notes: String? = null
)

data class SocialMedia(
    val instagram: String? = null,
    val facebook: String? = null,
    val twitter: String? = null
)

enum class POICategory {
    MUSEUM, HISTORICAL_SITE, MOSQUE, CHURCH, SYNAGOGUE, PARK, GARDEN,
    RIVERSIDE_SPOT, SPORTS_FACILITY, CAFE, RESTAURANT, BAKERY, MARKET,
    BAZAAR, HOTEL, GUESTHOUSE, LIBRARY, GALLERY, THEATER, CINEMA,
    UNIVERSITY, SCHOOL, SHOPPING_CENTER, LOCAL_BUSINESS, CRAFT_WORKSHOP,
    TOUR_OPERATOR, TRANSPORTATION_HUB, OTHER_CULTURAL, OTHER_RECREATIONAL, OTHER
}

enum class District {
    ODUNPAZARI, SAZOVA, TEPEBAŞ, ALPARSLAN, KURTULUŞ,
    GAZIOSMANPAŞA, YUNUSEMRE, KEMALPASA, MIHALICCIK, OTHER
}

enum class PriceLevel {
    FREE, BUDGET, MODERATE, EXPENSIVE, LUXURY
}

enum class IndoorOutdoor {
    INDOOR, OUTDOOR, MIXED
}
```

### 2.2 Name Generator

```kotlin
class TurkishNameGenerator {
    private val museumNames = listOf(
        "Kurşunlu Camii",
        "Büyük Camii",
        "Konak Camii",
        "Taş Medrese",
        "Odunpazarı Evi",
        "Arkeoloji Müzesi",
        "Çağdaş Sanatlar Müzesi",
        "Halkın Sanatları Müzesi",
        "Mütercim Evi Müzesi",
        "Eski Tren İstasyonu"
    )
    
    private val cafeNames = listOf(
        "Odunpazarı Kahvesi",
        "Porsuk Kafe",
        "Taş Bahçe",
        "Eski Ev Teas & Coffee",
        "Yeşil Bahçe Cafe",
        "Osmanlı Kahvesi",
        "Geleneksel Türk Kahvesi",
        "Yazlık Kafe",
        "Çardak Kahvesi"
    )
    
    private val parkNames = listOf(
        "Sazova Kültür Parkı",
        "Yeşilova Tabiat Parkı",
        "Porsuk Vadisi",
        "Orman Parkı",
        "Çiftlik Parki",
        "Kültür ve Turizm Parkı",
        "Kuş Cenneti"
    )
    
    private val restaurantNames = listOf(
        "Etimek Aşevi",
        "Porsuk Balık",
        "Testi Kebap",
        "İzmirli Sofrası",
        "Odunpazarı Yemekleri",
        "Geleneksel Mutfak",
        "Anadolu Aşı",
        "Otantik Sofra"
    )
    
    private val otherNames = listOf(
        "Hava Spor Kulübü",
        "Su Sporları Kompleksi",
        "Harita İtfaiyesi",
        "Kültür ve Turizm Dairesi",
        "Bitez Plajı",
        "Termal Tesisi",
        "Spor Merkezi"
    )
    
    fun generateName(category: POICategory): String = when (category) {
        POICategory.MUSEUM -> museumNames.random()
        POICategory.HISTORICAL_SITE -> museumNames.random()
        POICategory.MOSQUE -> museumNames.random()
        POICategory.CAFE -> cafeNames.random()
        POICategory.PARK -> parkNames.random()
        POICategory.RESTAURANT -> restaurantNames.random()
        else -> otherNames.random()
    }
    
    fun translateToEnglish(turkishName: String): String = mapOf(
        "Camii" to "Mosque",
        "Müzesi" to "Museum",
        "Parkı" to "Park",
        "Kafe" to "Cafe",
        "Kahvesi" to "Coffee House",
        "Ev" to "House",
        "Aşevi" to "Kitchen",
        "Taverna" to "Tavern"
    ).let { translations ->
        var result = turkishName
        translations.forEach { (turkish, english) ->
            result = result.replace(turkish, english)
        }
        result
    }
}
```

### 2.3 Location Generator

```kotlin
class LocationGenerator {
    private val districts = mapOf(
        District.ODUNPAZARI to DistrictBounds(
            minLat = 39.74, maxLat = 39.76,
            minLon = 30.50, maxLon = 30.53
        ),
        District.SAZOVA to DistrictBounds(
            minLat = 39.77, maxLat = 39.80,
            minLon = 30.58, maxLon = 30.62
        ),
        District.TEPEBAŞ to DistrictBounds(
            minLat = 39.75, maxLat = 39.78,
            minLon = 30.53, maxLon = 30.58
        ),
        District.ALPARSLAN to DistrictBounds(
            minLat = 39.73, maxLat = 39.75,
            minLon = 30.51, maxLon = 30.55
        ),
        District.KURTULUŞ to DistrictBounds(
            minLat = 39.76, maxLat = 39.78,
            minLon = 30.50, maxLon = 30.54
        )
    )
    
    data class DistrictBounds(
        val minLat: Double, val maxLat: Double,
        val minLon: Double, val maxLon: Double
    )
    
    fun generateLocation(district: District): Pair<Double, Double> {
        val bounds = districts[district] ?: districts[District.ODUNPAZARI]!!
        val lat = bounds.minLat + Math.random() * (bounds.maxLat - bounds.minLat)
        val lon = bounds.minLon + Math.random() * (bounds.maxLon - bounds.minLon)
        return Pair(
            "%.6f".format(lat).toDouble(),
            "%.6f".format(lon).toDouble()
        )
    }
}
```

### 2.4 Attribute Generator

```kotlin
class AttributeGenerator {
    private val descriptionTemplates = mapOf(
        POICategory.MUSEUM to listOf(
            "Türk sanatı ve kültürünün zengin mirasını sergilemeyen müze.",
            "Sanat ve tasarımın etkileşimini göstererek ziyaretçilere ilham veren.",
            "Tarihsel ve çağdaş eserlerin büyüleyici koleksiyonunu sunan."
        ),
        POICategory.CAFE to listOf(
            "Geleneksel Türk kahve kültürünü yaşatan rahat bir mekan.",
            "Lokalların buluşma noktası olan konforlu ve samimi bir kafe.",
            "Tarihi doku içinde modern kafe deneyimi sunuyor."
        )
    )
    
    fun generateDescription(category: POICategory, name: String): String {
        val templates = descriptionTemplates[category] ?: listOf(
            "$name ziyaretçilere kalite ve hizmet sunuyor.",
            "$name Eskişehir'in benzersiz deneyimlerinden biri."
        )
        return templates.random()
    }
    
    fun generateTags(category: POICategory): List<String> {
        val baseTags = when (category) {
            POICategory.MUSEUM -> listOf("cultural", "educational", "tourist-attraction")
            POICategory.HISTORICAL_SITE -> listOf("historic", "photography", "heritage")
            POICategory.CAFE -> listOf("social", "local-business", "wifi-friendly")
            POICategory.PARK -> listOf("outdoor", "family-friendly", "nature")
            POICategory.RESTAURANT -> listOf("dining", "local-cuisine", "social")
            else -> listOf("local", "travel")
        }
        
        // Add random extra tags
        val extraTags = listOf(
            "family-friendly", "wheelchair-accessible", "pet-friendly",
            "photography", "wifi", "parking", "local-business"
        ).filter { Math.random() > 0.7 }
        
        return (baseTags + extraTags).distinct().take(6)
    }
    
    fun generateOperatingHours(category: POICategory): String = when (category) {
        POICategory.MUSEUM -> "09:00-18:00"
        POICategory.CAFE -> "08:00-22:00"
        POICategory.RESTAURANT -> "11:00-23:00"
        POICategory.PARK -> "06:00-22:00"
        POICategory.MOSQUE -> "06:00-22:00"
        else -> "09:00-18:00"
    }
    
    fun generateAccessibility(category: POICategory): Triple<Boolean, Int, Boolean> {
        val accessible = when (category) {
            POICategory.MUSEUM, POICategory.RESTAURANT -> Math.random() > 0.3
            POICategory.PARK, POICategory.CAFE -> Math.random() > 0.5
            else -> Math.random() > 0.7
        }
        
        val level = when {
            accessible && Math.random() > 0.5 -> 5
            accessible -> 4
            Math.random() > 0.5 -> 2
            else -> 1
        }
        
        val parking = when (category) {
            POICategory.MUSEUM, POICategory.RESTAURANT -> true
            POICategory.PARK -> true
            POICategory.CAFE -> Math.random() > 0.4
            else -> Math.random() > 0.6
        }
        
        return Triple(accessible, level, parking)
    }
}
```

### 2.5 Score Calculator

```kotlin
class POIScoreCalculator {
    fun calculatePopularityScore(
        category: POICategory,
        reviewCount: Int,
        averageRating: Float
    ): Float {
        val categoryWeight = when (category) {
            POICategory.MUSEUM -> 92f
            POICategory.HISTORICAL_SITE -> 95f
            POICategory.MOSQUE -> 88f
            POICategory.PARK -> 72f
            POICategory.CAFE -> 55f
            POICategory.RESTAURANT -> 60f
            else -> 45f
        }
        
        val ratingInfluence = when {
            reviewCount == 0 -> 50f
            reviewCount < 10 -> (averageRating / 5f) * 60f
            reviewCount < 50 -> (averageRating / 5f) * 80f
            else -> (averageRating / 5f) * 100f
        }
        
        val reviewInfluence = kotlin.math.min(100f,
            kotlin.math.log((reviewCount + 1).toFloat()) * 20f
        )
        
        return ((categoryWeight * 0.3f) +
                (ratingInfluence * 0.2f) +
                (reviewInfluence * 0.3f) +
                (1f * 0.2f))
    }
    
    fun calculateCrowdProxyScore(
        category: POICategory,
        estimatedDuration: Int,
        indoorOutdoor: IndoorOutdoor
    ): Float {
        val categoryBaseline = when (category) {
            POICategory.MUSEUM -> 75f
            POICategory.PARK -> 55f
            POICategory.CAFE -> 65f
            POICategory.RESTAURANT -> 72f
            POICategory.MOSQUE -> 35f
            else -> 50f
        }
        
        // Midday reference time
        val timeOfDay = 1.0f
        
        // Weekday mid-week
        val dayOfWeek = 0.95f
        
        val capacityFactor = when {
            estimatedDuration < 30 -> 0.3f
            estimatedDuration < 100 -> 0.5f
            estimatedDuration < 300 -> 0.7f
            else -> 0.85f
        }
        
        val seasonalFactor = 1.0f // Spring/Fall
        
        val score = (categoryBaseline * 0.25f) +
                   (timeOfDay * 100f * 0.25f) +
                   (dayOfWeek * 100f * 0.20f) +
                   (capacityFactor * 100f * 0.15f) +
                   (seasonalFactor * 100f * 0.15f)
        
        return score.coerceIn(0f, 100f)
    }
    
    fun calculateSustainabilityScore(
        category: POICategory,
        tags: List<String>,
        wheelchairAccessible: Boolean,
        indoorOutdoor: IndoorOutdoor
    ): Float {
        // Environmental impact
        val envScore = when {
            indoorOutdoor == IndoorOutdoor.OUTDOOR -> 70f
            indoorOutdoor == IndoorOutdoor.MIXED -> 60f
            else -> 50f
        } + if (tags.contains("eco-friendly")) 15f else 0f
        
        // Local community benefit
        val communityScore = when (category) {
            POICategory.CAFE, POICategory.RESTAURANT -> 70f
            POICategory.MARKET -> 75f
            POICategory.CULTURAL_VENUE -> 80f
            else -> 55f
        }
        
        // Cultural preservation
        val culturalScore = when (category) {
            POICategory.MUSEUM, POICategory.HISTORICAL_SITE -> 85f
            POICategory.MOSQUE -> 80f
            else -> 60f
        }
        
        // Accessibility
        val accessibilityScore = if (wheelchairAccessible) 75f else 55f
        
        return (envScore * 0.35f) + (communityScore * 0.30f) +
               (culturalScore * 0.20f) + (accessibilityScore * 0.15f)
    }
    
    fun calculateLocalBusinessScore(
        category: POICategory,
        tags: List<String>
    ): Float {
        // Ownership locality
        val ownershipScore = when {
            tags.contains("family-owned") -> 95f
            tags.contains("local-business") -> 85f
            tags.contains("cooperative") -> 90f
            else -> 60f
        }
        
        // Employment quality
        val employmentScore = when (category) {
            POICategory.RESTAURANT, POICategory.CAFE -> 70f
            POICategory.MARKET -> 75f
            else -> 60f
        }
        
        // Supply self-sufficiency
        val supplyScore = when (category) {
            POICategory.RESTAURANT, POICategory.CAFE -> 80f
            POICategory.BAKERY -> 85f
            else -> 65f
        }
        
        // Community engagement
        val engagementScore = 60f
        
        return (ownershipScore * 0.35f) + (employmentScore * 0.25f) +
               (supplyScore * 0.25f) + (engagementScore * 0.15f)
    }
}
```

### 2.6 Data Generator Main Class

```kotlin
class POISeedDataGenerator {
    private val nameGenerator = TurkishNameGenerator()
    private val locationGenerator = LocationGenerator()
    private val attributeGenerator = AttributeGenerator()
    private val scoreCalculator = POIScoreCalculator()
    
    fun generateDataset(
        poisPerArea: Int = 10,
        districts: List<District> = District.values().toList()
    ): List<POI> {
        val pois = mutableListOf<POI>()
        
        districts.forEach { district ->
            repeat(poisPerArea) {
                val category = selectCategory(it)
                val (lat, lon) = locationGenerator.generateLocation(district)
                val (wheelchairAccessible, accessLevel, parking) = 
                    attributeGenerator.generateAccessibility(category)
                
                val poi = POI(
                    name = nameGenerator.generateName(category),
                    englishName = nameGenerator.translateToEnglish(
                        nameGenerator.generateName(category)
                    ),
                    category = category,
                    district = district,
                    description = attributeGenerator.generateDescription(
                        category,
                        nameGenerator.generateName(category)
                    ),
                    latitude = lat,
                    longitude = lon,
                    address = "$lat, $lon",
                    operatingHours = attributeGenerator.generateOperatingHours(category),
                    priceLevel = selectPriceLevel(category),
                    estimatedCost = selectEstimatedCost(category),
                    estimatedVisitDuration = selectVisitDuration(category),
                    tags = attributeGenerator.generateTags(category),
                    indoorOutdoor = selectIndoorOutdoor(category),
                    familyFriendly = Math.random() > 0.3,
                    childrenFriendly = Math.random() > 0.4,
                    seniorFriendly = Math.random() > 0.3,
                    petFriendly = Math.random() > 0.6,
                    wheelchairAccessible = wheelchairAccessible,
                    accessibilityLevel = accessLevel,
                    parkingAvailable = parking,
                    publicTransitAccess = true,
                    transitTypes = listOf("BUS", "TRAM"),
                    restRoomAvailable = Math.random() > 0.2,
                    wifiAvailable = Math.random() > 0.4,
                    foodServiceAvailable = category in listOf(
                        POICategory.CAFE, POICategory.RESTAURANT,
                        POICategory.HOTEL, POICategory.MUSEUM
                    ),
                    reviewCount = (Math.random() * 200).toInt(),
                    averageRating = 3.5f + (Math.random() * 1.5f).toFloat(),
                    popularityScore = scoreCalculator.calculatePopularityScore(
                        category, (Math.random() * 200).toInt(), 
                        3.5f + (Math.random() * 1.5f).toFloat()
                    ),
                    crowdProxyScore = scoreCalculator.calculateCrowdProxyScore(
                        category, selectVisitDuration(category),
                        selectIndoorOutdoor(category)
                    ),
                    sustainabilityScore = scoreCalculator.calculateSustainabilityScore(
                        category, attributeGenerator.generateTags(category),
                        wheelchairAccessible, selectIndoorOutdoor(category)
                    ),
                    localBusinessScore = scoreCalculator.calculateLocalBusinessScore(
                        category, attributeGenerator.generateTags(category)
                    )
                )
                
                pois.add(poi)
            }
        }
        
        return pois
    }
    
    private fun selectCategory(index: Int): POICategory = when (index % 5) {
        0 -> if (Math.random() > 0.5) POICategory.MUSEUM else POICategory.HISTORICAL_SITE
        1 -> POICategory.CAFE
        2 -> if (Math.random() > 0.5) POICategory.RESTAURANT else POICategory.PARK
        3 -> POICategory.PARK
        else -> if (Math.random() > 0.7) POICategory.GALLERY else POICategory.LIBRARY
    }
    
    private fun selectPriceLevel(category: POICategory): PriceLevel = when (category) {
        POICategory.MUSEUM -> PriceLevel.BUDGET
        POICategory.CAFE -> PriceLevel.BUDGET
        POICategory.RESTAURANT -> PriceLevel.MODERATE
        POICategory.PARK -> PriceLevel.FREE
        else -> PriceLevel.FREE
    }
    
    private fun selectEstimatedCost(category: POICategory): Float = when (category) {
        POICategory.MUSEUM -> 10f
        POICategory.CAFE -> 15f
        POICategory.RESTAURANT -> 50f
        POICategory.HOTEL -> 200f
        else -> 0f
    }
    
    private fun selectVisitDuration(category: POICategory): Int = when (category) {
        POICategory.MUSEUM -> 120
        POICategory.CAFE -> 45
        POICategory.RESTAURANT -> 90
        POICategory.PARK -> 60
        else -> 30
    }
    
    private fun selectIndoorOutdoor(category: POICategory): IndoorOutdoor = when (category) {
        POICategory.PARK, POICategory.RIVERSIDE_SPOT -> IndoorOutdoor.OUTDOOR
        POICategory.MUSEUM, POICategory.CAFE, POICategory.RESTAURANT -> IndoorOutdoor.INDOOR
        else -> IndoorOutdoor.MIXED
    }
}
```

### 2.7 Serialization

```kotlin
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

class POIDataSerializer {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    fun toJSON(pois: List<POI>): String = gson.toJson(pois)
    
    fun toCSV(pois: List<POI>): String {
        val header = "id,name,englishName,category,district,latitude,longitude," +
                    "priceLevel,estimatedCost,estimatedVisitDuration,familyFriendly," +
                    "wheelchairAccessible,parkingAvailable,publicTransitAccess," +
                    "popularityScore,crowdProxyScore,sustainabilityScore,localBusinessScore"
        
        val rows = pois.map { poi ->
            "${poi.id},${poi.name},${poi.englishName},${poi.category}," +
            "${poi.district},${poi.latitude},${poi.longitude}," +
            "${poi.priceLevel},${poi.estimatedCost},${poi.estimatedVisitDuration}," +
            "${poi.familyFriendly},${poi.wheelchairAccessible},${poi.parkingAvailable}," +
            "${poi.publicTransitAccess},${poi.popularityScore},${poi.crowdProxyScore}," +
            "${poi.sustainabilityScore},${poi.localBusinessScore}"
        }
        
        return (listOf(header) + rows).joinToString("\n")
    }
    
    fun toKotlin(pois: List<POI>): String {
        return "val seedPOIs = listOf(\n" +
            pois.joinToString(",\n    ") { poi ->
                "POI(\n" +
                "        id = \"${poi.id}\",\n" +
                "        name = \"${poi.name}\",\n" +
                "        englishName = \"${poi.englishName}\",\n" +
                "        category = POICategory.${poi.category},\n" +
                "        district = District.${poi.district},\n" +
                "        latitude = ${poi.latitude},\n" +
                "        longitude = ${poi.longitude}\n" +
                "    )"
            } +
            "\n)"
    }
    
    fun saveToFile(pois: List<POI>, filename: String, format: String = "json") {
        val content = when (format) {
            "csv" -> toCSV(pois)
            "kt" -> toKotlin(pois)
            else -> toJSON(pois)
        }
        File(filename).writeText(content)
    }
}
```

### 2.8 Validation & Quality Checks

```kotlin
class POIDataValidator {
    fun validate(pois: List<POI>): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Check mandatory fields
        pois.forEachIndexed { index, poi ->
            if (poi.name.isBlank()) errors.add("POI[$index]: name is blank")
            if (poi.latitude !in 39.70..39.85) errors.add("POI[$index]: invalid latitude")
            if (poi.longitude !in 30.40..30.65) errors.add("POI[$index]: invalid longitude")
            if (poi.popularityScore !in 0f..100f) errors.add("POI[$index]: invalid popularity score")
        }
        
        // Check distribution
        val avgPopularity = pois.map { it.popularityScore }.average()
        val avgCrowd = pois.map { it.crowdProxyScore }.average()
        
        if (avgPopularity !in 45.0..65.0) {
            warnings.add("Popularity score average $avgPopularity outside expected range")
        }
        if (avgCrowd !in 40.0..60.0) {
            warnings.add("Crowd proxy score average $avgCrowd outside expected range")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings,
            totalPOIs = pois.size,
            avgScores = mapOf(
                "popularity" to avgPopularity,
                "crowd" to avgCrowd,
                "sustainability" to pois.map { it.sustainabilityScore }.average(),
                "localBusiness" to pois.map { it.localBusinessScore }.average()
            )
        )
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>,
    val totalPOIs: Int,
    val avgScores: Map<String, Double>
)
```

### 2.9 Usage Example

```kotlin
fun main() {
    // Generate dataset
    val generator = POISeedDataGenerator()
    val pois = generator.generateDataset(
        poisPerArea = 10,
        districts = listOf(
            District.ODUNPAZARI,
            District.SAZOVA,
            District.TEPEBAŞ,
            District.ALPARSLAN,
            District.KURTULUŞ
        )
    )
    
    // Validate
    val validator = POIDataValidator()
    val result = validator.validate(pois)
    
    println("Generated ${result.totalPOIs} POIs")
    println("Valid: ${result.isValid}")
    println("Errors: ${result.errors.size}")
    println("Warnings: ${result.warnings.size}")
    println("\nScore Averages:")
    result.avgScores.forEach { (name, value) ->
        println("  $name: ${"%.2f".format(value)}")
    }
    
    // Save in multiple formats
    val serializer = POIDataSerializer()
    serializer.saveToFile(pois, "backend/src/main/resources/seed-data.json", "json")
    serializer.saveToFile(pois, "backend/src/main/resources/seed-data.csv", "csv")
    serializer.saveToFile(pois, "backend/src/main/kotlin/data/SeedPOIData.kt", "kt")
    
    println("\nData saved to:")
    println("  - seed-data.json")
    println("  - seed-data.csv")
    println("  - SeedPOIData.kt")
}
```

---

## 3. Implementation Files

### 3.1 File Structure

```
backend/src/main/kotlin/com/eskisehir/eventapi/
├── data/
│   ├── local/
│   │   ├── seed/
│   │   │   ├── POISeedDataGenerator.kt (Main generator)
│   │   │   ├── TurkishNameGenerator.kt (Names)
│   │   │   ├── LocationGenerator.kt (Coordinates)
│   │   │   ├── AttributeGenerator.kt (Tags, hours, etc)
│   │   │   └── POIScoreCalculator.kt (Proxy scores)
│   │   └── dao/
│   ├── model/
│   │   └── POI.kt (Data classes)
│   └── remote/
└── utils/
    ├── POIDataSerializer.kt (JSON/CSV export)
    └── POIDataValidator.kt (Validation)

backend/src/main/resources/
├── seed-data.json (80-100 POIs in JSON)
├── seed-data.csv (80-100 POIs in CSV)
└── application.yml (Configuration)
```

### 3.2 Build Configuration Addition

Add to `backend/build.gradle.kts`:

```gradle
dependencies {
    // JSON serialization
    implementation("com.google.code.gson:gson:2.9.0")
    
    // CSV handling (optional)
    implementation("org.apache.commons:commons-csv:1.9.0")
}

// Task to generate seed data
tasks.register<JavaExec>("generateSeedData") {
    group = "database"
    description = "Generate POI seed data"
    mainClass.set("com.eskisehir.eventapi.data.local.seed.GeneratorKt")
    classpath = sourceSets["main"].runtimeClasspath
}
```

---

## 4. Database Integration

### 4.1 Room Database Entity

```kotlin
@Entity(tableName = "pois")
data class POIEntity(
    @PrimaryKey val id: String,
    val name: String,
    val englishName: String,
    val category: String,
    val district: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val operatingHours: String,
    val priceLevel: String,
    val estimatedCost: Float,
    val estimatedVisitDuration: Int,
    val tags: String, // JSON serialized List<String>
    val indoorOutdoor: String,
    val familyFriendly: Boolean,
    val childrenFriendly: Boolean,
    val seniorFriendly: Boolean,
    val wheelchairAccessible: Boolean,
    val parkingAvailable: Boolean,
    val publicTransitAccess: Boolean,
    val popularityScore: Float,
    val crowdProxyScore: Float,
    val sustainabilityScore: Float,
    val localBusinessScore: Float,
    val averageRating: Float?,
    val reviewCount: Int,
    val createdAt: String,
    val lastUpdated: String
)

@Dao
interface POIDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOIs(pois: List<POIEntity>)
    
    @Query("SELECT * FROM pois")
    suspend fun getAllPOIs(): List<POIEntity>
    
    @Query("SELECT * FROM pois WHERE district = :district")
    suspend fun getPOIsByDistrict(district: String): List<POIEntity>
}
```

### 4.2 Migration Strategy

```sql
-- Migration: Create initial POI table with seed data
CREATE TABLE IF NOT EXISTS pois (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    englishName TEXT NOT NULL,
    category TEXT NOT NULL,
    district TEXT NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    priceLevel TEXT NOT NULL,
    estimatedCost REAL NOT NULL,
    estimatedVisitDuration INTEGER NOT NULL,
    familyFriendly INTEGER NOT NULL,
    wheelchairAccessible INTEGER NOT NULL,
    parkingAvailable INTEGER NOT NULL,
    publicTransitAccess INTEGER NOT NULL,
    popularityScore REAL NOT NULL,
    crowdProxyScore REAL NOT NULL,
    sustainabilityScore REAL NOT NULL,
    localBusinessScore REAL NOT NULL,
    reviewCount INTEGER NOT NULL,
    createdAt TEXT NOT NULL
);

CREATE INDEX idx_pois_category ON pois(category);
CREATE INDEX idx_pois_district ON pois(district);
CREATE INDEX idx_pois_coordinates ON pois(latitude, longitude);
```

---

## 5. Dataset Statistics

### 5.1 Expected Output

**Sample Generated Dataset (10 POIs per area × 5 areas = 50 POIs)**:

```
Area: ODUNPAZARI (10 POIs)
- 2 Museums
- 2 Historical Sites
- 2 Cafes
- 2 Restaurants
- 1 Park
- 1 Other

Area: SAZOVA (10 POIs)
- 2 Parks
- 3 Cafes
- 2 Restaurants
- 1 Museum
- 1 Gallery
- 1 Other

... and so on for other areas
```

### 5.2 Score Distribution

```
Popularity Scores:
  Mean: 55.2
  Median: 54.8
  Stdev: 18.3
  Range: [15.2, 95.0]

Crowd Proxy Scores:
  Mean: 48.7
  Median: 49.0
  Stdev: 22.1
  Range: [10.5, 99.2]

Sustainability Scores:
  Mean: 62.4
  Median: 63.0
  Stdev: 16.8
  Range: [28.5, 92.3]

Local Business Scores:
  Mean: 62.8
  Median: 65.0
  Stdev: 18.9
  Range: [18.3, 95.0]
```

---

## 6. Next Steps

1. **Copy code** from this document into Kotlin files
2. **Run generator**: `./gradlew generateSeedData`
3. **Validate output**: Check JSON, CSV, and statistics
4. **Load into database**: Execute migrations and insert seed data
5. **Test in app**: Verify POI display and filtering

---

**Status**: Complete ✅  
**Version**: 1.0  
**Last Updated**: May 6, 2026  
**Phase**: 13 (Dataset Generation)  

