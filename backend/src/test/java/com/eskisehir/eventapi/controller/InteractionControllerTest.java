package com.eskisehir.eventapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eskisehir.eventapi.domain.model.InteractionType;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserInteraction;
import com.eskisehir.eventapi.dto.InteractionRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.UserInteractionRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.service.InteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InteractionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StubInteractionService interactionService;

    @BeforeEach
    void setUp() {
        interactionService = new StubInteractionService();
        mockMvc = MockMvcBuilders.standaloneSetup(new InteractionController(interactionService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void logInteraction_ReturnsOkWithInteractionId() throws Exception {
        InteractionRequest request = new InteractionRequest();
        request.setUserId(1L);
        request.setPoiId(2L);
        request.setInteractionType(InteractionType.VIEW);
        request.setWeather("sunny");
        request.setTimeOfDay("AFTERNOON");

        UserInteraction interaction = new UserInteraction();
        interaction.setId(42L);
        interaction.setUser(new User());
        interaction.setPoi(new Poi());
        interactionService.savedInteraction = interaction;

        mockMvc.perform(post("/api/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interactionId").value(42));
    }

    private static final class StubInteractionService extends InteractionService {
        private UserInteraction savedInteraction;

        private StubInteractionService() {
            super(emptyUserRepository(), emptyPoiRepository(), emptyInteractionRepository(), emptyBanditRepository(), emptyBanditStatsRepository());
        }

        @Override
        public UserInteraction logInteraction(InteractionRequest request) {
            return savedInteraction;
        }

        private static UserRepository emptyUserRepository() {
            return (UserRepository) Proxy.newProxyInstance(
                    UserRepository.class.getClassLoader(),
                    new Class[]{UserRepository.class},
                    (proxy, method, args) -> null);
        }

        private static PoiRepository emptyPoiRepository() {
            return (PoiRepository) Proxy.newProxyInstance(
                    PoiRepository.class.getClassLoader(),
                    new Class[]{PoiRepository.class},
                    (proxy, method, args) -> null);
        }

        private static UserInteractionRepository emptyInteractionRepository() {
            return (UserInteractionRepository) Proxy.newProxyInstance(
                    UserInteractionRepository.class.getClassLoader(),
                    new Class[]{UserInteractionRepository.class},
                    (proxy, method, args) -> null);
        }

        private static BanditEventRepository emptyBanditRepository() {
            return (BanditEventRepository) Proxy.newProxyInstance(
                    BanditEventRepository.class.getClassLoader(),
                    new Class[]{BanditEventRepository.class},
                    (proxy, method, args) -> null);
        }

        private static BanditStatsRepository emptyBanditStatsRepository() {
            return (BanditStatsRepository) Proxy.newProxyInstance(
                    BanditStatsRepository.class.getClassLoader(),
                    new Class[]{BanditStatsRepository.class},
                    (proxy, method, args) -> null);
        }
    }
}
