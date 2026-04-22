package com.ecommerce.analytics_backend.dto;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    String getMonth();
    BigDecimal getAmount();
}