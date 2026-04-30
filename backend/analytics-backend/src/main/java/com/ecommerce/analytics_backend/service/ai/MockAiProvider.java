package com.ecommerce.analytics_backend.service.ai;

import com.ecommerce.analytics_backend.dto.AssistantAction;
import com.ecommerce.analytics_backend.dto.AssistantChatRequest;
import com.ecommerce.analytics_backend.dto.AssistantChatResponse;
import com.ecommerce.analytics_backend.model.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "assistant.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiProvider implements AiProvider {

    @Override
    public AssistantChatResponse generateResponse(AssistantChatRequest request, Map<String, Object> context,
            String role, String contextId) {
        String msg = request.getMessage() != null ? request.getMessage().toLowerCase() : "";
        String conversationId = request.getConversationId() != null ? request.getConversationId()
                : UUID.randomUUID().toString();

        AssistantChatResponse response = AssistantChatResponse.builder()
                .conversationId(conversationId)
                .referencedData(context)
                .build();

        if (msg.contains("past orders") || msg.contains("reorder") || msg.contains("buy this again")) {
            String recommendedProductId = null;

            if (context != null && context.get("products") instanceof List<?> products && !products.isEmpty()) {
                if (products.get(0) instanceof Product p) {
                    recommendedProductId = p.getId();
                }
            }

            if (recommendedProductId != null) {
                response.setAnswer("Based on your recent orders, I recommend reordering this item.");
                response.setSuggestedActions(List.of(
                        AssistantAction.builder()
                                .type("ADD_TO_CART_DRAFT")
                                .productId(recommendedProductId)
                                .quantity(1)
                                .label("Add to cart")
                                .confidence(0.95)
                                .build()));
            } else {
                response.setAnswer(
                        "I checked your history, but it looks like you don't have any past orders to reorder yet!");
                response.setSuggestedActions(List.of());
            }
        } else {
            response.setAnswer("I'm a mock AI assistant. I can help you reorder items from your past orders.");
            response.setSuggestedActions(List.of());
        }

        return response;
    }
}
