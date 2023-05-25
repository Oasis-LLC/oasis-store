package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.Order;
import com.oasis.onlinestore.domain.Status;
import com.oasis.onlinestore.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByShippingAddress(Address address);
    List<Order> findByStatus(Status status);

}
