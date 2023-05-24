package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity(name = "Purchase")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "addressId")
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId")
    private List<OrderLine> lineItems = new ArrayList<>();
    @Enumerated
    private Status status;

    public boolean isEditable() {
       return status == Status.NEW;
    }

    public void addLineItem(OrderLine lineItem) {
        this.lineItems.add(lineItem);
    }

    public void removeLineItem(UUID uuid) {
        lineItems = lineItems.stream().filter(x -> !x.getId().equals(uuid)).toList();
    }

    public Order() {
    }

    public Order(User customer, Status status) {
        this.customer = customer;
        this.status = status;
    }
}
