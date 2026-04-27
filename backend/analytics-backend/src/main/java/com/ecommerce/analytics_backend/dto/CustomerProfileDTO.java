package com.ecommerce.analytics_backend.dto;

import lombok.Data;

@Data
public class CustomerProfileDTO {
    private String fullName;
    private String phoneNumber;
    private String addressLine;
    private String city;
    private String country;
}