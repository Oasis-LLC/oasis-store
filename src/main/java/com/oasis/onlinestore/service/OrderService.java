package com.oasis.onlinestore.service;

import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.repository.OrderRepository;
import com.oasis.onlinestore.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
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

    public SimpleResponse addItemToOrder(UUID itemId) {

        Optional<Order> orderOptional = getCurrentOrder();
        if (orderOptional.isEmpty()) {
            return new SimpleResponse(false, "Cannot find order");
        }
        Order order = orderOptional.get();

        if (!order.isEditable()) {
            return new SimpleResponse(false, "Order is not editable");
        }

        Optional<Item> itemOpt = itemService.findById(itemId);
        if (itemOpt.isEmpty()) {
            return new SimpleResponse(false, "No item found");
        }

        // Search item in order line
        List<OrderLine> found = order.getOrderLines()
                .stream()
                .filter(i -> i.getItem().getId().equals(itemId))
                .toList();

        OrderLine orderLine;

        if (found.size() > 0) {
            // Increase order line qty
            orderLine = found.get(0);
            orderLine.increaseQuantity();
        } else {
            // create new line item
             orderLine = new OrderLine(itemOpt.get());
            order.addLineItem(orderLine);
        }
        orderRepository.save(order);
        return new SimpleResponse(true, "Successfully added item to order", orderLine);
    }

    public SimpleResponse removeItemFromOrder(UUID itemId) {

        Optional<Order> orderOptional = getCurrentOrder();
        if (orderOptional.isEmpty()) {
            return new SimpleResponse(false, "Couldn't find order");
        }
        Order order = orderOptional.get();

        if (!order.isEditable()) {
            return new SimpleResponse(false, "Couldn't update order");
        }

        Optional<Item> itemOpt = itemService.findById(itemId);
        if (itemOpt.isEmpty()) {
            return new SimpleResponse(false, "No item found");
        }

        // Search item in order line
        List<OrderLine> found = order.getOrderLines()
                .stream()
                .filter(i -> i.getItem().getId().equals(itemId))
                .toList();

        if (found.size() == 0) {
            return new SimpleResponse(false, "No item found");
        }

        OrderLine orderLine = found.get(0);

        if (orderLine.getQuantity() > 1) {
            // decrease order line qty
            orderLine.decreaseQuantity();
        } else {
            // Remove from order
            order.removeLineItem(orderLine);
        }

        orderRepository.save(order);
        return new SimpleResponse(true, "Successfully removed from order");
    }

    public void checkoutOrder(UUID uuid) {
        /// TO-DO

        // fetch the order with UUID
//        Optional<Order> orderOptional = getOrderById(uuid);
//        if (orderOptional.isPresent()) {
//            Order order = orderOptional.get();
//            if (order.getStatus() != Status.NEW) {
//                // If the order state is not NEW, return or throw an exception to indicate the invalid state
//                return;
//            }
//
//            // Validate credit card
//            BindingResult bindingResult = new BeanPropertyBindingResult(order, "order");
//            creditCardValidator.validate(order.getCustomer().getCreditCards(), bindingResult);
//            if (bindingResult.hasErrors()) {
//                // Handle validation errors, e.g., log errors, return an error response, or throw an exception
//                return;
//            }
//
//            // Do checkout here
//
//            // Update order state
//            order.setStatus(Status.PLACED);
//
//            // Save the updated order
//            orderRepository.save(order);
//        }

        Optional<Order> orderOpt = orderRepository.findById(uuid);
        if(orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderLine> lineItems = order.getOrderLines()
                    .stream()
                    .collect(Collectors.toList());
            if(order.getStatus() != Status.NEW) {
                return;
            }else if(order.getShippingAddress() == null || lineItems.size() == 0
                    || order.getCustomer().getCreditCards().size() == 0){
                return;
            }else{
                order.setStatus(Status.PLACED);
                orderRepository.save(order);
            }
        }

    }
    public void markOrderAsReturned(UUID orderId) {
        // Fetch the order by ID from the database or any other data source
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus() == Status.DELIVERED) {
                order.setStatus(Status.RETURNED);
                orderRepository.save(order);
            } else {
                throw new IllegalStateException("Order must be delivered before it can be marked as returned");
            }
        }

    }

    // Helper methods
    private User getCurrentCustomer() {
        // Get user based on JWT token
        // Testing
        return userRepository.findByEmail("test@gmail.com");
    }

    private Optional<Order> getCurrentOrder() {
        // return new existing order or create new order
        User customer = getCurrentCustomer();
        if (customer == null) {
            return Optional.empty();
        }

        Order newOrder = customer.getCurrentOrder();
        if (newOrder == null) {
            newOrder = new Order(customer, Status.NEW);
            customer.addOrder(newOrder);
            // save to userRepository
            userRepository.save(customer);
        }
        return Optional.of(newOrder);
    }

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
}




