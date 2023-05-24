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

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<?> submitCustomerReview(@RequestBody Review review, Item item, User buyer) {
        reviewService.submitReview(review, buyer, item);
        return ResponseEntity.ok(review);
    }


  @GetMapping("/{itemName}")
  public ResponseEntity<List<Review>>getReviewsByItem(@PathVariable Item itemName) {
    List<Review> reviews = reviewService.getReviewsByItem(itemName);
    return ResponseEntity.ok(reviews);
  }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
}









