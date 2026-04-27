package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.CustomerProfileDTO;
import com.ecommerce.analytics_backend.model.CustomerProfile;
import com.ecommerce.analytics_backend.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService profileService;

    @GetMapping
    public ResponseEntity<CustomerProfile> getMyProfile(Principal principal) {
        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");
        return ResponseEntity.ok(profileService.getProfile(principal.getName()));
    }

    @PutMapping
    public ResponseEntity<CustomerProfile> updateMyProfile(
            Principal principal,
            @RequestBody CustomerProfileDTO dto) {
        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");
        return ResponseEntity.ok(profileService.upsertProfile(principal.getName(), dto));
    }
}