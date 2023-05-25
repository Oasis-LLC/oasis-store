package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.domain.Review;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/orders/{orderId}/order-lines/{orderLineId}")
    public ResponseEntity<?> submitCustomerReview(@RequestBody Review review, @PathVariable UUID orderId, @PathVariable UUID orderLineId) {
        reviewService.submitReview(review, orderId, orderLineId);
        return ResponseEntity.ok(review);
    }

//    @GetMapping("/{itemName}")
//    public ResponseEntity<List<Review>> getReviewsByItem(@PathVariable Item itemName) {
//        List<Review> reviews = reviewService.getReviewsByItem(itemName);
//        return ResponseEntity.ok(reviews);
//    }
}









