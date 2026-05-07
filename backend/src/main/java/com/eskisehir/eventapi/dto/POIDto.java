package com.eskisehir.eventapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * POI Data Transfer Object for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POIDto {
    private Long id;
    private String name;
    private String englishName;
    private String category;
    private String district;
    private Double latitude;
    private Double longitude;
    private String address;
    private String description;
    private String englishDescription;
    private String priceLevel;
    private Double estimatedCost;
    private Integer estimatedVisitDuration;
    private List<String> tags;
    private String locationType;
    
    // Accessibility
    private Boolean wheelchairAccessible;
    private Boolean publicTransitAccess;
    private Boolean childFriendly;
    private Boolean petFriendly;
    
    // Proxy scores
    private Float popularityScore;
    private Float crowdProxyScore;
    private Float sustainabilityScore;
    private Float localBusinessScore;
    private Float averageScore;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

