package com.luisaparra.aiproxy.controller;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.dto.PromptRequest;
import com.luisaparra.aiproxy.service.ai.AIGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Text generation endpoint. The injected service is the full proxy chain.
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiGenerationService;

    public AIController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(@RequestHeader(ApiHeaders.USER_ID) String userId,
                                       @Valid @RequestBody PromptRequest body) {
        return aiGenerationService.generate(new GenerationRequest(userId, body.prompt()));
    }
}
