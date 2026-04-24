package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserPreference;
import com.eskisehir.eventapi.dto.PreferenceUpdateRequest;
import com.eskisehir.eventapi.dto.UserResponse;
import com.eskisehir.eventapi.exception.UserNotFoundException;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for user-related operations.
 * Handles user profile retrieval and preference updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public UserService(UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    /**
     * Get user information by ID.
     *
     * @param userId User ID
     * @return UserResponse containing user details
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * Get user information by email.
     *
     * @param email User email
     * @return UserResponse containing user details
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    /**
     * Update user preferences for recommendations and route planning.
     *
     * @param userId User ID
     * @param request PreferenceUpdateRequest containing updated preferences
     * @throws UserNotFoundException if user not found
     */
    @Transactional
    public void updateUserPreferences(Long userId, PreferenceUpdateRequest request) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Get or create UserPreference
        UserPreference preference = userPreferenceRepository.findById(userId)
                .orElseGet(() -> {
                    UserPreference newPref = new UserPreference();
                    newPref.setId(userId);
                    User user = new User();
                    user.setId(userId);
                    newPref.setUser(user);
                    return newPref;
                });

        // Update preferences
        if (request.getPreferredCategories() != null) {
            preference.setPreferredCategories(request.getPreferredCategories());
        }

        if (request.getPreferredTags() != null) {
            preference.setPreferredTags(request.getPreferredTags());
        }

        if (request.getBudgetSensitivity() != null) {
            preference.setBudgetSensitivity(request.getBudgetSensitivity());
        }

        if (request.getCrowdTolerance() != null) {
            preference.setCrowdTolerance(request.getCrowdTolerance());
        }

        if (request.getMobilityPreference() != null) {
            preference.setMobilityPreference(request.getMobilityPreference());
        }

        if (request.getSustainabilityPreference() != null) {
            preference.setSustainabilityPreference(request.getSustainabilityPreference());
        }

        if (request.getMaxWalkingMinutes() != null) {
            preference.setMaxWalkingMinutes(request.getMaxWalkingMinutes());
        }

        // Save updated preference
        userPreferenceRepository.save(preference);
    }

    /**
     * Get user preferences.
     *
     * @param userId User ID
     * @return UserPreference object
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserPreference getUserPreferences(Long userId) {
        return userPreferenceRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User preferences not found for ID: " + userId));
    }
}
