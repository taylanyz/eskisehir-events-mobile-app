package com.eskisehir.eventapp.data.generator

import com.eskisehir.eventapp.data.model.POICategory
import kotlin.random.Random

object TurkishNameGenerator {
    
    // Turkish POI Names grouped by category (40+ entries)
    private val turkishNames = mapOf(
        POICategory.MUSEUM to listOf(
            Pair("Anadolu Medeniyetleri Müzesi", "Anatolian Civilizations Museum"),
            Pair("Arkeoloji Müzesi", "Archaeology Museum"),
            Pair("Sanat Müzesi", "Art Museum"),
            Pair("Çağdaş Sanatlar Müzesi", "Contemporary Arts Museum")
        ),
        POICategory.HISTORICAL_SITE to listOf(
            Pair("Beyazıt Camii", "Beyazit Mosque"),
            Pair("Kurşunlu Camii", "Kurşunlu Mosque"),
            Pair("Hidırküp Camii", "Hidırküp Mosque"),
            Pair("Porsuk Vadi Tarihi Sit", "Porsuk Valley Historic Site")
        ),
        POICategory.CULTURAL_CENTER to listOf(
            Pair("Kültür ve Turizm Merkezi", "Culture and Tourism Center"),
            Pair("Halkevi", "People's Hall"),
            Pair("Sanat Sarayı", "Art Palace"),
            Pair("Müzik Akademisi", "Music Academy")
        ),
        POICategory.ART_GALLERY to listOf(
            Pair("Modern Sanat Galerisi", "Modern Art Gallery"),
            Pair("Çağdaş Eserler Galerisi", "Contemporary Works Gallery"),
            Pair("Ressam Atölyesi", "Artist Studio"),
            Pair("Sanat Merkezi", "Art Center")
        ),
        POICategory.MOSQUE to listOf(
            Pair("Kurşunlu Camii", "Kurşunlu Mosque"),
            Pair("Beyazıt Camii", "Beyazit Mosque"),
            Pair("Hidırküp Camii", "Hidırküp Mosque"),
            Pair("Sultan Camii", "Sultan Mosque"),
            Pair("Merkez Camii", "Central Mosque")
        ),
        POICategory.PARK to listOf(
            Pair("Sazova Parkı", "Sazova Park"),
            Pair("Adalet Parkı", "Justice Park"),
            Pair("Yeşil Orman Parkı", "Green Forest Park"),
            Pair("Kent Parkı", "City Park"),
            Pair("Çiçek Bahçesi", "Flower Garden")
        ),
        POICategory.RESTAURANT to listOf(
            Pair("Geleneksel Türk Mutfağı", "Traditional Turkish Cuisine"),
            Pair("Kebab Evi", "Kebab House"),
            Pair("Usta Pideci", "Master Pita Baker"),
            Pair("Çorbacı Ali", "Ali the Soup Master"),
            Pair("Zarif Restoran", "Elegant Restaurant"),
            Pair("Aile Sofrası", "Family Table"),
            Pair("Lezzetli Lokanta", "Delicious Eatery")
        ),
        POICategory.CAFE to listOf(
            Pair("Kahve Sahnesi", "Coffee Scene"),
            Pair("Espresso Köşesi", "Espresso Corner"),
            Pair("Türk Kahvesi Evi", "Turkish Coffee House"),
            Pair("Çay Bahçesi", "Tea Garden"),
            Pair("Modern Kafe", "Modern Cafe"),
            Pair("Rahat Kahvesi", "Comfortable Coffee"),
            Pair("Sakin Mekan", "Peaceful Place")
        ),
        POICategory.BAKERY to listOf(
            Pair("Fırıncı Ahmet", "Baker Ahmet"),
            Pair("Ekmek Fırını", "Bread Bakery"),
            Pair("Pastane Ustası", "Pastry Master"),
            Pair("Taze Ekmek", "Fresh Bread"),
            Pair("Simit Sarayı", "Simit Palace")
        ),
        POICategory.TRADITIONAL_MARKET to listOf(
            Pair("Bedesten Pazarı", "Bedesten Market"),
            Pair("İç Çarşı", "Inner Bazaar"),
            Pair("Pazar Alanı", "Market Area"),
            Pair("Halk Pazarı", "People's Market"),
            Pair("Porsuk Pazarı", "Porsuk Market")
        ),
        POICategory.SHOPPING_CENTER to listOf(
            Pair("Modern Alışveriş Merkezi", "Modern Shopping Mall"),
            Pair("Kent Plaza", "City Plaza"),
            Pair("Ticaret Merkezi", "Trade Center"),
            Pair("Alışveriş Sarayı", "Shopping Palace"),
            Pair("Mağaza Kompleksi", "Store Complex")
        ),
        POICategory.BAZAAR to listOf(
            Pair("Bedesten", "Bedesten Bazaar"),
            Pair("Kapalı Çarşı", "Covered Bazaar"),
            Pair("Pazarcı Mahallesi", "Merchant Quarter"),
            Pair("Antika Pazarı", "Antique Bazaar"),
            Pair("Pazarbaşı", "Market Head")
        ),
        POICategory.CINEMA to listOf(
            Pair("Sinema Kompleksi", "Cinema Complex"),
            Pair("Film Sarayı", "Film Palace"),
            Pair("Ses Görüntü Evi", "Audio Visual House"),
            Pair("Sanat Sineması", "Art Cinema")
        ),
        POICategory.LIBRARY to listOf(
            Pair("Şehir Kütüphanesi", "City Library"),
            Pair("İl Kütüphanesi", "Provincial Library"),
            Pair("Kitap Evi", "Book House"),
            Pair("Halk Kütüphanesi", "People's Library")
        ),
        POICategory.SPORTS_FACILITY to listOf(
            Pair("Spor Kompleksi", "Sports Complex"),
            Pair("Gym Merkezi", "Gym Center"),
            Pair("Tenis Kortu", "Tennis Court"),
            Pair("Futbol Sahanı", "Football Field")
        ),
        POICategory.LANDMARK to listOf(
            Pair("Meydana", "Town Square"),
            Pair("Anıt Yer", "Monument Site"),
            Pair("Tarihi Yapı", "Historic Building"),
            Pair("Şehir Simgesi", "City Symbol")
        ),
        POICategory.SCENIC_VIEWPOINT to listOf(
            Pair("Manzara Noktası", "Viewpoint"),
            Pair("Tepeden Manzara", "View from Hill"),
            Pair("Göz Alıcı Yer", "Scenic Spot"),
            Pair("Fotoğraf Noktası", "Photo Point")
        )
    )
    
