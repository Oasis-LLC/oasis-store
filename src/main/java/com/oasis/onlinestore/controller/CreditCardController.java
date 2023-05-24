package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.domain.CreditCard;
import com.oasis.onlinestore.domain.Order;

import com.oasis.onlinestore.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/creditcard")
public class CreditCardController {

    @Autowired
    CreditCardService ccs;

    @GetMapping
    public ResponseEntity<?> getCCS(){
        List<CreditCard> cc = ccs.getAllCreditCards();
        return new ResponseEntity<List<CreditCard>>(cc, HttpStatus.OK);

    }

    @PostMapping(consumes = { "application/json"})
    public ResponseEntity<?> saveCreditCard(@Valid @RequestBody CreditCard cc) {
        try {
            ccs.saveCreditCard(cc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<CreditCard>(cc, HttpStatus.CREATED);
    }
}
