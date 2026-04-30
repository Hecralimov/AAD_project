package com.ecommerce.analytics_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.CorporateOperationsService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/corporate/operations")
@RequiredArgsConstructor
public class CorporateOperationsController {

    private final UserRepository userRepository;
    private final CorporateOperationsService corporateService;

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

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(Principal principal, @RequestBody Product product) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.createStoreProduct(user.getId(), product));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(
            Principal principal,
            @PathVariable String productId,
            @RequestBody Product product) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.updateStoreProduct(user.getId(), productId, product));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(Principal principal, @PathVariable String productId) {
        User user = getCurrentCorporateUser(principal);
        corporateService.deleteStoreProduct(user.getId(), productId);
        return ResponseEntity.ok().build();
    }

    // A13: Fetch Orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getCurrentCorporateUser(principal);
        return ResponseEntity.ok(corporateService.getStoreOrders(user.getId(), PageRequest.of(page, size)));
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
