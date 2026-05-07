package com.eskisehir.eventapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POI Entity for PostgreSQL
 * Represents Points of Interest in Eskişehir
 */
@Entity
@Table(name = "poi", indexes = {
    @Index(name = "idx_poi_district", columnList = "district"),
    @Index(name = "idx_poi_category", columnList = "category"),
    @Index(name = "idx_poi_location", columnList = "latitude, longitude"),
    @Index(name = "idx_poi_popularity", columnList = "popularity_score")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POI {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    // Core Identification
    @Column(name = "name", nullable = false, length = 255)
    private String name;  // Turkish name
    
    @Column(name = "english_name", length = 255)
    private String englishName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private POICategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "district", nullable = false)
    private District district;
    
    // Location (6 decimal precision = ~0.1m accuracy)
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    // Description
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "english_description", columnDefinition = "TEXT")
    private String englishDescription;
    
    // Operations
    @Column(name = "operating_hours", columnDefinition = "jsonb")
    private String operatingHours;  // JSON: {"monday":"09:00-18:00", ...}
    
    // Pricing
    @Enumerated(EnumType.STRING)
    @Column(name = "price_level")
    private PriceLevel priceLevel;
    
    @Column(name = "estimated_cost")
    private Float estimatedCost;  // in TL
    
    @Column(name = "estimated_visit_duration")
    private Integer estimatedVisitDuration;  // in minutes
    
    // Classification
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;  // Comma-separated or JSON array
    
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type")
    private LocationType locationType;
    
    // Accessibility
    @Column(name = "wheelchair_accessible")
    private Boolean wheelchairAccessible = false;
    
    @Column(name = "public_transit_access")
    private Boolean publicTransitAccess = false;
    
    @Column(name = "parking_available")
    private Boolean parkingAvailable = false;
    
    @Column(name = "restrooms")
    private Boolean restrooms = false;
    
    @Column(name = "child_friendly")
    private Boolean childFriendly = false;
    
    @Column(name = "senior_friendly")
    private Boolean seniorFriendly = false;
    
    @Column(name = "pet_friendly")
    private Boolean petFriendly = false;
    
    // Contact
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "website", length = 500)
    private String website;
    
    @Column(name = "instagram", length = 100)
    private String instagram;
    
    // Proxy Scores (0-100 scale)
    @Column(name = "popularity_score")
    private Float popularityScore = 0f;
    
    @Column(name = "crowd_proxy_score")
    private Float crowdProxyScore = 0f;
    
    @Column(name = "sustainability_score")
    private Float sustainabilityScore = 0f;
    
    @Column(name = "local_business_score")
    private Float localBusinessScore = 0f;
    
    // Average of all proxy scores for easy sorting/filtering
    @Column(name = "average_score")
    private Float averageScore = 0f;
    
    // Metadata
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "data_source_notes", length = 500)
    private String dataSourceNotes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateAverageScore();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAverageScore();
    }
    
    private void calculateAverageScore() {
        int count = 0;
        float sum = 0f;
        
        if (popularityScore != null && popularityScore > 0) {
            sum += popularityScore;
            count++;
        }
        if (crowdProxyScore != null && crowdProxyScore > 0) {
            sum += crowdProxyScore;
            count++;
        }
        if (sustainabilityScore != null && sustainabilityScore > 0) {
            sum += sustainabilityScore;
            count++;
        }
        if (localBusinessScore != null && localBusinessScore > 0) {
            sum += localBusinessScore;
            count++;
        }
        
        this.averageScore = count > 0 ? sum / count : 0f;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public POICategory getCategory() { return category; }
    public void setCategory(POICategory category) { this.category = category; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEnglishDescription() { return englishDescription; }
    public void setEnglishDescription(String englishDescription) { this.englishDescription = englishDescription; }
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    public PriceLevel getPriceLevel() { return priceLevel; }
    public void setPriceLevel(PriceLevel priceLevel) { this.priceLevel = priceLevel; }
    public Float getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Float estimatedCost) { this.estimatedCost = estimatedCost; }
    public Integer getEstimatedVisitDuration() { return estimatedVisitDuration; }
    public void setEstimatedVisitDuration(Integer estimatedVisitDuration) { this.estimatedVisitDuration = estimatedVisitDuration; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocationType getLocationType() { return locationType; }
    public void setLocationType(LocationType locationType) { this.locationType = locationType; }
    public Boolean getWheelchairAccessible() { return wheelchairAccessible; }
    public void setWheelchairAccessible(Boolean wheelchairAccessible) { this.wheelchairAccessible = wheelchairAccessible; }
    public Boolean getPublicTransitAccess() { return publicTransitAccess; }
    public void setPublicTransitAccess(Boolean publicTransitAccess) { this.publicTransitAccess = publicTransitAccess; }
    public Boolean getParkingAvailable() { return parkingAvailable; }
    public void setParkingAvailable(Boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }
    public Boolean getRestrooms() { return restrooms; }
    public void setRestrooms(Boolean restrooms) { this.restrooms = restrooms; }
    public Boolean getChildFriendly() { return childFriendly; }
    public void setChildFriendly(Boolean childFriendly) { this.childFriendly = childFriendly; }
    public Boolean getSeniorFriendly() { return seniorFriendly; }
    public void setSeniorFriendly(Boolean seniorFriendly) { this.seniorFriendly = seniorFriendly; }
    public Boolean getPetFriendly() { return petFriendly; }
    public void setPetFriendly(Boolean petFriendly) { this.petFriendly = petFriendly; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public Float getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Float popularityScore) { this.popularityScore = popularityScore; }
    public Float getCrowdProxyScore() { return crowdProxyScore; }
    public void setCrowdProxyScore(Float crowdProxyScore) { this.crowdProxyScore = crowdProxyScore; }
    public Float getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(Float sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
    public Float getLocalBusinessScore() { return localBusinessScore; }
    public void setLocalBusinessScore(Float localBusinessScore) { this.localBusinessScore = localBusinessScore; }
    public Float getAverageScore() { return averageScore; }
    public void setAverageScore(Float averageScore) { this.averageScore = averageScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getDataSourceNotes() { return dataSourceNotes; }
    public void setDataSourceNotes(String dataSourceNotes) { this.dataSourceNotes = dataSourceNotes; }
    
    public enum POICategory {
        MUSEUM, HISTORICAL_SITE, CULTURAL_CENTER, ART_GALLERY,
        MOSQUE, CHURCH, SYNAGOGUE, TEMPLE,
        PARK, GARDEN, NATURE_RESERVE, ZOO,
        RESTAURANT, CAFE, BAKERY, TRADITIONAL_MARKET,
        SHOPPING_CENTER, BAZAAR, ANTIQUE_SHOP, BOOKSTORE,
        CINEMA, THEATER, SPORTS_FACILITY, SWIMMING_POOL,
        LIBRARY, UNIVERSITY, EDUCATIONAL_INSTITUTION,
        LANDMARK, SCENIC_VIEWPOINT, OTHER
    }
    
    public enum District {
        ODUNPAZARI, SAZOVA, YUNUSELI, ESKISEHIR_CENTER,
        TEPEBASΙ, ALPASLAN, HOŞNUDIYE, BAHÇELIEVLER,
        MIHALICILAR, SITELER
    }
    
    public enum PriceLevel {
        FREE, BUDGET, MODERATE, EXPENSIVE, LUXURY
    }
    
    public enum LocationType {
        INDOOR, OUTDOOR, MIXED
    }
}
