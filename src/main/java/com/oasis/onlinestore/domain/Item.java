package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @Lob
    private String image;

    private String barcode;

    private int quantity;

    private double price;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "BundleItem",
            joinColumns = { @JoinColumn(name = "itemId", referencedColumnName = "id")},
            inverseJoinColumns =  { @JoinColumn(name = "subItemId", referencedColumnName = "id")})
    private List<Item> items = new ArrayList<>(); // composite

    public Item(String name, String description, String image, String barcode, int quantity, double price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.barcode = barcode;
        this.quantity = quantity;
        this.price = price;
    }

    boolean isValid() {
        if (quantity < 0) {
            return false;
        } else if (price < 0) {
            return false;
        } else if (barcode == null) {
            return false;
        }

        return true;
    }
}
