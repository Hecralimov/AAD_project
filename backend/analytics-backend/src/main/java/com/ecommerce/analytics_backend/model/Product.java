package com.ecommerce.analytics_backend.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_store_id", columnList = "store_id"),
    @Index(name = "idx_products_category_id", columnList = "category_id")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "category_id")
    private String categoryId;

    private int stock;

    private String sku;

    private String name;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}
