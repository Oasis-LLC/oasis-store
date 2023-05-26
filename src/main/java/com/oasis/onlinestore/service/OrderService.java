package com.oasis.onlinestore.service;

import com.oasis.onlinestore.contract.CreditCardResponse;
import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.integration.PaymentServiceClient;
import com.oasis.onlinestore.repository.OrderRepository;
import com.oasis.onlinestore.repository.UserRepository;
import com.oasis.onlinestore.service.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    AuthUtil authUtil;

    @Autowired
    PaymentServiceClient paymentServiceClient;

    public List<Order> getAllOrders(){
        User user = authUtil.getCurrentCustomer();
        return user.getOrders();
    }

    public Optional<Order> getOrderById(UUID orderID){
        if(orderRepository.findById(orderID).isPresent()) {
            return orderRepository.findById(orderID);
        }else{
            return Optional.empty();
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




    public SimpleResponse markOrderAsReturned(UUID orderId) {
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
        return new SimpleResponse(true, "Successfully returned");
    }

    // Helper methods

    private Optional<Order> getCurrentOrder() {
        // return new existing order or create new order
        User customer = authUtil.getCurrentCustomer();
        if (customer == null) {
            return Optional.empty();
        }

        Order newOrder = customer.getCurrentOrder();
        if (newOrder == null) {
            newOrder = new Order();
            customer.addOrder(newOrder);
            // save to userRepository
            User user = userRepository.save(customer);
            newOrder = user.getCurrentOrder();
        }
        return Optional.of(newOrder);
    }

    public void checkoutOrder(UUID uuid) {

        Optional<Order> orderOpt = orderRepository.findById(uuid);
        if(orderOpt.isPresent()) {
            Order order = orderOpt.get();

            List<OrderLine> lineItems = order.getOrderLines()
                    .stream()
                    .collect(Collectors.toList());
            if(order.getStatus() == Status.NEW) {
                if(order.getShippingAddress() != null && lineItems.size() > 0){
                    order.setStatus(Status.PLACED);
                    orderRepository.save(order);
                }
            }
        }
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

    public SimpleResponse processOrder(String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> orderOptional = orderRepository.findById(uuid);
        if (orderOptional.isEmpty()) {
            return new SimpleResponse(false, "Couldn't locate order");
        }

        Order order = orderOptional.get();

        // Check status
        if (order.getStatus() != Status.PLACED) {
            return new SimpleResponse(false, "Order is not placed");
        }

        // Check credit card
        if (getUserCreditCards().isEmpty()) {
            return new SimpleResponse(false, "Payment method is not available");
        }

        // Change state to Proceed
        order.setStatus(Status.PROCESSED);

        // TODO - Email notification to customer

        orderRepository.save(order);
        return new SimpleResponse(true, "The order is being processed");
    }

    public SimpleResponse shipOrder(String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> orderOptional = orderRepository.findById(uuid);
        if (orderOptional.isEmpty()) {
            return new SimpleResponse(false, "Couldn't locate order");
        }

        Order order = orderOptional.get();

        // Check status
        if (order.getStatus() != Status.PROCESSED) {
            return new SimpleResponse(false, "Order is not placed");
        }

        // Check credit card
        if (getUserCreditCards().isEmpty()) {
            return new SimpleResponse(false, "Payment method is not available");
        }

        // Change state to Proceed
        order.setStatus(Status.SHIPPED);

        orderRepository.save(order);

        // TODO - Email notification to customer

        return new SimpleResponse(true, "The order is being processed");
    }

    private List<CreditCardResponse> getUserCreditCards() {
        UUID id = authUtil.getCurrentCustomer().getId();
        ResponseEntity<List<CreditCardResponse>> cc = paymentServiceClient.getCreditCards(id.toString());
        return cc.getBody();
    }
}




