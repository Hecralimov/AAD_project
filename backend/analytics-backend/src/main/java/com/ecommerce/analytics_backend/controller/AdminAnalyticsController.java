package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.StoreComparisonProjection;
import com.ecommerce.analytics_backend.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/store-comparison")
    public ResponseEntity<List<StoreComparisonProjection>> getStoreComparison() {
        return ResponseEntity.ok(adminAnalyticsService.getStoreComparison());
    }
}