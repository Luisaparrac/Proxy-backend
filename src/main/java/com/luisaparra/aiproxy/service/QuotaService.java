package com.luisaparra.aiproxy.service;

import com.luisaparra.aiproxy.dto.DailyUsageResponse;
import com.luisaparra.aiproxy.dto.QuotaStatusResponse;
import com.luisaparra.aiproxy.dto.UpgradeRequest;
import com.luisaparra.aiproxy.dto.UpgradeResponse;
import com.luisaparra.aiproxy.exception.InvalidUpgradeException;
import com.luisaparra.aiproxy.exception.UserNotFoundException;
import com.luisaparra.aiproxy.model.DailyUsage;
import com.luisaparra.aiproxy.model.Plan;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.RateLimitCounterStore;
import com.luisaparra.aiproxy.repository.UsageRepository;
import com.luisaparra.aiproxy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use cases related to quotas: current status, 7-day history and plan upgrade.
 */
@Service
public class QuotaService {

    private static final int HISTORY_DAYS = 7;

    private final UserRepository userRepository;
    private final UsageRepository usageRepository;
    private final RateLimitCounterStore counterStore;
    private final Clock clock;

    public QuotaService(UserRepository userRepository,
                        UsageRepository usageRepository,
                        RateLimitCounterStore counterStore,
                        Clock clock) {
        this.userRepository = userRepository;
        this.usageRepository = usageRepository;
        this.counterStore = counterStore;
        this.clock = clock;
    }

    public QuotaStatusResponse getStatus(String userId) {
        UserAccount user = findUser(userId);
        Plan plan = user.getPlan();
        long tokensUsed = user.getTokensUsedThisMonth();

        Long monthlyLimit = plan.hasUnlimitedTokens() ? null : plan.getMonthlyTokenLimit();
        Long tokensRemaining = plan.hasUnlimitedTokens() ? null : user.getRemainingTokens();
        Integer requestsLimit = plan.hasUnlimitedRequests() ? null : plan.getRequestsPerMinute();

        return new QuotaStatusResponse(
                user.getId(),
                user.getName(),
                plan,
                tokensUsed,
                tokensRemaining,
                monthlyLimit,
                plan.hasUnlimitedTokens(),
                nextMonthlyResetDate(),
                counterStore.getCount(user.getId()),
                requestsLimit,
                plan.hasUnlimitedRequests(),
                counterStore.secondsUntilReset()
        );
    }

    public List<DailyUsageResponse> getHistory(String userId) {
        UserAccount user = findUser(userId);
        LocalDate today = LocalDate.now(clock);
        List<DailyUsageResponse> history = new ArrayList<>();

        for (int daysAgo = HISTORY_DAYS - 1; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            DailyUsage usage = usageRepository.find(user.getId(), date).orElse(new DailyUsage(date));
            history.add(new DailyUsageResponse(date, usage.getTokens(), usage.getRequests()));
        }
        return history;
    }

    public UpgradeResponse upgrade(String userId, UpgradeRequest request) {
        UserAccount user = findUser(userId);
        Plan currentPlan = user.getPlan();

        if (currentPlan != Plan.FREE) {
            throw new InvalidUpgradeException("Solo los usuarios del plan FREE pueden hacer upgrade. Tu plan actual es "
                    + currentPlan + ".");
        }
        if (request.targetPlan() != Plan.PRO) {
            throw new InvalidUpgradeException("Solo está disponible el upgrade de FREE a PRO.");
        }

        user.changePlan(Plan.PRO);
        String transactionId = "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new UpgradeResponse(
                user.getId(),
                currentPlan,
                Plan.PRO,
                transactionId,
                Plan.PRO.getMonthlyPriceUsd(),
                "¡Pago simulado aprobado! " + user.getName() + " ahora tiene el plan PRO."
        );
    }

    private LocalDate nextMonthlyResetDate() {
        return LocalDate.now(clock).withDayOfMonth(1).plusMonths(1);
    }

    private UserAccount findUser(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
