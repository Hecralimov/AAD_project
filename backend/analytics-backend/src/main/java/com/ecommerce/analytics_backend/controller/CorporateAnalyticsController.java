package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.CorporateAnalyticsDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.CorporateAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/corporate/analytics")
@RequiredArgsConstructor
public class CorporateAnalyticsController {

    private final CorporateAnalyticsService analyticsService;
    private final UserRepository userRepository; // Inject this

    @GetMapping
    public ResponseEntity<CorporateAnalyticsDTO> getDashboardAnalytics(Principal principal) {
        // 1. Safely extract the email from the JWT Principal
        if (principal == null) {
            throw new RuntimeException("Unauthorized: No principal found");
        }
        
        // 2. Fetch the actual user from the database
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found for email: " + principal.getName()));

        // 3. Execute the service
        return ResponseEntity.ok(analyticsService.getAnalyticsForOwner(currentUser.getId()));
    }
}