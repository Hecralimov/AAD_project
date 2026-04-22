package com.ecommerce.analytics_backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySalesDTO {
    private String categoryName;
    private BigDecimal totalSales; // price ve grand_total decimal(38,2) olduğu için BigDecimal en sağlıklısı
}