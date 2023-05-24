package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.Item;
import com.oasis.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/billing-address")
    public ResponseEntity<List<Address>> getBillingAddresses() {
        // Get the billing addresses for the currently authenticated user
        List<Address> billingAddresses = userService.getBillingAddresses();

        return ResponseEntity.ok(billingAddresses);
    }

    //POST /users/billing-addess
    @PostMapping("/billing-address")
    public ResponseEntity<Void> addBillingAddress(@RequestBody Address address) {
        // Add the billing address to the user
        userService.addBillingAddress(address);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/shipping-addresses")
    public ResponseEntity<List<Address>> getShippingAddresses() {
        List<Address> shippingAddresses = userService.getShippingAddresses();
        return new ResponseEntity<List<Address>>(shippingAddresses, HttpStatus.OK);
    }

    @PostMapping("/shipping-addresses")
    public ResponseEntity<?> addShippingAddress(@RequestBody Address address) {
        userService.addAddress(address);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
