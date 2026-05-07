package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.*
import kotlin.random.Random

object AttributeGenerator {
    
    // Category-specific tags
    private val categoryTags = mapOf(
        POICategory.MUSEUM to listOf("historical", "educational", "cultural", "family-friendly"),
        POICategory.HISTORICAL_SITE to listOf("historical", "religious", "tourism", "photography"),
        POICategory.MOSQUE to listOf("religious", "historical", "ottoman", "spiritual"),
        POICategory.PARK to listOf("nature", "family-friendly", "walking", "photography", "picnic"),
        POICategory.RESTAURANT to listOf("dining", "local", "cuisine", "dinner", "lunch"),
        POICategory.CAFE to listOf("coffee", "social", "relaxing", "wifi", "study-friendly"),
        POICategory.BAKERY to listOf("fresh", "traditional", "local", "breakfast"),
        POICategory.SHOPPING_CENTER to listOf("shopping", "modern", "convenience", "dining"),
        POICategory.BAZAAR to listOf("shopping", "traditional", "local", "culture"),
        POICategory.CINEMA to listOf("entertainment", "movies", "social", "modern"),
        POICategory.LIBRARY to listOf("educational", "quiet", "learning", "research")
    )
    
    // Default address patterns by district
    private val addressPatterns = mapOf(
        District.ODUNPAZARI to listOf(
            "Kurşunlu Cami Sokak, Odunpazarı",
            "Porsuk Caddesi, Odunpazarı",
            "Sazova Yolu, Odunpazarı",
            "İç Çarşı, Odunpazarı"
        ),
        District.SAZOVA to listOf(
            "Sazova Parkı Yolu, Sazova",
            "Adalet Caddesi, Sazova",
            "Yeşil Orman Parkı, Sazova"
        ),
        District.YUNUSELI to listOf(
            "Atatürk Caddesi, Yunuseli",
            "Yenişehir Mahallesi, Yunuseli",
            "Kültür Sokak, Yunuseli"
        ),
        District.ESKISEHIR_CENTER to listOf(
            "Porsuk Caddesi, Merkez",
            "İnönü Caddesi, Merkez",
            "Kent Meydanı, Merkez"
        ),
        District.TEPEBASΙ to listOf(
            "Tepebaşı Caddesi, Tepebaşı",
            "Yeşil Alan, Tepebaşı",
            "Park Yolu, Tepebaşı"
        ),
        District.ALPASLAN to listOf(
            "Alpaslan Caddesi, Alpaslan",
            "Modern Mahallesi, Alpaslan",
            "Yeni Sokak, Alpaslan"
        ),
        District.HOŞNUDIYE to listOf(
            "Hoşnudiye Caddesi, Hoşnudiye",
            "Konut Mahallesi, Hoşnudiye",
            "Hizmet Sokak, Hoşnudiye"
        ),
        District.BAHÇELIEVLER to listOf(
            "Bahçe Caddesi, Bahçelievler",
            "Villa Sokak, Bahçelievler",
            "Yeşil Mahallesi, Bahçelievler"
        ),
        District.MIHALICILAR to listOf(
            "Mihalıcılar Caddesi, Mihalıcılar",
            "Kenar Yolu, Mihalıcılar",
            "Kırsal Sokak, Mihalıcılar"
        ),
        District.SITELER to listOf(
            "Siteler Caddesi, Siteler",
            "Endüstri Yolu, Siteler",
            "Ticari Sokak, Siteler"
        )
    )
    
    // Operating hours templates
    private val hoursTemplates = listOf(
        OperatingHours(
            monday = "09:00-18:00", tuesday = "09:00-18:00", wednesday = "09:00-18:00",
            thursday = "09:00-18:00", friday = "09:00-18:00", saturday = "10:00-18:00",
            sunday = "11:00-17:00"
        ),
        OperatingHours(
            monday = "08:00-17:00", tuesday = "08:00-17:00", wednesday = "08:00-17:00",
            thursday = "08:00-17:00", friday = "08:00-17:00", saturday = "09:00-17:00",
            sunday = null
        ),
        OperatingHours(
            monday = "10:00-22:00", tuesday = "10:00-22:00", wednesday = "10:00-22:00",
            thursday = "10:00-22:00", friday = "10:00-23:00", saturday = "10:00-23:00",
            sunday = "10:00-22:00"
        ),
        OperatingHours(
            monday = "06:00-22:00", tuesday = "06:00-22:00", wednesday = "06:00-22:00",
            thursday = "06:00-22:00", friday = "06:00-22:00", saturday = "06:00-23:00",
            sunday = "06:00-23:00"
        )
    )
    
    /**
     * Generate tags for a POI based on category
     */
    fun generateTags(category: POICategory, baseCount: Int = 3): List<String> {
        val tags = categoryTags[category]?.toMutableList() ?: mutableListOf("general", "local")
        val selected = tags.shuffled(Random).take(baseCount)
        return selected.ifEmpty { listOf("local", "tourism") }
    }
    
    /**
     * Generate a realistic address for a district
     */
    fun generateAddress(district: District): String {
        val patterns = addressPatterns[district] ?: listOf("Sokak, ${district.name}")
        return patterns.random(Random)
    }
    
    /**
     * Generate operating hours
     */
    fun generateOperatingHours(): OperatingHours {
        return hoursTemplates.random(Random)
    }
    
