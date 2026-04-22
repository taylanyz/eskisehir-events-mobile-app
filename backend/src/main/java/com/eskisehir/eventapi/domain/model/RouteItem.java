package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * A single stop within a Route. Links a POI to a route with ordering and timing.
 */
@Entity
@Table(name = "route_items")
public class RouteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    @Column(nullable = false)
    private Integer visitOrder;

    private LocalTime estimatedArrival;
    private LocalTime estimatedDeparture;

    /** Distance in km from the previous stop (or starting point) */
    private Double distanceFromPreviousKm;

    public RouteItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Poi getPoi() { return poi; }
    public void setPoi(Poi poi) { this.poi = poi; }

    public Integer getVisitOrder() { return visitOrder; }
    public void setVisitOrder(Integer visitOrder) { this.visitOrder = visitOrder; }

    public LocalTime getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public LocalTime getEstimatedDeparture() { return estimatedDeparture; }
    public void setEstimatedDeparture(LocalTime estimatedDeparture) { this.estimatedDeparture = estimatedDeparture; }

    public Double getDistanceFromPreviousKm() { return distanceFromPreviousKm; }
    public void setDistanceFromPreviousKm(Double distanceFromPreviousKm) { this.distanceFromPreviousKm = distanceFromPreviousKm; }
}
