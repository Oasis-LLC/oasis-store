package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.repository.ReviewRepository;
import com.oasis.onlinestore.repository.UserRepository;
import com.oasis.onlinestore.service.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthUtil authUtil;

    public UUID submitReview(Review review, UUID orderId, UUID orderLineID) {
        User user = authUtil.getCurrentCustomer();
        Optional<Order> order = orderService.getOrderById(orderId);
        User buyer = null;

        if (order.isPresent()) {
            buyer = order.get().getCustomer();
            if (!buyer.equals(user)) {
                System.out.println("You must be a buyer of the item to submit a review");
                return null;
            }
            OrderLine orderline = order.get().getOrderLines()
                    .stream().filter(ol -> ol.getId().equals(orderLineID))
                    .findFirst().
                    orElse(null);

            if (orderline == null) {
                System.out.println("Order line not found");
                return null;
            }

            Item item = orderline.getItem();

            if (item == null) {
                System.out.println("Item not found");
                return null;
            }

            item.getReviews().add(review);

            Review savedReview = reviewRepository.save(review);
            itemService.save(item);

            return savedReview.getId();
        }

        return null;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
