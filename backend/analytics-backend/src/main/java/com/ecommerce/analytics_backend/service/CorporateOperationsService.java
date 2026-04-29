package com.ecommerce.analytics_backend.service;

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorporateOperationsService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private Store getValidStore(String ownerId) {
        return storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Forbidden: No store associated with this corporate account."));
    }

    // A12: Get Store Products
    public Page<Product> getStoreInventory(String ownerId, Pageable pageable) {
        Store store = getValidStore(ownerId);
        return productRepository.findByStoreId(store.getId(), pageable);
    }

    @Transactional
    public Product createStoreProduct(String ownerId, Product product) {
        Store store = getValidStore(ownerId);
        product.setStoreId(store.getId());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateStoreProduct(String ownerId, String productId, Product updatedProduct) {
        Store store = getValidStore(ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!product.getStoreId().equals(store.getId())) {
            throw new RuntimeException("Forbidden: You cannot modify products that belong to another store.");
        }

        product.setCategoryId(updatedProduct.getCategoryId());
        product.setStock(updatedProduct.getStock());
        product.setSku(updatedProduct.getSku());
        product.setName(updatedProduct.getName());
        product.setUnitPrice(updatedProduct.getUnitPrice());

        return productRepository.save(product);
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
    public List<Order> getStoreOrders(String ownerId, Pageable pageable) {
        Store store = getValidStore(ownerId);
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(store.getId(), pageable);
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
