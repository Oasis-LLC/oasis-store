package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.Review;
import com.oasis.onlinestore.repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepo reviewRepo;

    public void submitReview(int numberOfStars, String title, String description) {
        Review review = new Review(numberOfStars, title, LocalDate.now().toString(), description);
        reviewRepo.save(review);
    }


    public List<Review>getReviewsByItem(String itemName) {
        return reviewRepo.findByTitle(itemName);
    }



    public List<Review> getAllReviews(){
        return reviewRepo.findAll();
    }




}
