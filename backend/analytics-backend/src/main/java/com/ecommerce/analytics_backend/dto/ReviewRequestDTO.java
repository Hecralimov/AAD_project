package com.ecommerce.analytics_backend.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private String productId;
    private Integer rating;
    private String comment;
}