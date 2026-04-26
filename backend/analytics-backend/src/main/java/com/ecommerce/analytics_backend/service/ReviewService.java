package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.ReviewRequestDTO;
import com.ecommerce.analytics_backend.model.Review;
import com.ecommerce.analytics_backend.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;

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

    @Transactional
    public void setRating(String id, Integer rating) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(rating);
        reviewRepository.save(review);
    }

    @Transactional
    public void setComment(String id, String comment) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setComment(comment);
        reviewRepository.save(review);
    }

    @Transactional
    public Review submitReview(String userId, ReviewRequestDTO request) {

        Long purchaseCount = orderItemRepository.countUserPurchasesOfProduct(userId, request.getProductId());

        if (purchaseCount == null || purchaseCount == 0) {
            throw new RuntimeException("Forbidden: You can only review products you have actively purchased.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(request.getProductId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review.setSentiment("UNANALYZED");

        return reviewRepository.save(review);
    }
}
