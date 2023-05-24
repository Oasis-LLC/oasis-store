package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.domain.Review;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.oasis.onlinestore.domain.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepo reviewRepo;


   public void submitReview(Review review, User user, Item item) {
       if(user == null) {
           System.out.println("You must be logged in to submit a review");
       }
       if (user.getRole() != user.getRole().CUSTOMER ) {
           System.out.println("You must be a Customer to submit a review\"");
       } else
         reviewRepo.save(review);

   }
   public List<Review>getReviewsByItem(Item item) {
        return reviewRepo.findByItem(item);
    }


    public List<Review> getAllReviews(){
        return reviewRepo.findAll();
    }



}
