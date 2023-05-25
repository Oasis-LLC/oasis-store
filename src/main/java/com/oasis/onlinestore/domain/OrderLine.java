package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int quantity;
    private double discount;

    @OneToOne
    @JoinColumn(name = "itemId")
    private Item item;

    public OrderLine() {
    }

    public OrderLine(Item item) {
        this.item = item;
        this.quantity = 1;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        quantity--;
    }

}
