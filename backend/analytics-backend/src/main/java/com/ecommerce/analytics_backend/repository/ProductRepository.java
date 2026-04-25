package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.ecommerce.analytics_backend.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId AND p.stock < 10")
    Long countLowStockProducts(String storeId);
    List<Product> findByStoreId(String storeId);

    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:categoryId IS NULL OR :categoryId = '' OR p.categoryId = :categoryId)")
    Page<Product> searchAndFilterProducts(
            @Param("search") String search, 
            @Param("categoryId") String categoryId, 
            Pageable pageable);
}
