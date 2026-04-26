package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.CorporateAnalyticsDTO;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import com.ecommerce.analytics_backend.repository.ProductRepository;
import com.ecommerce.analytics_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorporateAnalyticsService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CorporateAnalyticsDTO getAnalyticsForOwner(String ownerId) {
        // 1. Find the store using the user's ID
        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("No store found for user ID: " + ownerId));

        String storeId = store.getId();

        // 2. Run aggregations scoped strictly to this store
        BigDecimal revenue = orderRepository.calculateTotalRevenueByStoreId(storeId);
        Long orders = orderRepository.countByStoreId(storeId);
        Long lowStock = productRepository.countLowStockProducts(storeId);

        // 3. Build and return DTO
        return CorporateAnalyticsDTO.builder()
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .orderCount(orders != null ? orders : 0L)
                .lowStockCount(lowStock != null ? lowStock : 0L)
                .build();
    }

    private Store getValidStore(String ownerId) {
        return storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Forbidden: No store associated with this corporate account."));
    }

    // A12: Get Store Products
    public Page<Product> getStoreInventory(String ownerId, Pageable pageable) {
        Store store = getValidStore(ownerId);
        return productRepository.findByStoreId(store.getId(), pageable);
    }

    // A13: Get Store Orders
    public List<Order> getStoreOrders(String ownerId) {
        Store store = getValidStore(ownerId);
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(store.getId());
    }

    // A13: Update Order Status
    @Transactional
    public Order updateOrderStatus(String ownerId, String orderId, String newStatus) {
        Store store = getValidStore(ownerId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Security Check: Does this order actually belong to this store?
        if (!order.getStoreId().equals(store.getId())) {
            throw new RuntimeException("Forbidden: You cannot modify orders that belong to another store.");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}