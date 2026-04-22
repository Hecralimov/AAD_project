package com.ecommerce.analytics_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.dto.SentimentDistributionDTO;
import com.ecommerce.analytics_backend.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    @Query("SELECT new com.ecommerce.analytics_backend.dto.SentimentDistributionDTO(r.sentiment, COUNT(r)) " +
           "FROM Review r " +
           "GROUP BY r.sentiment")
    List<SentimentDistributionDTO> getSentimentDistribution();
}
