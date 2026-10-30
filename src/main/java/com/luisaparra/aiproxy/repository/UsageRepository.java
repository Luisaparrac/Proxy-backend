package com.luisaparra.aiproxy.repository;

import com.luisaparra.aiproxy.model.DailyUsage;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage of daily consumption per user.
 */
@Repository
public class UsageRepository {

    private final Map<String, Map<LocalDate, DailyUsage>> usageByUser = new ConcurrentHashMap<>();

    public void record(String userId, LocalDate date, long tokens) {
        usageByUser
                .computeIfAbsent(userId, id -> new ConcurrentHashMap<>())
                .computeIfAbsent(date, DailyUsage::new)
                .add(tokens);
    }

    public void save(String userId, DailyUsage usage) {
        usageByUser
                .computeIfAbsent(userId, id -> new ConcurrentHashMap<>())
                .put(usage.getDate(), usage);
    }

    public Optional<DailyUsage> find(String userId, LocalDate date) {
        return Optional.ofNullable(usageByUser.getOrDefault(userId, Map.of()).get(date));
    }
}
