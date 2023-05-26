package com.oasis.onlinestore.service;

import com.oasis.onlinestore.contract.CreditCardResponse;
import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.integration.PaymentServiceClient;
import com.oasis.onlinestore.repository.OrderRepository;
import com.oasis.onlinestore.repository.UserRepository;
import com.oasis.onlinestore.service.security.AuthUtil;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        Item item = itemOpt.get();



        if (found.size() > 0) {
            orderLine = found.get(0);

            // Check stock
            if (item.getQuantity() < orderLine.getQuantity() + 1) {
                return new SimpleResponse(false, "Item is out of stock");
            }

            // Increase order line qty
            orderLine.increaseQuantity();

        } else {
            // Check stock
            if (item.getQuantity() == 0) {
                return new SimpleResponse(false, "Item is out of stock");
            }
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

    public SimpleResponse setShippingAddressToCurrentOrder(UUID uuid) {
        Optional<Order> orderOpt = getCurrentOrder();
        if (orderOpt.isEmpty()) {
            return new SimpleResponse(false, "Could not locate order");
        }
        Order order = orderOpt.get();
        List<Address> addresses = authUtil.getCurrentCustomer().getAddresses();
        List<Address> founds = addresses.stream().filter(a ->
                a.getId().equals(uuid)
                && a.getAddressType() == AddressType.SHIPPING).toList();

        if (founds.size() == 0) {
            return new SimpleResponse(false, "Address is not correct customer shipping address");
        }
        Address address = founds.get(0);
        order.setShippingAddress(address);

        orderRepository.save(order);
        return new SimpleResponse(true, "Successfully set shipping address");
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

    public SimpleResponse checkoutOrder() {
        Optional<Order> orderOpt = getCurrentOrder();

        if (orderOpt.isEmpty()) {
            return new SimpleResponse(false, "Fail to locate this order");
        }

        Order order = orderOpt.get();
        List<OrderLine> lineItems = order.getOrderLines();

        // Check credit card
        if (getUserCreditCards().size() == 0) {
            return new SimpleResponse(false, "There is no credit card available");
        }

        // Check order line
        if (lineItems.size() == 0) {
            return new SimpleResponse(false, "There is no item in your order");
        }

        // Check Status
        if (order.getStatus() != Status.NEW) {
            return new SimpleResponse(false, "Order is already checked out");
        }

        // Decrease item stock
        order.getOrderLines().forEach(line -> {
            Item item = line.getItem();
            int qty = item.getQuantity() - line.getQuantity();
            item.setQuantity(qty);
            itemService.save(item);
        });

        order.setStatus(Status.PLACED);
        Order save = orderRepository.save(order);
        return new SimpleResponse(true, "Successfully placed your order", save);
    }


    public SimpleResponse cancelOrder(UUID orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            if (existingOrder.getStatus() == Status.PLACED) {
                return new SimpleResponse(false,"Cannot cancel the order as it is already placed.");
            } else if (existingOrder.getStatus() == Status.SHIPPED) {
                existingOrder.setStatus(Status.CANCELLED);
                orderRepository.save(existingOrder);
                return new SimpleResponse(true,"Order has been successfully cancelled.");
            }

        } else {
            return new SimpleResponse(false,"Order not found with ID: " + orderId);
        }
        return null;
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




