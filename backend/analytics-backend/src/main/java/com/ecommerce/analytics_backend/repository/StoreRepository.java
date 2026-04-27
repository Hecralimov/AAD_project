package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

import com.ecommerce.analytics_backend.dto.StoreComparisonProjection;
import com.ecommerce.analytics_backend.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByOwnerId(String ownerId);

    @Query(value = """
            SELECT
                s.id AS storeId,
                s.name AS storeName,
                (SELECT COALESCE(SUM(o.grand_total), 0) FROM orders o WHERE o.store_id = s.id) AS totalRevenue,
                (SELECT COUNT(o.id) FROM orders o WHERE o.store_id = s.id) AS totalOrders,
                (SELECT COALESCE(AVG(r.star_rating), 0) FROM reviews r JOIN products p ON r.product_id = p.id WHERE p.store_id = s.id) AS averageRating
            FROM stores s
            """, nativeQuery = true)
    List<StoreComparisonProjection> getStoreComparisonAnalytics();
}