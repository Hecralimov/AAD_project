package com.ecommerce.analytics_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "store_id")
    private String storeId;

    private String status;

    @Column(name = "grand_total")
    private BigDecimal grandTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
