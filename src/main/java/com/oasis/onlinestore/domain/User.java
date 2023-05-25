package com.oasis.onlinestore.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "UserEntity")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;


    private String password;



    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId")
    private List<CreditCard> creditCards = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @Enumerated
    private Role role = Role.CUSTOMER;



    public User(){}







    public Order getCurrentOrder() {
        List<Order> newList = orders.stream().filter(x-> x.getStatus() == Status.NEW).toList();
        if (newList.size() > 0) {
            return newList.get(0);
        }
        return null;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public User(String firstName, String lastName, String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

}

// An example JSON body for a POST request to /api/auth/signup:
// {
//     "firstName": "John",
//     "lastName": "Doe",
//     "email": "john.doe@example",
//     "password": "password"
// }
