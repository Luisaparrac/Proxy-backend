package com.luisaparra.aiproxy.controller;

import com.luisaparra.aiproxy.dto.DailyUsageResponse;
import com.luisaparra.aiproxy.dto.QuotaStatusResponse;
import com.luisaparra.aiproxy.dto.UpgradeRequest;
import com.luisaparra.aiproxy.dto.UpgradeResponse;
import com.luisaparra.aiproxy.service.QuotaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Quota status, usage history and plan upgrade endpoints.
 */
@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/status")
    public QuotaStatusResponse getStatus(@RequestHeader(ApiHeaders.USER_ID) String userId) {
        return quotaService.getStatus(userId);
    }

    @GetMapping("/history")
    public List<DailyUsageResponse> getHistory(@RequestHeader(ApiHeaders.USER_ID) String userId) {
        return quotaService.getHistory(userId);
    }

    @PostMapping("/upgrade")
    public UpgradeResponse upgrade(@RequestHeader(ApiHeaders.USER_ID) String userId,
                                   @Valid @RequestBody UpgradeRequest request) {
        return quotaService.upgrade(userId, request);
    }
}
