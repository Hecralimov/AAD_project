package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.CorporateAnalyticsDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.CorporateAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping({ "/api/analytics/corporate", "/api/corporate/analytics" })
@RequiredArgsConstructor
public class CorporateAnalyticsController {

    private final CorporateAnalyticsService corporateAnalyticsService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<CorporateAnalyticsDTO> getCorporateAnalytics(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity
                .ok(corporateAnalyticsService.getAnalyticsForOwner(currentUser.getId(), startDate, endDate));
    }
}
