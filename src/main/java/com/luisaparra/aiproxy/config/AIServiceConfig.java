package com.luisaparra.aiproxy.config;

import com.luisaparra.aiproxy.repository.RateLimitCounterStore;
import com.luisaparra.aiproxy.repository.UsageRepository;
import com.luisaparra.aiproxy.repository.UserRepository;
import com.luisaparra.aiproxy.service.TokenEstimator;
import com.luisaparra.aiproxy.service.ai.AIGenerationService;
import com.luisaparra.aiproxy.service.ai.MockAIGenerationService;
import com.luisaparra.aiproxy.service.ai.QuotaProxyService;
import com.luisaparra.aiproxy.service.ai.RateLimitProxyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Builds the proxy chain:
 * RateLimitProxyService -> QuotaProxyService -> MockAIGenerationService.
 * Controllers only receive the {@link AIGenerationService} interface.
 */
@Configuration
public class AIServiceConfig {

    @Bean
    public AIGenerationService aiGenerationService(TokenEstimator tokenEstimator,
                                                   UserRepository userRepository,
                                                   UsageRepository usageRepository,
                                                   RateLimitCounterStore counterStore,
                                                   Clock clock) {
        AIGenerationService realService = new MockAIGenerationService(tokenEstimator);
        AIGenerationService quotaProxy = new QuotaProxyService(
                realService, userRepository, usageRepository, tokenEstimator, clock);
        return new RateLimitProxyService(quotaProxy, userRepository, counterStore);
    }
}
