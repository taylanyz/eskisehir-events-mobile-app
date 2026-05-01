package com.eskisehir.eventapi.dto;

import java.util.List;

/**
 * Represents a complete optimized route with metrics and scores.
 */
public class OptimizedRoute {
    private String routeId;
    private RouteLocation origin;
    private List<RouteStop> pois;
    private RouteMetrics metrics;
    private RouteScoreBreakdown scores;
    private String explanation;
    
    public OptimizedRoute() {
    }
    
    public OptimizedRoute(
        String routeId,
        RouteLocation origin,
        List<RouteStop> pois,
        RouteMetrics metrics,
        RouteScoreBreakdown scores,
        String explanation
    ) {
        this.routeId = routeId;
        this.origin = origin;
        this.pois = pois;
        this.metrics = metrics;
        this.scores = scores;
        this.explanation = explanation;
    }
    
    // Getters and setters
    public String getRouteId() {
        return routeId;
    }
    
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    
    public RouteLocation getOrigin() {
        return origin;
    }
    
    public void setOrigin(RouteLocation origin) {
        this.origin = origin;
    }
    
    public List<RouteStop> getPois() {
        return pois;
    }
    
    public void setPois(List<RouteStop> pois) {
        this.pois = pois;
    }
    
    public RouteMetrics getMetrics() {
        return metrics;
    }
    
    public void setMetrics(RouteMetrics metrics) {
        this.metrics = metrics;
    }
    
    public RouteScoreBreakdown getScores() {
        return scores;
    }
    
    public void setScores(RouteScoreBreakdown scores) {
        this.scores = scores;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    // Inner class for route location
    public static class RouteLocation {
        private String name;
        private Double lat;
        private Double lng;
        
        public RouteLocation() {
        }
        
        public RouteLocation(String name, Double lat, Double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Double getLat() {
            return lat;
        }
        
        public void setLat(Double lat) {
            this.lat = lat;
        }
        
        public Double getLng() {
            return lng;
        }
        
        public void setLng(Double lng) {
            this.lng = lng;
        }
    }
    
    // Inner class for route stop
    public static class RouteStop {
        private Long poiId;
        private String name;
        private Integer order;
        private Integer visitDurationMinutes;
        private Double costTl;
        private Double crowdLevel;
        private String category;
        
        public RouteStop() {
        }
        
        public RouteStop(
            Long poiId,
            String name,
            Integer order,
            Integer visitDurationMinutes,
            Double costTl,
            Double crowdLevel,
            String category
        ) {
            this.poiId = poiId;
            this.name = name;
            this.order = order;
            this.visitDurationMinutes = visitDurationMinutes;
            this.costTl = costTl;
            this.crowdLevel = crowdLevel;
            this.category = category;
        }
        
        public Long getPoiId() {
            return poiId;
        }
        
        public void setPoiId(Long poiId) {
            this.poiId = poiId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Integer getOrder() {
            return order;
        }
        
        public void setOrder(Integer order) {
            this.order = order;
        }
        
        public Integer getVisitDurationMinutes() {
            return visitDurationMinutes;
        }
        
        public void setVisitDurationMinutes(Integer visitDurationMinutes) {
            this.visitDurationMinutes = visitDurationMinutes;
        }
        
        public Double getCostTl() {
            return costTl;
        }
        
        public void setCostTl(Double costTl) {
            this.costTl = costTl;
        }
        
        public Double getCrowdLevel() {
            return crowdLevel;
        }
        
        public void setCrowdLevel(Double crowdLevel) {
            this.crowdLevel = crowdLevel;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
}
