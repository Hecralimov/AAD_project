package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.User;

import com.ecommerce.analytics_backend.service.CorporateAnalyticsService;
import com.ecommerce.analytics_backend.repository.UserRepository;
import java.security.Principal;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/corporate/operations")
@RequiredArgsConstructor
public class CorporateOperationsController {

    private final UserRepository userRepository;
    private final CorporateAnalyticsService corporateService;

    private User getCurrentCorporateUser(Principal principal) {
        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // A12: Fetch Inventory
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getInventory(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.getStoreInventory(user.getId(), PageRequest.of(page, size)));
    }

    // A13: Fetch Orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(Principal principal) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.getStoreOrders(user.getId()));
    }

    // A13: Fulfill/Update Order Status
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            Principal principal,
            @PathVariable String orderId,
            @RequestBody StatusUpdateDTO request) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.updateOrderStatus(user.getId(), orderId, request.getStatus()));
    }

    @Data
    public static class StatusUpdateDTO {
        private String status;
    }
}