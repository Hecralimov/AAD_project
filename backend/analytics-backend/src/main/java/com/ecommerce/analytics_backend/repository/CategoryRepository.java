package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
}
