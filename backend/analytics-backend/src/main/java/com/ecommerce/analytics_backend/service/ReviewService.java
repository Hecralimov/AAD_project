package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.model.Review;
import com.ecommerce.analytics_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    @Transactional
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(String id, Review review) {
        review.setId(id);
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsForProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }
}
