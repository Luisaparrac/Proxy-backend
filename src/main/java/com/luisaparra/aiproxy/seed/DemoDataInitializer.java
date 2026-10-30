package com.luisaparra.aiproxy.seed;

import com.luisaparra.aiproxy.model.DailyUsage;
import com.luisaparra.aiproxy.model.Plan;
import com.luisaparra.aiproxy.model.UserAccount;
import com.luisaparra.aiproxy.repository.UsageRepository;
import com.luisaparra.aiproxy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Random;

/**
 * Loads demo users and the usage of the previous 6 days, so the history chart
 * has data from the start. One FREE user starts almost out of tokens to make
 * it easy to show the upgrade flow.
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final int SEEDED_PAST_DAYS = 6;

    private final UserRepository userRepository;
    private final UsageRepository usageRepository;
    private final Clock clock;

    public DemoDataInitializer(UserRepository userRepository, UsageRepository usageRepository, Clock clock) {
        this.userRepository = userRepository;
        this.usageRepository = usageRepository;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        createUser("free-user", "Ana Gómez", Plan.FREE, 12_400, 2_500);
        createUser("free-limit-user", "Carlos Ruiz", Plan.FREE, 49_700, 6_000);
        createUser("pro-user", "Sofía Martínez", Plan.PRO, 182_300, 25_000);
        createUser("enterprise-user", "Acme Corp", Plan.ENTERPRISE, 1_250_000, 90_000);
        log.info("Usuarios demo cargados: {}", userRepository.findAll().size());
    }

    private void createUser(String id, String name, Plan plan, long tokensUsed, int maxDailyTokens) {
        userRepository.save(new UserAccount(id, name, plan, tokensUsed));

        Random random = new Random(id.hashCode());
        LocalDate today = LocalDate.now(clock);
        for (int daysAgo = SEEDED_PAST_DAYS; daysAgo >= 1; daysAgo--) {
            long tokens = maxDailyTokens / 5 + random.nextInt(maxDailyTokens);
            int requests = (int) Math.max(1, tokens / 90);
            usageRepository.save(id, new DailyUsage(today.minusDays(daysAgo), tokens, requests));
        }
    }
}
