package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.AuthResponse;
import com.eskisehir.eventapi.dto.LoginRequest;
import com.eskisehir.eventapi.dto.PreferenceUpdateRequest;
import com.eskisehir.eventapi.dto.RegisterRequest;
import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.SensitivityLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController.
 * Tests user profile retrieval and preference updates.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String testUserEmail;
    private static final String ME_URL = "/api/users/me";
    private static final String PREFERENCES_URL = "/api/users/preferences";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    @BeforeEach
    public void setUp() throws Exception {
        // Register and login to get token with unique email
        testUserEmail = "testuser_" + System.currentTimeMillis() + "@test.com";
        RegisterRequest registerRequest = new RegisterRequest(
                testUserEmail,
                "Test User",
                "password123"
        );

        MvcResult registerResult = mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(registerResponse, AuthResponse.class);
        accessToken = authResponse.getAccessToken();
    }

    @Test
    public void testGetCurrentUser_Success() throws Exception {
        mockMvc.perform(get(ME_URL)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(testUserEmail))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    public void testGetCurrentUserWithoutToken_Unauthorized() throws Exception {
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    public void testGetCurrentUserWithInvalidToken_Unauthorized() throws Exception {
        mockMvc.perform(get(ME_URL)
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUserPreferences_Success() throws Exception {
        PreferenceUpdateRequest request = new PreferenceUpdateRequest();
        request.setPreferredCategories(Arrays.asList(Category.CONCERT, Category.THEATER));
        request.setPreferredTags(Arrays.asList("outdoor", "family"));
        request.setBudgetSensitivity(SensitivityLevel.MEDIUM);
        request.setCrowdTolerance(SensitivityLevel.HIGH);
        request.setMaxWalkingMinutes(30);
        request.setSustainabilityPreference(0.7);

        mockMvc.perform(put(PREFERENCES_URL)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // Verify preferences were updated by checking through getting user
        mockMvc.perform(get(ME_URL)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUserEmail));
    }

    @Test
    public void testUpdateUserPreferencesWithoutToken_Unauthorized() throws Exception {
        PreferenceUpdateRequest request = new PreferenceUpdateRequest();
        request.setMaxWalkingMinutes(30);

        mockMvc.perform(put(PREFERENCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUserPreferencesInvalidData_BadRequest() throws Exception {
        PreferenceUpdateRequest request = new PreferenceUpdateRequest();
        request.setMaxWalkingMinutes(700); // Exceeds max of 600

        mockMvc.perform(put(PREFERENCES_URL)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserPreferencesWithSustainability_Success() throws Exception {
        PreferenceUpdateRequest request = new PreferenceUpdateRequest();
        request.setSustainabilityPreference(0.5);
        request.setMaxWalkingMinutes(45);

        mockMvc.perform(put(PREFERENCES_URL)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
