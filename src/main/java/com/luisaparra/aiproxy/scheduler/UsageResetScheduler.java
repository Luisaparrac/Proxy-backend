package com.luisaparra.aiproxy.scheduler;

import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.RateLimitCounterStore;
import com.luisaparra.aiproxy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks: resets rate limit counters every minute
 * and monthly token quotas on the first day of each month.
 */
@Component
public class UsageResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(UsageResetScheduler.class);

    private final RateLimitCounterStore counterStore;
    private final UserRepository userRepository;

    public UsageResetScheduler(RateLimitCounterStore counterStore, UserRepository userRepository) {
        this.counterStore = counterStore;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 * * * * *", zone = "${app.time-zone}")
    public void resetRateLimits() {
        counterStore.resetAll();
        log.debug("Contadores de solicitudes por minuto reiniciados");
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "${app.time-zone}")
    public void resetMonthlyQuotas() {
        userRepository.findAll().forEach(UserAccount::resetMonthlyUsage);
        log.info("Cuotas mensuales de tokens reiniciadas para {} usuarios", userRepository.findAll().size());
    }
}
