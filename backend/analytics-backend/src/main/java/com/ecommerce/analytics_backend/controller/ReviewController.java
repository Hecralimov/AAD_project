package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.ReviewRequestDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.model.Review;
import com.ecommerce.analytics_backend.repository.UserRepository;
import com.ecommerce.analytics_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(id, review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Review> submitReview(Principal principal, @RequestBody ReviewRequestDTO request) {
        if (principal == null)
            throw new RuntimeException("Unauthorized: No active session.");

        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review savedReview = reviewService.submitReview(currentUser.getId(), request);

        return ResponseEntity.ok(savedReview);
    }
}
