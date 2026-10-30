package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.exception.RateLimitExceededException;
import com.luisaparra.aiproxy.model.Plan;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.RateLimitCounterStore;
import com.luisaparra.aiproxy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateLimitProxyServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-30T10:15:20Z"), ZoneOffset.UTC);

    private UserRepository userRepository;
    private RateLimitCounterStore counterStore;
    private RateLimitProxyService proxy;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        counterStore = new RateLimitCounterStore(FIXED_CLOCK);
        AIGenerationService fakeService = request ->
                new GenerationResponse("ok", "fake", 1, 1, 2, 0, Instant.now());
        proxy = new RateLimitProxyService(fakeService, userRepository, counterStore);
    }

    @Test
    void freeUserIsBlockedAfterTenRequestsInTheSameMinute() {
        userRepository.save(new UserAccount("u1", "Test", Plan.FREE, 0));
        GenerationRequest request = new GenerationRequest("u1", "hola");

        for (int i = 0; i < Plan.FREE.getRequestsPerMinute(); i++) {
            proxy.generate(request);
        }

        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class, () -> proxy.generate(request));
        assertEquals(40, ex.getRetryAfterSeconds());
        assertEquals(10, counterStore.getCount("u1"));
    }

    @Test
    void enterpriseUserIsNeverRateLimited() {
        userRepository.save(new UserAccount("e1", "Corp", Plan.ENTERPRISE, 0));
        GenerationRequest request = new GenerationRequest("e1", "hola");

        for (int i = 0; i < 200; i++) {
            proxy.generate(request);
        }

        assertEquals(200, counterStore.getCount("e1"));
    }

    @Test
    void counterResetAllowsNewRequests() {
        userRepository.save(new UserAccount("u2", "Test", Plan.FREE, 0));
        GenerationRequest request = new GenerationRequest("u2", "hola");
        for (int i = 0; i < Plan.FREE.getRequestsPerMinute(); i++) {
            proxy.generate(request);
        }

        counterStore.resetAll();

        assertEquals("ok", proxy.generate(request).text());
    }
}
