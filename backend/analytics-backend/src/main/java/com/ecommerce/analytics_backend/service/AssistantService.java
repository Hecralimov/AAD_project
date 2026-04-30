package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.AssistantChatRequest;
import com.ecommerce.analytics_backend.dto.AssistantChatResponse;
import com.ecommerce.analytics_backend.model.Order;
import com.ecommerce.analytics_backend.model.OrderItem;
import com.ecommerce.analytics_backend.model.Product;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.OrderItemRepository;
import com.ecommerce.analytics_backend.repository.ProductRepository;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.ai.AiProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistantService {

        private final AiProvider aiProvider;
        private final OrderService orderService;
        private final OrderItemRepository orderItemRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        @Value("${assistant.max-context-orders:5}")
        private int maxContextOrders;

        public AssistantChatResponse chat(AssistantChatRequest request, String email) {
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<Order> userOrders = orderService.getUserOrders(currentUser.getId(), null);

                List<Order> recentOrders = userOrders.stream()
                                .limit(maxContextOrders)
                                .collect(Collectors.toList());

                List<String> orderIds = recentOrders.stream().map(Order::getId).collect(Collectors.toList());

                List<OrderItem> orderItems = orderIds.isEmpty() ? List.of()
                                : orderItemRepository.findByOrderIdIn(orderIds);

                List<String> productIds = orderItems.stream()
                                .map(OrderItem::getProductId)
                                .distinct()
                                .collect(Collectors.toList());

                List<Product> products = productIds.isEmpty() ? List.of() : productRepository.findAllById(productIds);

                Map<String, Object> context = new HashMap<>();
                context.put("ordersReviewed", recentOrders.size());
                context.put("productsReviewed", products.size());
                context.put("recentOrders", recentOrders);
                context.put("orderItems", orderItems);
                context.put("products", products);

                String role = currentUser.getRoleType() != null ? currentUser.getRoleType().toUpperCase()
                                : "INDIVIDUAL";
                String contextId = currentUser.getId();

                return aiProvider.generateResponse(request, context, role, contextId);
        }
}
