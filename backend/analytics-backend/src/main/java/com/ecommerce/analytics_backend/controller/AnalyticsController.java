package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto;
import com.ecommerce.analytics_backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAnalyticsDto> getAdminAnalytics() {
        return ResponseEntity.ok(analyticsService.getAdminAnalytics());
    }

    @GetMapping("/individual/analytics")
    @PreAuthorize("hasRole('INDIVIDUAL')")
    public ResponseEntity<DashboardAnalyticsDto> getIndividualAnalytics(Principal principal) {
        return ResponseEntity.ok(analyticsService.getIndividualAnalytics(principal.getName()));
    }

    @GetMapping("/corporate/analytics")
    @PreAuthorize("hasRole('CORPORATE')")
    public ResponseEntity<DashboardAnalyticsDto> getCorporateAnalytics(Principal principal) {
        return ResponseEntity.ok(analyticsService.getCorporateAnalytics(principal.getName()));
    }
}
