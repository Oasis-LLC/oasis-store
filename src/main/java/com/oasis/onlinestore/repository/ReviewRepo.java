package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepo extends JpaRepository<Review, UUID> {

   List<Review> findByItem(Item item);
    List<Review> findAll();
}
















