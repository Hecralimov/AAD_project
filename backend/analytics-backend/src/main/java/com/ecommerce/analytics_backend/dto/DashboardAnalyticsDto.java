package com.ecommerce.analytics_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDto {
    private KpiDto kpis;
    private List<CategorySalesDto> categorySales;
    private List<MonthlyRevenueDto> monthlyRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiDto {
        private String totalRevenue;
        private Long totalOrders;
        private Long activeUsers;
        private Long pendingShipments;
        private String revenueTrend;
        private String ordersTrend;
        private boolean revenuePositive;
        private boolean ordersPositive;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategorySalesDto {
        private String categoryName;
        private Long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRevenueDto {
        private String month;
        private BigDecimal amount;
    }
}
