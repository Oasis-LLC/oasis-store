package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class LineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int quantity;
    private double discount;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "itemId")
    private Item item;

    public LineItem(){

    }
    public LineItem(Item item) {
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
