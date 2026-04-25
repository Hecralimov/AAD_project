package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.ecommerce.analytics_backend.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByOwnerId(String ownerId);
}