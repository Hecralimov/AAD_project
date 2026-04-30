package com.ecommerce.analytics_backend.service.ai;

import com.ecommerce.analytics_backend.dto.AssistantChatRequest;
import com.ecommerce.analytics_backend.dto.AssistantChatResponse;

import java.util.Map;

public interface AiProvider {
    AssistantChatResponse generateResponse(AssistantChatRequest request, Map<String, Object> context, String role, String contextId);
}
