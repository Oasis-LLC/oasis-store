package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review,Integer>{


   List<Review> findByTitle(String name);



}
