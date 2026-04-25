package com.ecommerce.analytics_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class IndividualAnalyticsDTO {
    private BigDecimal totalSpent;
    private Long orderCount;
    private List<Map<String, Object>> statusDistribution;
}