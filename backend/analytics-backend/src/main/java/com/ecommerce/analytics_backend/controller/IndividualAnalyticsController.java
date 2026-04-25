package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.IndividualAnalyticsDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.IndividualAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/individual/analytics")
@RequiredArgsConstructor
public class IndividualAnalyticsController {

    private final IndividualAnalyticsService analyticsService;
    private final UserRepository userRepository;

@GetMapping
    public ResponseEntity<IndividualAnalyticsDTO> getDashboardAnalytics(Principal principal, org.springframework.security.core.Authentication auth) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized: No principal found");
        }
        
        System.out.println("DEBUG: User " + principal.getName() + " has authorities: " + auth.getAuthorities());
        
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found for email: " + principal.getName()));

        return ResponseEntity.ok(analyticsService.getIndividualDashboard(currentUser.getId()));
    }
}