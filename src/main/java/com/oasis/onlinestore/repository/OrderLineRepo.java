package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface OrderLineRepo extends JpaRepository<OrderLine, UUID>{

//  List<LineItem> findByOrderId(Long orderId);

}
