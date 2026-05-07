package com.eskisehir.eventapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POI Search/Filter Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class POISearchRequest {
    private String searchTerm;
    private String category;
    private String district;
    private String sortBy;  // "popularity", "crowd", "sustainability", "local-business"
    private Boolean sortAsc;
    private Integer pageNumber;
    private Integer pageSize;
}