    /**
     * Generate accessibility features based on category and location type
     */
    fun generateAccessibilityFeatures(
        category: POICategory,
        locationType: LocationType
    ): AccessibilityFeatures {
        val random = Random
        
        // Museums and public spaces have higher accessibility
        val wheelchairAccessible = when (category) {
            POICategory.MUSEUM, POICategory.LIBRARY, POICategory.CINEMA,
            POICategory.SHOPPING_CENTER -> random.nextDouble() > 0.2
            else -> random.nextDouble() > 0.4
        }
        
        // Outdoor locations typically have transit access
        val publicTransitAccess = when (locationType) {
            LocationType.OUTDOOR, LocationType.MIXED -> random.nextDouble() > 0.3
            LocationType.INDOOR -> random.nextDouble() > 0.6
        }
        
        // Most places have parking
        val parkingAvailable = when (category) {
            POICategory.BAZAAR, POICategory.TRADITIONAL_MARKET -> random.nextDouble() > 0.6
            else -> random.nextDouble() > 0.3
        }
        
        return AccessibilityFeatures(
            wheelchairAccessible = wheelchairAccessible,
            publicTransitAccess = publicTransitAccess,
            parkingAvailable = parkingAvailable,
            restrooms = random.nextDouble() > 0.3,
            childFriendly = when (category) {
                POICategory.PARK, POICategory.MUSEUM, POICategory.ZOO -> true
                else -> random.nextDouble() > 0.5
            },
            seniorFriendly = random.nextDouble() > 0.4,
            petFriendly = when (category) {
                POICategory.PARK, POICategory.BAZAAR -> random.nextDouble() > 0.5
                else -> random.nextDouble() > 0.8
            }
        )
    }
    
    /**
     * Generate a realistic description for a POI
     */
    fun generateDescription(
        category: POICategory,
        turkishName: String,
        district: District
    ): Pair<String, String> {
        val turkishDesc = when (category) {
            POICategory.MUSEUM -> "Tarihi eserler ve kültürel mirasın sunulduğu müze. Ziyaretçilerin eğitim alması ve kültür edinmesi için ideal mekan."
            POICategory.HISTORICAL_SITE -> "Osmanlı ve Selçuk döneminden kalma tarihi yapı. Şehrin kültürel mirası için önemli bir lokasyon."
            POICategory.MOSQUE -> "İbadet amacıyla inşa edilmiş tarihi camii. Mimarisi ve iç dekorasyonu önemli tarihî özellikleri içerir."
            POICategory.PARK -> "Yeşil alanlar ve doğal güzelliklerin bulunduğu halk parkı. Aile ve arkadaşlarla zaman geçirmek için ideal."
            POICategory.RESTAURANT -> "Geleneksel Türk mutfağının sunulduğu, çeşitli yemeklerin pişirildiği restoran."
            POICategory.CAFE -> "Rahat ortamda kahve ve tatlı tüketebileceğiniz, sosyalleşme mekanı."
            POICategory.SHOPPING_CENTER -> "Çeşitli mağaza ve hizmetlerin bulunduğu modern alışveriş merkezi."
            POICategory.BAZAAR -> "Geleneksel El sanatlarının satıldığı, kültürel atmosferin hissedildiği pazaryeri."
            POICategory.LIBRARY -> "Kitap ve bilgi kaynakları içeren, okuma ve araştırma için tasarlanmış kütüphane."
            POICategory.CINEMA -> "Son teknoloji ekipmanlarıyla donatılmış film izleme mekanı."
            else -> "Bölgede önemli bir kültürel ve turist çekici mekan. Ziyaret edilmeye değer bir yer."
        }
        
        val englishDesc = when (category) {
            POICategory.MUSEUM -> "A museum showcasing historical artifacts and cultural heritage. Ideal for education and cultural appreciation."
            POICategory.HISTORICAL_SITE -> "Historic structure from Ottoman and Seljuk periods. Important location for city's cultural legacy."
            POICategory.MOSQUE -> "Historic mosque built for worship. Notable for its architecture and interior decoration."
            POICategory.PARK -> "Public park with green spaces and natural beauty. Ideal for spending time with family and friends."
            POICategory.RESTAURANT -> "Restaurant serving traditional Turkish cuisine with a variety of dishes."
            POICategory.CAFE -> "Comfortable venue for coffee and pastries with a social atmosphere."
            POICategory.SHOPPING_CENTER -> "Modern shopping mall with various stores and services."
            POICategory.BAZAAR -> "Traditional marketplace where handicrafts are sold with cultural atmosphere."
            POICategory.LIBRARY -> "Library with books and information resources designed for reading and research."
            POICategory.CINEMA -> "Modern cinema equipped with latest technology for film viewing."
            else -> "Important cultural and tourist attraction in the region. Worth visiting."
        }
        
        return Pair(turkishDesc, englishDesc)
    }
    
    /**
     * Generate estimated visit duration in minutes based on category
     */
    fun generateEstimatedDuration(category: POICategory): Int {
        return when (category) {
            POICategory.MUSEUM -> Random.nextInt(60, 180)  // 1-3 hours
            POICategory.HISTORICAL_SITE -> Random.nextInt(30, 90)  // 30 min - 1.5 hours
            POICategory.PARK -> Random.nextInt(45, 120)  // 45 min - 2 hours
            POICategory.CAFE -> Random.nextInt(30, 60)   // 30 min - 1 hour
            POICategory.RESTAURANT -> Random.nextInt(60, 120)  // 1-2 hours
            POICategory.SHOPPING_CENTER -> Random.nextInt(90, 180)  // 1.5-3 hours
            else -> Random.nextInt(30, 90)  // Default 30 min - 1.5 hours
        }
    }
}

/**
 * Usage Example:
 * 
 * val tags = AttributeGenerator.generateTags(POICategory.MUSEUM)
 * val address = AttributeGenerator.generateAddress(District.ODUNPAZARI)
 * val hours = AttributeGenerator.generateOperatingHours()
 * val accessibility = AttributeGenerator.generateAccessibilityFeatures(
 *     POICategory.MUSEUM, 
 *     LocationType.INDOOR
 * )
 */
