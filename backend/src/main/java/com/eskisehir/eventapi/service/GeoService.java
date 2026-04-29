package com.eskisehir.eventapi.service;

import org.springframework.stereotype.Service;

/**
 * Geographic service for distance and time calculations.
 * Uses Haversine formula for great-circle distance.
 * Walking time estimated at ~1.4 m/s (typical walking speed).
 */
@Service
public class GeoService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double WALKING_SPEED_KMH = 5.0;  // km/h typical walking speed
    private static final double WALKING_SPEED_MPS = WALKING_SPEED_KMH / 3.6;  // m/s

    /**
     * Calculate distance between two coordinates using Haversine formula.
     * @param lat1 Starting latitude
     * @param lng1 Starting longitude
     * @param lat2 Ending latitude
     * @param lng2 Ending longitude
     * @return distance in kilometers
     */
    public double getDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimate walking time between two points.
     * @param distanceKm distance in kilometers
     * @return estimated walking time in minutes
     */
    public int getWalkingTimeMinutes(double distanceKm) {
        double distanceMeters = distanceKm * 1000;
        double timeSeconds = distanceMeters / WALKING_SPEED_MPS;
        return (int) Math.ceil(timeSeconds / 60.0);
    }

    /**
     * Get walking time between two coordinates.
     * @return estimated walking time in minutes
     */
    public int getWalkingTimeMinutes(double lat1, double lng1, double lat2, double lng2) {
        double distance = getDistanceKm(lat1, lng1, lat2, lng2);
        return getWalkingTimeMinutes(distance);
    }
}
