package com.eskisehir.eventapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Geographic bounds DTO for location-based queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeographicBoundsDto {
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;
    private List<POIDto> pois;
}
