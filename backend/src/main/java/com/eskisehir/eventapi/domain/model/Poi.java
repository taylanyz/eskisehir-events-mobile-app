package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * JPA Entity representing a Point of Interest (POI) in Eskişehir.
 *
 * A POI can be an event (concert, festival), a venue (museum, cafe),
 * or a landmark (historical site, park). This unified model supports
 * both time-bound events and permanent attractions.
 *
 * Scoring fields (sustainabilityScore, crowdProxy, popularityScore, localBusinessScore)
 * are normalized to [0, 1] for use in recommendation and route optimization.
 */
@Entity
@Table(name = "pois")
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    /** District within Eskişehir (e.g., "Odunpazarı", "Tepebaşı") */
    private String district;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /** Venue or place name */
    @Column(nullable = false)
    private String venue;

    /** For events: when it occurs. For permanent POIs: null. */
    private LocalDateTime date;

    /** Raw price in TRY. Use budgetLevel for categorical filtering. */
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetLevel budgetLevel;

    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "poi_tags", joinColumns = @JoinColumn(name = "poi_id"))
    @Column(name = "tag")
    private List<String> tags;

    /** Estimated visit duration in minutes */
    private Integer estimatedVisitMinutes;

    @Enumerated(EnumType.STRING)
    private IndoorOutdoor indoorOutdoor;

    private Boolean familyFriendly;

    /** Sustainability score [0, 1]: eco-friendly, walkable, local materials, etc. */
    @Column(columnDefinition = "DOUBLE DEFAULT 0.5")
    private Double sustainabilityScore;

    /** Local business score [0, 1]: locally owned, supports local economy */
    @Column(columnDefinition = "DOUBLE DEFAULT 0.5")
    private Double localBusinessScore;

    /** Crowd proxy [0, 1]: 0 = empty, 1 = very crowded (estimated) */
    @Column(columnDefinition = "DOUBLE DEFAULT 0.5")
    private Double crowdProxy;

    /** Popularity score [0, 1]: based on reviews, visits, social media */
    @Column(columnDefinition = "DOUBLE DEFAULT 0.5")
    private Double popularityScore;

    /** Opening time (for permanent POIs). Null = always open or event-based. */
    private LocalTime openingTime;

    /** Closing time (for permanent POIs). */
    private LocalTime closingTime;

    /** Whether this POI is currently active/visible */
    @Column(nullable = false)
    private Boolean isActive = true;

    public Poi() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public BudgetLevel getBudgetLevel() { return budgetLevel; }
    public void setBudgetLevel(BudgetLevel budgetLevel) { this.budgetLevel = budgetLevel; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Integer getEstimatedVisitMinutes() { return estimatedVisitMinutes; }
    public void setEstimatedVisitMinutes(Integer estimatedVisitMinutes) { this.estimatedVisitMinutes = estimatedVisitMinutes; }

    public IndoorOutdoor getIndoorOutdoor() { return indoorOutdoor; }
    public void setIndoorOutdoor(IndoorOutdoor indoorOutdoor) { this.indoorOutdoor = indoorOutdoor; }

    public Boolean getFamilyFriendly() { return familyFriendly; }
    public void setFamilyFriendly(Boolean familyFriendly) { this.familyFriendly = familyFriendly; }

    public Double getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(Double sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }

    public Double getLocalBusinessScore() { return localBusinessScore; }
    public void setLocalBusinessScore(Double localBusinessScore) { this.localBusinessScore = localBusinessScore; }

    public Double getCrowdProxy() { return crowdProxy; }
    public void setCrowdProxy(Double crowdProxy) { this.crowdProxy = crowdProxy; }

    public Double getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Double popularityScore) { this.popularityScore = popularityScore; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
