package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.AuthResponse;
import com.eskisehir.eventapi.dto.LoginRequest;
import com.eskisehir.eventapi.dto.RegisterRequest;
import com.eskisehir.eventapi.dto.RefreshTokenRequest;
import com.eskisehir.eventapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration and login operations.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     * POST /api/auth/register
     *
     * @param registerRequest Registration request (email, displayName, password)
     * @return AuthResponse with JWT tokens and user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and generate JWT tokens.
     * POST /api/auth/login
     *
     * @param loginRequest Login request (email, password)
     * @return AuthResponse with JWT tokens and user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     * POST /api/auth/refresh
     *
     * @param refreshTokenRequest Refresh token request (refreshToken)
     * @return AuthResponse with new JWT tokens and user info
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
