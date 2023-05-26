package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.contract.OrderRequestBody;
import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.Order;
import com.oasis.onlinestore.domain.OrderLine;
import com.oasis.onlinestore.service.OrderService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping
    public List<Order> findAll() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> findAllById(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> order = orderService.getOrderById(uuid);
        if (order.isPresent()) {
            return new ResponseEntity<Order>(order.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{orderId}/lines")
    public ResponseEntity<?> getAllItemLine(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Optional<Order> order = orderService.getOrderById(uuid);
        if (order.isPresent()) {
            List<OrderLine> orderLines = order.get().getOrderLines();
            return new ResponseEntity<List<OrderLine>>(orderLines, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/additem")
    public ResponseEntity<SimpleResponse> addItemLineToOrder(@RequestBody OrderRequestBody body) {
        UUID itemUuid = UUID.fromString(body.getItemId());
        SimpleResponse res = orderService.addItemToOrder(itemUuid);
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.OK);
    }

    @PostMapping("/removeitem")
    public ResponseEntity<SimpleResponse> removeItemLineFromOrder(@RequestBody OrderRequestBody body) {
        UUID itemUuid = UUID.fromString(body.getItemId());
        SimpleResponse res = orderService.removeItemFromOrder(itemUuid);
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.OK);
    }

    @PostMapping("/set-address")
    public ResponseEntity<SimpleResponse> setShippingAddressToOrder(@RequestBody OrderRequestBody body) {
        UUID itemUuid = UUID.fromString(body.getItemId());
        SimpleResponse res = orderService.setShippingAddressToCurrentOrder(itemUuid);
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutOrder() {
        SimpleResponse res = orderService.checkoutOrder();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        orderService.cancelOrder(uuid );
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @PostMapping("/{orderId}/returned")
    public ResponseEntity<SimpleResponse> markOrderAsReturned(@PathVariable String orderId) {
        UUID uuid = UUID.fromString(orderId);
        SimpleResponse res = orderService.markOrderAsReturned(uuid);
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.OK);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping("/{orderId}/process")
    public ResponseEntity<?> processOrder(@PathVariable String orderId) {
        SimpleResponse res = orderService.processOrder(orderId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable String orderId) {
        SimpleResponse res = orderService.shipOrder(orderId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}




