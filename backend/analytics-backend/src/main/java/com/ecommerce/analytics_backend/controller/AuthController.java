package com.ecommerce.analytics_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.analytics_backend.dto.AuthRequest;
import com.ecommerce.analytics_backend.dto.AuthResponse;
import com.ecommerce.analytics_backend.dto.RegisterRequest;
import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.StoreRepository;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final StoreRepository storeRepository;

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
                String email = request.getEmail().trim().toLowerCase();
                String roleType = normalizeRole(request.getRoleType());

                if (repository.existsByEmail(email)) {
                        throw new RuntimeException("Email is already in use.");
                }

                // Build the new user object
                var user = User.builder()
                                .email(email)
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .roleType(roleType)
                                .gender(request.getGender())
                                .isActive(true)
                                .build();

                // Save to DB
                repository.save(user);

                if ("Corporate".equals(roleType)) {
                        Store store = new Store();
                        store.setOwnerId(user.getId());
                        store.setName(email + " Store");
                        store.setStatus("OPEN");
                        storeRepository.save(store);
                }

                // Generate a token for the newly registered user
                var jwtToken = jwtService.generateToken(user);

                return ResponseEntity.ok(AuthResponse.builder()
                                .token(jwtToken)
                                .role(user.getRoleType())
                                .email(user.getEmail())
                                .build());
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
                String email = request.getEmail().trim().toLowerCase();
                System.out.println("[DEBUG] Login attempt received for email: " + email);
                // Authenticate credentials (this throws an exception if incorrect)
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                email,
                                                request.getPassword()));

                // Find user & generate token
                var user = repository.findByEmail(email)
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);

                return ResponseEntity.ok(AuthResponse.builder()
                                .token(jwtToken)
                                .role(user.getRoleType())
                                .email(user.getEmail())
                                .build());
        }

        private String normalizeRole(String roleType) {
                if (roleType == null || roleType.isBlank()) {
                        return "Individual";
                }

                return switch (roleType.trim().toUpperCase()) {
                        case "ADMIN", "ROLE_ADMIN" -> "Admin";
                        case "CORPORATE", "ROLE_CORPORATE" -> "Corporate";
                        default -> "Individual";
                };
        }
}
