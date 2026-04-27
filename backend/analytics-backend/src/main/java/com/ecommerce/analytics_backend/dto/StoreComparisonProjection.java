package com.ecommerce.analytics_backend.dto;

public interface StoreComparisonProjection {
    String getStoreId();

    String getStoreName();

    Double getTotalRevenue();

    Long getTotalOrders();

    Double getAverageRating();
}