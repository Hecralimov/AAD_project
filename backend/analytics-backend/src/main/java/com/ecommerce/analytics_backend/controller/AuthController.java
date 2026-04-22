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
import com.ecommerce.analytics_backend.model.User;
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

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
                // Build the new user object
                var user = User.builder()
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .roleType(request.getRoleType())
                                .gender(request.getGender())
                                .build();

                // Save to DB
                repository.save(user);

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
                System.out.println("[DEBUG] Login attempt received for email: " + request.getEmail());
                // Authenticate credentials (this throws an exception if incorrect)
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                // Find user & generate token
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);

                return ResponseEntity.ok(AuthResponse.builder()
                                .token(jwtToken)
                                .role(user.getRoleType())
                                .email(user.getEmail())
                                .build());
        }
}
