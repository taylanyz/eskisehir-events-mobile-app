package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.InteractionRequest;
import com.eskisehir.eventapi.service.InteractionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@CrossOrigin(origins = "*")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> logInteraction(
            @Valid @RequestBody InteractionRequest request) {
        var saved = interactionService.logInteraction(request);
        return ResponseEntity.ok(Map.of("interactionId", saved.getId()));
    }
}
