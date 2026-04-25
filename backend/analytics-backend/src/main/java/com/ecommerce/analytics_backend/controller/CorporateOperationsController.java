package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.model.User;

import com.ecommerce.analytics_backend.repository.StoreRepository;

import com.ecommerce.analytics_backend.service.OrderService;
import com.ecommerce.analytics_backend.service.ProductService;
import com.ecommerce.analytics_backend.repository.UserRepository;
import java.security.Principal;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/corporate")
@RequiredArgsConstructor
public class CorporateOperationsController {

    private final StoreRepository storeRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    private String resolveStoreId(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        Store store = storeRepository.findByOwnerId(user.getId())
            .orElseThrow(() -> new RuntimeException("No store provisioned for corporate user: " + user.getId()));
        
        return store.getId();
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getStoreProducts(Principal principal) {
        String storeId = resolveStoreId(principal);
        return ResponseEntity.ok(productService.getProductsByStore(storeId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getStoreOrders(
            Principal principal,
            @RequestParam(required = false) String status) {
        
        String storeId = resolveStoreId(principal);
        return ResponseEntity.ok(orderService.getCorporateOrders(storeId, status));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            Principal principal,
            @PathVariable String orderId,
            @RequestBody Map<String, String> payload) {
        
        String storeId = resolveStoreId(principal);
        String newStatus = payload.get("status");
        
        if (newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("Status payload is missing");
        }

        return ResponseEntity.ok(orderService.updateCorporateOrderStatus(orderId, storeId, newStatus));
    }
}