package com.ecommerce.analytics_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    private String storeId;
    private List<CartItemDTO> items;

    @Data
    public static class CartItemDTO {
        private String productId;
        private Integer quantity;
    }
}