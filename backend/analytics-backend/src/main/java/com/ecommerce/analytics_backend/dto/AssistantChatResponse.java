package com.ecommerce.analytics_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistantChatResponse {
    private String conversationId;
    private String answer;
    private List<AssistantAction> suggestedActions;
    private Map<String, Object> referencedData;
    private String visualizationCode;
}
