package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.exception.QuotaExceededException;
import com.luisaparra.aiproxy.model.Plan;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.UsageRepository;
import com.luisaparra.aiproxy.repository.UserRepository;
import com.luisaparra.aiproxy.service.TokenEstimator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuotaProxyServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-30T10:15:20Z"), ZoneOffset.UTC);
    private static final int TOKENS_PER_CALL = 100;

    private UserRepository userRepository;
    private UsageRepository usageRepository;
    private QuotaProxyService proxy;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        usageRepository = new UsageRepository();
        AIGenerationService fakeService = request ->
                new GenerationResponse("ok", "fake", 10, 90, TOKENS_PER_CALL, 0, Instant.now());
        proxy = new QuotaProxyService(fakeService, userRepository, usageRepository, new TokenEstimator(), FIXED_CLOCK);
    }

    @Test
    void discountsTokensAndRecordsDailyUsage() {
        UserAccount user = userRepository.save(new UserAccount("u1", "Test", Plan.FREE, 0));

        proxy.generate(new GenerationRequest("u1", "hola"));

        assertEquals(TOKENS_PER_CALL, user.getTokensUsedThisMonth());
        assertEquals(TOKENS_PER_CALL, usageRepository.find("u1", LocalDate.now(FIXED_CLOCK)).orElseThrow().getTokens());
    }

    @Test
    void throwsPaymentRequiredWhenQuotaIsExhausted() {
        userRepository.save(new UserAccount("u2", "Test", Plan.FREE, Plan.FREE.getMonthlyTokenLimit()));

        assertThrows(QuotaExceededException.class, () -> proxy.generate(new GenerationRequest("u2", "hola")));
    }

    @Test
    void enterpriseUserHasNoTokenLimit() {
        UserAccount user = userRepository.save(new UserAccount("e1", "Corp", Plan.ENTERPRISE, 10_000_000));

        proxy.generate(new GenerationRequest("e1", "hola"));

        assertEquals(10_000_000 + TOKENS_PER_CALL, user.getTokensUsedThisMonth());
    }
}
