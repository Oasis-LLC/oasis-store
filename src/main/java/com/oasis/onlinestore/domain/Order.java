package com.oasis.onlinestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity(name = "AppOrder")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private User customer;


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "addressId")
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId")
    private List<OrderLine> orderLines = new ArrayList<>();
    @Enumerated
    private Status status;

    public Order() {
        this.status = Status.NEW;
    }

    public boolean isEditable() {
        return status == Status.NEW;
    }

    public void addLineItem(OrderLine orderLine) {
        this.orderLines.add(orderLine);
    }

    public void removeLineItem(OrderLine orderLine) {
        int index = 0;
        boolean found = false;
        for (int i = 0; i < orderLines.size(); i++) {
            if (orderLines.get(i).getId().equals( orderLine.getId())) {
                index = i;
                found = true;
            }
        }
        if (found) {
            orderLines.remove(index);
        }
    }

}
