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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorporateAnalyticsService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CorporateAnalyticsDTO getAnalyticsForOwner(String ownerId, LocalDate startDate, LocalDate endDate) {
        Store store = getValidStore(ownerId);

        String storeId = store.getId();
        BigDecimal revenue;
        Long orders;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX); // End of the day
            revenue = orderRepository.calculateTotalRevenueByStoreIdAndDateRange(storeId, start, end);
            orders = orderRepository.countByStoreIdAndDateRange(storeId, start, end);
        } else {
            // Fallback to lifetime stats if no dates are selected
            revenue = orderRepository.calculateTotalRevenueByStoreId(storeId);
            orders = orderRepository.countByStoreId(storeId);
        }

        Long lowStock = productRepository.countLowStockProducts(storeId);

        // Fixed the builder to match your DTO's totalOrders field
        return CorporateAnalyticsDTO.builder()
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalOrders(orders != null ? orders : 0L)
                .lowStockCount(lowStock != null ? lowStock : 0L)
                .build();
    }

    private Store getValidStore(String ownerId) {
        return storeRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    Store store = new Store();
                    store.setOwnerId(ownerId);
                    store.setName("Corporate Store");
                    store.setStatus("OPEN");
                    return storeRepository.save(store);
                });
    }

    // A12: Get Store Products
    @Transactional
    public Page<Product> getStoreInventory(String ownerId, Pageable pageable) {
        Store store = getValidStore(ownerId);
        return productRepository.findByStoreId(store.getId(), pageable);
    }

    @Transactional
    public Product createStoreProduct(String ownerId, Product product) {
        Store store = getValidStore(ownerId);
        product.setId(null);
        product.setStoreId(store.getId());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateStoreProduct(String ownerId, String productId, Product updatedProduct) {
        Store store = getValidStore(ownerId);

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!existingProduct.getStoreId().equals(store.getId())) {
            throw new RuntimeException("Forbidden: You cannot modify products that belong to another store.");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setSku(updatedProduct.getSku());
        existingProduct.setUnitPrice(updatedProduct.getUnitPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setCategoryId(updatedProduct.getCategoryId());

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteStoreProduct(String ownerId, String productId) {
        Store store = getValidStore(ownerId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!product.getStoreId().equals(store.getId())) {
            throw new RuntimeException("Forbidden: You cannot delete products that belong to another store.");
        }

        productRepository.delete(product);
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
