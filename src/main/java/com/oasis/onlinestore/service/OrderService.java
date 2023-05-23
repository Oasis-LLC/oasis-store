package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.repository.OrderRepository;
import com.oasis.onlinestore.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;



    public void createOrder(Order o){
        orderRepository.save(o);
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemService itemService;


    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderID){
        if(orderRepository.findById(orderID).isPresent()) {
            return orderRepository.findById(orderID);
        }else{
            return null;
        }

    }

    public Optional<LineItem> addLineItem(UUID orderId, UUID itemId) {

        // Validate order, check status,
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();
        Optional<Item> itemOpt = itemService.findById(itemId);
        if (itemOpt.isEmpty()) {
            return Optional.empty();
        }

        // Search item in order line
        List<LineItem> found = order.getLineItems()
                .stream()
                .filter(i -> i.getItem().getId().equals(itemId))
                .toList();

        LineItem lineItem;

        if (found.size() > 0) {
            // Increase order line qty
            lineItem = found.get(0);
            lineItem.increaseQuantity();
        } else {
            // create new line item
             lineItem = new LineItem(itemOpt.get());
            order.addLineItem(lineItem);
        }
        orderRepository.save(order);
        return Optional.of(lineItem);
    }

    public void removeLineItem(UUID orderId, UUID lineId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.removeLineItem(lineId);
            orderRepository.save(order);
        }
    }



    public void checkoutOrder(UUID uuid) {

        Optional<Order> orderOpt = orderRepository.findById(uuid);
        if(orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<LineItem> lineItems = order.getLineItems()
                    .stream()
                    .collect(Collectors.toList());
            if(order.getStatus() != Status.NEW) {
                return;
            }else if(order.getShippingAddress() != null && lineItems.size() > 0){
                return;
            }else{
                order.setStatus(Status.PLACED);
                orderRepository.save(order);
            }
        }



    }

    // Helper methods

    public void cancelOrder(UUID orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            if (existingOrder.getStatus() == Status.PLACED) {
                System.out.println("Cannot cancel the order as it is already placed.");
            } else if (existingOrder.getStatus() == Status.NEW) {
                existingOrder.setStatus(Status.CANCELLED);
                //orderRepository.save(existingOrder);
                System.out.println("Order has been successfully cancelled.");
            }
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }

    }

    private User getCurrentCustomer() {
        // Get user based on JWT token
        // Testing
        return new User();
    }

    private Order getCurrentOrder() {
        // return new existing order or create new order
        User customer = getCurrentCustomer();
        Order newOrder = customer.getCurrentOrder();
        if (newOrder == null) {
            newOrder = new Order(customer, Status.NEW);
            customer.addOrder(newOrder);
            // save to userRepository
            userRepository.save(customer);
        }
        return newOrder;
    }


}





