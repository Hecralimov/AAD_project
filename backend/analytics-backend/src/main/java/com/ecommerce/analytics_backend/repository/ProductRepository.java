package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.ecommerce.analytics_backend.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId AND p.stock < 10")
    Long countLowStockProducts(String storeId);
    List<Product> findByStoreId(String storeId);
}
