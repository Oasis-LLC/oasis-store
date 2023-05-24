package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int numberOfStars;
    private String title;
    private String date;
    private String description;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customerId")
    private User buyer;

    public Review() {

    }

    public Review(int numberOfStars, String title,String date, String description) {
        this.numberOfStars = numberOfStars;
        this.title = title;
        this.date = date;
        this.description = description;
    }
    public void add(Review review) {
        review.add(review);
    }


}
