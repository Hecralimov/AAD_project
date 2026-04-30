package com.ecommerce.analytics_backend.service.ai;

import com.ecommerce.analytics_backend.dto.AssistantChatRequest;
import com.ecommerce.analytics_backend.dto.AssistantChatResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "assistant.provider", havingValue = "langgraph")
public class LangGraphAiProvider implements AiProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.langgraph.url:http://localhost:8000/api/chat/ask}")
    private String langgraphUrl;

    @Override
    public AssistantChatResponse generateResponse(AssistantChatRequest request, Map<String, Object> context,
            String role, String contextId) {
        LangGraphChatRequest langGraphRequest = LangGraphChatRequest.builder()
                .message(request.getMessage())
                .role(role)
                .context_id(contextId)
                .build();

        try {
            LangGraphChatResponse langGraphResponse = restTemplate.postForObject(
                    langgraphUrl,
                    langGraphRequest,
                    LangGraphChatResponse.class);

            AssistantChatResponse response = new AssistantChatResponse();
            response.setConversationId(
                    request.getConversationId() != null ? request.getConversationId() : UUID.randomUUID().toString());
            response.setReferencedData(context);

            if (langGraphResponse != null) {
                response.setAnswer(langGraphResponse.getFinal_answer());
                response.setVisualizationCode(langGraphResponse.getVisualization_code());
            } else {
                response.setAnswer("Error: Received empty response from AI service.");
            }
            return response;

        } catch (ResourceAccessException e) {
            log.error("Failed to connect to LangGraph service at {}: {}", langgraphUrl, e.getMessage());
            return buildFallbackResponse(request, context,
                    "The AI assistant is currently analyzing a heavy load. Please try again in a moment.");
        } catch (Exception e) {
            log.error("Unexpected error calling LangGraph service: {}", e.getMessage());
            return buildFallbackResponse(request, context,
                    "An unexpected error occurred while processing your request. Please try again later.");
        }
    }

    private AssistantChatResponse buildFallbackResponse(AssistantChatRequest request, Map<String, Object> context,
            String message) {
        AssistantChatResponse response = new AssistantChatResponse();
        response.setConversationId(
                request.getConversationId() != null ? request.getConversationId() : UUID.randomUUID().toString());
        response.setReferencedData(context);
        response.setAnswer(message);
        return response;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LangGraphChatRequest {
        private String message;
        private String role;
        private String context_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LangGraphChatResponse {
        private String final_answer;
        private String visualization_code;
    }
}
