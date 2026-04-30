package com.ecommerce.analytics_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistantAction {
    private String type;
    private String productId;
    private Integer quantity;
    private String label;
    private Double confidence;
}
