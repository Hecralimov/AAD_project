package com.ecommerce.analytics_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SentimentDistributionDTO {
    private String sentiment;
    private Long count;
}