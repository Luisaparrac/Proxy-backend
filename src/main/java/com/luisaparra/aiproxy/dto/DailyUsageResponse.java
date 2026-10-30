package com.luisaparra.aiproxy.dto;

import java.time.LocalDate;

/**
 * Usage of a single day, used by the 7-day history chart.
 */
public record DailyUsageResponse(LocalDate date, long tokens, int requests) {
}
