package com.ecommerce.analytics_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CorporateAnalyticsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long lowStockCount;
}