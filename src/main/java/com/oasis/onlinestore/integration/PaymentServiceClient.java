package com.oasis.onlinestore.integration;

import com.oasis.onlinestore.contract.CreditCardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "PaymentMicroservice", url = "http://localhost:9000/")
public interface PaymentServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/cards/user/{userId}")
    ResponseEntity<List<CreditCardResponse>> getCreditCards(@PathVariable String userId);

    @RequestMapping(method = RequestMethod.GET, value = "/cards/{cardId}")
    ResponseEntity<CreditCardResponse> getCardById(@PathVariable String cardId);

    @RequestMapping(method = RequestMethod.POST, value = "/cards/add")
    ResponseEntity<CreditCardResponse> addCard(CreditCardResponse card);



    @RequestMapping(method = RequestMethod.PUT, value = "/cards/update/{cardId}")
    ResponseEntity<CreditCardResponse> updateCard(@PathVariable String cardId, CreditCardResponse card);

    @RequestMapping(method = RequestMethod.DELETE, value = "/cards/delete/{cardId}")
    ResponseEntity<CreditCardResponse> deleteCard(@PathVariable String cardId);

}
