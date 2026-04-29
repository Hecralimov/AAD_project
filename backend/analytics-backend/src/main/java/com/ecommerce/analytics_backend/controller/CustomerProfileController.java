package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.CustomerProfileDTO;
import com.ecommerce.analytics_backend.model.CustomerProfile;
import com.ecommerce.analytics_backend.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.model.User;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService profileService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(Principal principal, @RequestBody Map<String, String> payload) {
        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid old password"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}