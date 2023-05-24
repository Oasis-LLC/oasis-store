package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.CreditCard;
import com.oasis.onlinestore.repository.CreditCardRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service
public class CreditCardService {

    @Autowired
    CreditCardRepository creditCardRepository;

    public void saveCreditCard(CreditCard cc){
       creditCardRepository.save(cc);
    }

    public List<CreditCard> getAllCreditCards(){
        return creditCardRepository.findAll();
    }
}
