package com.oasis.onlinestore.contract;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class CreditCardResponse {
    private String id;
    private String nameOnCard;
    private String cardNumber;
    private int securityCode;
    private int expirationDate;
    private UUID userId;
}
