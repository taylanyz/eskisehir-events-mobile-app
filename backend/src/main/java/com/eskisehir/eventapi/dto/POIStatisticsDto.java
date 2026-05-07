package com.eskisehir.eventapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * POI Statistics Response DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POIStatisticsDto {
    private Long totalPOIs;
    private Integer totalCategories;
    private Integer totalDistricts;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> districtDistribution;
    private Float averagePopularityScore;
    private Float averageCrowdScore;
    private Float averageSustainabilityScore;
    private Float averageLocalBusinessScore;
    private Integer wheelchairAccessibleCount;
    private Integer childFriendlyCount;
    private Integer freeCount;
    
    // Getters and Setters
    public Long getTotalPOIs() { return totalPOIs; }
    public void setTotalPOIs(Long totalPOIs) { this.totalPOIs = totalPOIs; }
    public void setTotalPOIs(long totalPOIs) { this.totalPOIs = totalPOIs; }
    
    public Integer getTotalCategories() { return totalCategories; }
    public void setTotalCategories(Integer totalCategories) { this.totalCategories = totalCategories; }
    public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }
    
    public Integer getTotalDistricts() { return totalDistricts; }
    public void setTotalDistricts(Integer totalDistricts) { this.totalDistricts = totalDistricts; }
    public void setTotalDistricts(int totalDistricts) { this.totalDistricts = totalDistricts; }
    
    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
    
    public Map<String, Long> getDistrictDistribution() { return districtDistribution; }
    public void setDistrictDistribution(Map<String, Long> districtDistribution) { this.districtDistribution = districtDistribution; }
    
    public Float getAveragePopularityScore() { return averagePopularityScore; }
    public void setAveragePopularityScore(Float averagePopularityScore) { this.averagePopularityScore = averagePopularityScore; }
    public void setAveragePopularityScore(float averagePopularityScore) { this.averagePopularityScore = averagePopularityScore; }
    
    public Float getAverageCrowdScore() { return averageCrowdScore; }
    public void setAverageCrowdScore(Float averageCrowdScore) { this.averageCrowdScore = averageCrowdScore; }
    public void setAverageCrowdScore(float averageCrowdScore) { this.averageCrowdScore = averageCrowdScore; }
    
    public Float getAverageSustainabilityScore() { return averageSustainabilityScore; }
    public void setAverageSustainabilityScore(Float averageSustainabilityScore) { this.averageSustainabilityScore = averageSustainabilityScore; }
    public void setAverageSustainabilityScore(float averageSustainabilityScore) { this.averageSustainabilityScore = averageSustainabilityScore; }
    
    public Float getAverageLocalBusinessScore() { return averageLocalBusinessScore; }
    public void setAverageLocalBusinessScore(Float averageLocalBusinessScore) { this.averageLocalBusinessScore = averageLocalBusinessScore; }
    public void setAverageLocalBusinessScore(float averageLocalBusinessScore) { this.averageLocalBusinessScore = averageLocalBusinessScore; }
    
    public Integer getWheelchairAccessibleCount() { return wheelchairAccessibleCount; }
    public void setWheelchairAccessibleCount(Integer wheelchairAccessibleCount) { this.wheelchairAccessibleCount = wheelchairAccessibleCount; }
    public void setWheelchairAccessibleCount(int wheelchairAccessibleCount) { this.wheelchairAccessibleCount = wheelchairAccessibleCount; }
    
    public Integer getChildFriendlyCount() { return childFriendlyCount; }
    public void setChildFriendlyCount(Integer childFriendlyCount) { this.childFriendlyCount = childFriendlyCount; }
    public void setChildFriendlyCount(int childFriendlyCount) { this.childFriendlyCount = childFriendlyCount; }
    
    public Integer getFreeCount() { return freeCount; }
    public void setFreeCount(Integer freeCount) { this.freeCount = freeCount; }
    public void setFreeCount(int freeCount) { this.freeCount = freeCount; }
}
