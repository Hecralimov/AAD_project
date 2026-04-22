package com.ecommerce.analytics_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDistributionDTO {
    private String roleType;
    private Long userCount;
}