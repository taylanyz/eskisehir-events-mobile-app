package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserPreference;
import com.eskisehir.eventapi.dto.AuthResponse;
import com.eskisehir.eventapi.dto.LoginRequest;
import com.eskisehir.eventapi.dto.RegisterRequest;
import com.eskisehir.eventapi.exception.InvalidCredentialsException;
import com.eskisehir.eventapi.exception.UserAlreadyExistsException;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.repository.UserPreferenceRepository;
import com.eskisehir.eventapi.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 * Handles user registration and login, token generation.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Register a new user.
     * Creates User entity with hashed password and initializes empty UserPreference.
     *
     * @param registerRequest Registration request containing email, displayName, password
     * @return AuthResponse with JWT tokens and user info
     * @throws UserAlreadyExistsException if email already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + registerRequest.getEmail());
        }

        // Create new User
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setDisplayName(registerRequest.getDisplayName());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Create empty UserPreference for the user
        UserPreference preference = new UserPreference();
        preference.setUser(savedUser);
        userPreferenceRepository.save(preference);

        // Generate tokens
        String accessToken = jwtUtils.generateJwtToken(savedUser.getId(), savedUser.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getDisplayName()
        );
    }

    /**
     * Authenticate user and generate JWT tokens.
     *
     * @param loginRequest Login request containing email and password
     * @return AuthResponse with JWT tokens and user info
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Update last login time
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtils.generateJwtToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    /**
     * Refresh access token using a valid refresh token.
     * Validates the refresh token and generates a new access token.
     *
     * @param refreshToken The refresh token
     * @return AuthResponse with new access token and refresh token
     * @throws InvalidCredentialsException if refresh token is invalid or expired
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        // Extract user ID from refresh token
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String email = jwtUtils.getEmailFromToken(refreshToken);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Generate new tokens
        String newAccessToken = jwtUtils.generateJwtToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getEmail());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }
}
