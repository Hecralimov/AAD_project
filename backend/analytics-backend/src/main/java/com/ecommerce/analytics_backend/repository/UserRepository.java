package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.User;

import com.ecommerce.analytics_backend.dto.RoleDistributionDTO;

import java.util.List;
import java.util.Optional;

@Repository
// Notice the <User, String> here! It must match the data type of your @Id
// variable.
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
            FROM users
            WHERE email = :email
            ORDER BY (is_active IS NULL) DESC, COALESCE(is_active, 0) DESC, id DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query("SELECT new com.ecommerce.analytics_backend.dto.RoleDistributionDTO(u.roleType, COUNT(u)) " +
           "FROM User u GROUP BY u.roleType")
    List<RoleDistributionDTO> getRoleDistribution();
}
