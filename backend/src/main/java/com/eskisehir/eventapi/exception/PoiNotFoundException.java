package com.eskisehir.eventapi.exception;

/**
 * Exception thrown when a POI with a given ID is not found.
 * Handled globally by {@link GlobalExceptionHandler} to return a 404 response.
 */
public class PoiNotFoundException extends RuntimeException {

    public PoiNotFoundException(Long id) {
        super("POI not found with id: " + id);
    }
}
