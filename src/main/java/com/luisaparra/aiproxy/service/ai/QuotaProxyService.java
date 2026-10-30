package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.exception.QuotaExceededException;
import com.luisaparra.aiproxy.exception.UserNotFoundException;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.UsageRepository;
import com.luisaparra.aiproxy.repository.UserRepository;
import com.luisaparra.aiproxy.service.TokenEstimator;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Second proxy of the chain. Checks that the user still has monthly tokens,
 * calls the real service, discounts the consumed tokens and records the usage.
 * When the quota is exhausted it throws a 402 Payment Required.
 */
public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService delegate;
    private final UserRepository userRepository;
    private final UsageRepository usageRepository;
    private final TokenEstimator tokenEstimator;
    private final Clock clock;

    public QuotaProxyService(AIGenerationService delegate,
                             UserRepository userRepository,
                             UsageRepository usageRepository,
                             TokenEstimator tokenEstimator,
                             Clock clock) {
        this.delegate = delegate;
        this.userRepository = userRepository;
        this.usageRepository = usageRepository;
        this.tokenEstimator = tokenEstimator;
        this.clock = clock;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        if (!user.getPlan().hasUnlimitedTokens()) {
            long remainingTokens = user.getRemainingTokens();
            int promptTokens = tokenEstimator.estimate(request.prompt());
            if (remainingTokens <= 0 || promptTokens > remainingTokens) {
                throw new QuotaExceededException(remainingTokens);
            }
        }

        GenerationResponse response = delegate.generate(request);

        user.consumeTokens(response.totalTokens());
        usageRepository.record(user.getId(), LocalDate.now(clock), response.totalTokens());
        return response;
    }
}
