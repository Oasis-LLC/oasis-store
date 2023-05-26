package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.contract.CreditCardResponse;
import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/billing-address")
    public ResponseEntity<List<Address>> getBillingAddresses() {
        // Get the billing addresses for the currently authenticated user
        List<Address> billingAddresses = userService.getBillingAddresses();

        return new ResponseEntity<List<Address>>(billingAddresses, HttpStatus.OK);

    }

    //POST /users/billing-addess
    @PostMapping("/billing-address")
    public ResponseEntity<?> addBillingAddress(@RequestBody Address address) {
        // Add the billing address to the user
        SimpleResponse res = userService.addBillingAddress(address);
        return new ResponseEntity<SimpleResponse>(res, HttpStatus.OK);
    }

    // update billing address
    @PutMapping("/{userId}/billing-address")
    public ResponseEntity<?> updateBillingAddress(@PathVariable("userId") UUID userId, @RequestBody Address newBillingAddress) {

        SimpleResponse res = userService.updateBillingAddress(userId, newBillingAddress);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


@DeleteMapping("/{addressId}/billing-address")
public SimpleResponse deleteBillingAddress(@PathVariable("addressId") UUID addressId) {
    boolean deleted = userService.deleteBillingAddress(addressId);

        if (deleted) {
            return new SimpleResponse(true, "Successfully deleted billing address", null);
        } else {
            return new SimpleResponse(false, "Billing address not found", null);
        }
    }

    @GetMapping("/shipping-addresses")
    public ResponseEntity<List<Address>> getShippingAddresses() {
        List<Address> shippingAddresses = userService.getShippingAddresses();
        return new ResponseEntity<List<Address>>(shippingAddresses, HttpStatus.OK);
    }

    @PostMapping("/shipping-addresses")
    public ResponseEntity<?> addShippingAddress(@RequestBody Address address) {
        SimpleResponse res = userService.addAddress(address);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/cards")
    public  ResponseEntity<List<CreditCardResponse>> getCardList() {
        List<CreditCardResponse> ccs = userService.getAllCustomerCreditCards();
        return new ResponseEntity<List<CreditCardResponse>>(ccs, HttpStatus.OK);
    }

    @PostMapping("/cards")
    public ResponseEntity<CreditCardResponse> addCard(@RequestBody CreditCardResponse creditCard) {
        return userService.addCreditCard(creditCard);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<CreditCardResponse> getCardById(@PathVariable String cardId) {
        return userService.getCardById(cardId);
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<CreditCardResponse> updateCard(
            @PathVariable String cardId,
            @RequestBody CreditCardResponse creditCard) {
        return userService.updateCreditCard(cardId, creditCard);
    }


    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable String cardId) {
        return userService.deleteCreditCard(cardId);
    }
}
