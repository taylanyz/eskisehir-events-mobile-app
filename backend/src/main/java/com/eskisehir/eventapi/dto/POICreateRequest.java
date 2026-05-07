package com.eskisehir.eventapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Create/Update POI Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POICreateRequest {
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
    private Boolean wheelchairAccessible;
    private Boolean publicTransitAccess;
    private Boolean childFriendly;
    private Boolean petFriendly;
}
