package com.ecommerce.analytics_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CorporateAnalyticsDTO {
    private BigDecimal totalRevenue;
    private Long orderCount;
    private Long lowStockCount;
    // We will leave 'topProducts' out for this immediate step to keep the scope tight, 
    // or you can add a List<String> here later.
}