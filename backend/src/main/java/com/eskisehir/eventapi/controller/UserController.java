package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.PreferenceUpdateRequest;
import com.eskisehir.eventapi.dto.UserResponse;
import com.eskisehir.eventapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user endpoints.
 * Handles user profile retrieval and preference updates.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current authenticated user's information.
     * GET /api/users/me
     * Requires authentication.
     *
     * @return UserResponse with current user's information
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        // Extract user ID from JWT token (stored in principal)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Update current user's preferences.
     * PUT /api/users/preferences
     * Requires authentication.
     *
     * @param preferenceUpdateRequest Update request containing new preferences
     * @return Empty 204 No Content response
     */
    @PutMapping("/preferences")
    public ResponseEntity<Void> updateUserPreferences(@Valid @RequestBody PreferenceUpdateRequest preferenceUpdateRequest) {
        // Extract user ID from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        userService.updateUserPreferences(userId, preferenceUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
