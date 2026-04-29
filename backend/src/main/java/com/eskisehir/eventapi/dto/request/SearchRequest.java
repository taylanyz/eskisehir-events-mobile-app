package com.eskisehir.eventapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * POI search request DTO.
 */
@Data
public class SearchRequest {

    private String query;

    private Integer page;

    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(100)
    private Integer pageSize;

    private String category;

    private String sortBy; // "name", "rating", "distance", "popularity"

    public SearchRequest() {
        this.page = 0;
        this.pageSize = 20;
    }
}
