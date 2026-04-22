package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    // Spring Data JPA derives the query automatically based on method name
    Long countByStatus(String status);
}