package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.Shipment;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import com.ecommerce.analytics_backend.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(String id, Order order) {
        order.setId(id);
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getCorporateOrders(String storeId, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return orderRepository.findByStoreIdAndStatus(storeId, status);
        }
        return orderRepository.findByStoreId(storeId);
    }

    @Transactional
    public Order updateCorporateOrderStatus(String orderId, String storeId, String newStatus) {
        // Find the order AND verify it belongs to this store in one query
        Order order = orderRepository.findByIdAndStoreId(orderId, storeId)
                .orElseThrow(() -> new RuntimeException("Order not found or unauthorized access: " + orderId));

        order.setStatus(newStatus);

        // If the order is being shipped, automatically update the physical shipment
        // pipeline
        if ("SHIPPED".equalsIgnoreCase(newStatus)) {
            shipmentRepository.findByOrderId(orderId).ifPresent(shipment -> {
                shipment.setStatus("IN_TRANSIT");
                shipmentRepository.save(shipment);
            });
        }

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String userId, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Shipment getShipmentTracking(String orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No shipment found for order: " + orderId));
    }
}
