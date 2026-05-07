package com.eskisehir.eventapp.data.local.converters

import androidx.room.TypeConverter
import com.eskisehir.eventapp.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type Converters for POI complex types in Room Database
 * Converts complex objects to/from JSON for storage
 */
class POIConverters {
    
    private val gson = Gson()
    
    /**
     * Convert POICategory enum to String
     */
    @TypeConverter
    fun fromPOICategory(category: POICategory): String {
        return category.name
    }
    
    @TypeConverter
    fun toPOICategory(value: String): POICategory {
        return POICategory.valueOf(value)
    }
    
    /**
     * Convert District enum to String
     */
    @TypeConverter
    fun fromDistrict(district: District): String {
        return district.name
    }
    
    @TypeConverter
    fun toDistrict(value: String): District {
        return District.valueOf(value)
    }
    
    /**
     * Convert PriceLevel enum to String
     */
    @TypeConverter
    fun fromPriceLevel(level: PriceLevel): String {
        return level.name
    }
    
    @TypeConverter
    fun toPriceLevel(value: String): PriceLevel {
        return PriceLevel.valueOf(value)
    }
    
    /**
     * Convert LocationType enum to String
     */
    @TypeConverter
    fun fromLocationType(type: LocationType): String {
        return type.name
    }
    
    @TypeConverter
    fun toLocationType(value: String): LocationType {
        return LocationType.valueOf(value)
    }
    
    /**
     * Convert OperatingHours to JSON
     */
    @TypeConverter
    fun fromOperatingHours(hours: OperatingHours?): String? {
        return if (hours == null) null else gson.toJson(hours)
    }
    
    @TypeConverter
    fun toOperatingHours(json: String?): OperatingHours? {
        return if (json == null) null else gson.fromJson(json, OperatingHours::class.java)
    }
    
    /**
     * Convert AccessibilityFeatures to JSON
     */
    @TypeConverter
    fun fromAccessibilityFeatures(features: AccessibilityFeatures?): String? {
        return if (features == null) null else gson.toJson(features)
    }
    
    @TypeConverter
    fun toAccessibilityFeatures(json: String?): AccessibilityFeatures? {
        return if (json == null) null else gson.fromJson(json, AccessibilityFeatures::class.java)
    }
    
    /**
     * Convert ContactInfo to JSON
     */
    @TypeConverter
    fun fromContactInfo(info: ContactInfo?): String? {
        return if (info == null) null else gson.toJson(info)
    }
    
    @TypeConverter
    fun toContactInfo(json: String?): ContactInfo? {
        return if (json == null) null else gson.fromJson(json, ContactInfo::class.java)
    }
    
    /**
     * Convert ProxyScores to JSON
     */
    @TypeConverter
    fun fromProxyScores(scores: ProxyScores?): String? {
        return if (scores == null) null else gson.toJson(scores)
    }
    
    @TypeConverter
    fun toProxyScores(json: String?): ProxyScores? {
        return if (json == null) null else gson.fromJson(json, ProxyScores::class.java)
    }
    
    /**
     * Convert List<String> (tags) to/from comma-separated string
     */
    @TypeConverter
    fun fromTagList(tags: List<String>?): String? {
        return if (tags == null) null else gson.toJson(tags)
    }
    
    @TypeConverter
    fun toTagList(json: String?): List<String>? {
        return if (json == null) null else gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}
