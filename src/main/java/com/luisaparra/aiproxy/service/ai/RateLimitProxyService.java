package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.exception.RateLimitExceededException;
import com.luisaparra.aiproxy.exception.UserNotFoundException;
import com.luisaparra.aiproxy.model.Plan;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.RateLimitCounterStore;
import com.luisaparra.aiproxy.repository.UserRepository;

/**
 * First proxy of the chain. Checks that the user does not exceed the
 * requests per minute of its plan; otherwise throws a 429 with Retry-After.
 */
public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService delegate;
    private final UserRepository userRepository;
    private final RateLimitCounterStore counterStore;

    public RateLimitProxyService(AIGenerationService delegate,
                                 UserRepository userRepository,
                                 RateLimitCounterStore counterStore) {
        this.delegate = delegate;
        this.userRepository = userRepository;
        this.counterStore = counterStore;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Plan plan = user.getPlan();

        int requestsThisMinute = counterStore.increment(user.getId());
        if (!plan.hasUnlimitedRequests() && requestsThisMinute > plan.getRequestsPerMinute()) {
            counterStore.decrement(user.getId());
            throw new RateLimitExceededException(plan.getRequestsPerMinute(), counterStore.secondsUntilReset());
        }

        return delegate.generate(request);
    }
}
