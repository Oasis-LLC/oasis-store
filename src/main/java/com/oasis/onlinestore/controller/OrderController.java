package com.oasis.onlinestore.controller;




import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.domain.LineItem;

import com.oasis.onlinestore.domain.Order;
import com.oasis.onlinestore.domain.OrderLine;
import com.oasis.onlinestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    OrderService orderService;



    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(){
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
    }



    @PostMapping(consumes = { "application/json"})
    public ResponseEntity<?> saveOrder(@RequestBody Order order) {
        try {
            orderService.createOrder(order);
        } catch (Exception e) {
            SimpleResponse res = new SimpleResponse(false, "Failed to create order");
            return new ResponseEntity<SimpleResponse>(res, HttpStatus.BAD_REQUEST);
        }
        SimpleResponse res = new SimpleResponse(true, "Order successfully created");
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.CREATED);
    }


    @GetMapping
    public List<Order> findAll(){
        return orderService.getAllOrders();
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<?> findAllById(@PathVariable String orderId){
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> order = orderService.getOrderById(uuid);
        if (order.isPresent()) {
            return new ResponseEntity<Order>(order.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }

    @GetMapping("/{orderId}/line")
    public ResponseEntity<?> getAllItemLine(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> order = orderService.getOrderById(uuid);
        if (order.isPresent()) {
            List<OrderLine> lineItems = order.get().getLineItems();
            return new ResponseEntity<List<OrderLine>>(lineItems, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{orderId}/addItem")
    public ResponseEntity<?> addItemLineToOrder(@PathVariable String orderId,
                                                @RequestParam("itemId") String itemId) {
        UUID uuid = UUID.fromString(orderId);
        UUID itemUuid = UUID.fromString(itemId);
        Optional<OrderLine> lineItemOptional = orderService.addLineItem(uuid, itemUuid);

        if (lineItemOptional.isPresent()) {
            return new ResponseEntity<OrderLine>(lineItemOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{orderId}/")

    @DeleteMapping("/{orderId}/line/{itemLineId}")
    public ResponseEntity<?> removeItemLineFromOrder(@PathVariable String orderId,
                                                     @PathVariable String itemLineId) {
        UUID uuid = UUID.fromString(orderId);
        UUID itemLineUUID = UUID.fromString(orderId);
        orderService.removeLineItem(uuid, itemLineUUID);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{orderId}/line/checkout")
    public ResponseEntity<?> checkoutOrder(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        orderService.checkoutOrder(uuid);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        orderService.cancelOrder(uuid);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
