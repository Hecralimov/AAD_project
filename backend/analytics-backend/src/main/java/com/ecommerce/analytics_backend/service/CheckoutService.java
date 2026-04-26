package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.CheckoutRequestDTO;
import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.OrderItem;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.Shipment;
import com.ecommerce.analytics_backend.repository.OrderItemRepository;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import com.ecommerce.analytics_backend.repository.ProductRepository;
import com.ecommerce.analytics_backend.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ShipmentRepository shipmentRepository;

    @Transactional
    public Order processCheckout(String userId, CheckoutRequestDTO request) {
        BigDecimal grandTotal = BigDecimal.ZERO;
        List<Product> productsToUpdate = new ArrayList<>();
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        // 1. Verify stock, deduct inventory, and calculate grand total
        for (CheckoutRequestDTO.CartItemDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() + " (Only "
                        + product.getStock() + " left)");
            }

            // Deduct stock
            product.setStock(product.getStock() - item.getQuantity());
            productsToUpdate.add(product);

            // Calculate item total using your precise 'unitPrice' field
            BigDecimal itemTotal = product.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(itemTotal);

            // Prep OrderItem (we don't have the Order ID yet!)
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getUnitPrice()); // Lock in the price at checkout
            orderItemsToSave.add(orderItem);
        }

        // 2. Save all updated products to the DB
        productRepository.saveAll(productsToUpdate);

        // 3. Create the Order and save it to generate the UUID
        Order order = new Order();
        order.setUserId(userId);
        order.setStoreId(request.getStoreId());
        order.setGrandTotal(grandTotal);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 4. Link the generated Order ID to our items and save them
        for (OrderItem oi : orderItemsToSave) {
            oi.setOrderId(savedOrder.getId());
        }
        orderItemRepository.saveAll(orderItemsToSave);

        // 5. Generate the Shipment pipeline record
        Shipment shipment = new Shipment();
        shipment.setOrderId(savedOrder.getId());
        shipment.setWarehouse("Main Fulfillment Center");
        shipment.setMode("STANDARD");
        shipment.setStatus("PROCESSING");

        shipmentRepository.save(shipment);

        return savedOrder;
    }
}