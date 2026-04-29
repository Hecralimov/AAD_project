package com.ecommerce.analytics_backend.dto;

import lombok.Data;

@Data
public class CustomerProfileDTO {
    private String id;
    private String email;
    private String roleType;
    private Boolean active;
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String country;
}
