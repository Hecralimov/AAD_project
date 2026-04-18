package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.User;

@Repository
// Notice the <User, String> here! It must match the data type of your @Id
// variable.
public interface UserRepository extends JpaRepository<User, String> {
}