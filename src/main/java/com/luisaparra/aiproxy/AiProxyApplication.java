package com.luisaparra.aiproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of the AI consumption platform.
 * Scheduling is enabled to reset rate limits every minute and quotas every month.
 */
@SpringBootApplication
@EnableScheduling
public class AiProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiProxyApplication.class, args);
    }
}
