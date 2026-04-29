package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Data Transfer Object for POI API responses.
 */
public class PoiResponse {

    private Long id;
    private String name;
    private String description;
    private Category category;
    private String district;
    private Double latitude;
    private Double longitude;
    private String venue;
    private LocalDateTime date;
    private Double price;
    private BudgetLevel budgetLevel;
    private String imageUrl;
    private List<String> tags;
    private Integer estimatedVisitMinutes;
    private IndoorOutdoor indoorOutdoor;
    private Boolean familyFriendly;
    private Double sustainabilityScore;
    private Double localBusinessScore;
    private Double crowdProxy;
    private Double popularityScore;
    private Double rankingScore;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private WeatherDto weather;

    public PoiResponse() {}

    public static PoiResponse fromEntity(Poi poi) {
        return fromEntity(poi, null);
    }

    public static PoiResponse fromEntity(Poi poi, Double rankingScore) {
        PoiResponse r = new PoiResponse();
        r.setId(poi.getId());
        r.setName(poi.getName());
        r.setDescription(poi.getDescription());
        r.setCategory(poi.getCategory());
        r.setDistrict(poi.getDistrict());
        r.setLatitude(poi.getLatitude());
        r.setLongitude(poi.getLongitude());
        r.setVenue(poi.getVenue());
        r.setDate(poi.getDate());
        r.setPrice(poi.getPrice());
        r.setBudgetLevel(poi.getBudgetLevel());
        r.setImageUrl(poi.getImageUrl());
        r.setTags(poi.getTags());
        r.setEstimatedVisitMinutes(poi.getEstimatedVisitMinutes());
        r.setIndoorOutdoor(poi.getIndoorOutdoor());
        r.setFamilyFriendly(poi.getFamilyFriendly());
        r.setSustainabilityScore(poi.getSustainabilityScore());
        r.setLocalBusinessScore(poi.getLocalBusinessScore());
        r.setCrowdProxy(poi.getCrowdProxy());
        r.setPopularityScore(poi.getPopularityScore());
        r.setRankingScore(rankingScore);
        r.setOpeningTime(poi.getOpeningTime());
        r.setClosingTime(poi.getClosingTime());
        return r;
    }

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
    public Double getRankingScore() { return rankingScore; }
    public void setRankingScore(Double rankingScore) { this.rankingScore = rankingScore; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public WeatherDto getWeather() { return weather; }
    public void setWeather(WeatherDto weather) { this.weather = weather; }
}
