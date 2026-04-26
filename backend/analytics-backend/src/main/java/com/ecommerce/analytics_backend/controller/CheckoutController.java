package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.CheckoutRequestDTO;
import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/individual/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Order> submitOrder(Principal principal, @RequestBody CheckoutRequestDTO request) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized: No active session.");
        }

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order completedOrder = checkoutService.processCheckout(currentUser.getId(), request);

        return ResponseEntity.ok(completedOrder);
    }
}