    /**
     * Generate a random POI name and English translation for a given category
     */
    fun generateName(category: POICategory): Pair<String, String> {
        val names = turkishNames[category] ?: listOf(
            Pair("Mekan", "Place"),
            Pair("Lokasyon", "Location")
        )
        return names.random(Random)
    }
    
    /**
     * Generate multiple random names for a category
     */
    fun generateNames(category: POICategory, count: Int): List<Pair<String, String>> {
        val names = turkishNames[category] ?: emptyList()
        return names.shuffled(Random).take(count)
    }
    
    /**
     * Get all available names for a category
     */
    fun getAvailableNames(category: POICategory): List<Pair<String, String>> {
        return turkishNames[category] ?: emptyList()
    }
    
    /**
     * Get total count of names across all categories
     */
    fun getTotalNameCount(): Int {
        return turkishNames.values.sumOf { it.size }
    }
    
    /**
     * Get all unique Turkish names (for validation/deduplication)
     */
    fun getAllTurkishNames(): Set<String> {
        return turkishNames.values.flatten().map { it.first }.toSet()
    }
}

/**
 * Usage Example:
 * 
 * val museumName = TurkishNameGenerator.generateName(POICategory.MUSEUM)
 * println("Turkish: ${museumName.first}, English: ${museumName.second}")
 * // Output: Turkish: Anadolu Medeniyetleri Müzesi, English: Anatolian Civilizations Museum
 */
