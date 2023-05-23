package com.oasis.onlinestore.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Data
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Size(max = 16, min = 15)
    private String cardNumber;
    @Future
    private YearMonth expirationDate;
    @NotBlank
    private String nameOnCard;
    @Size(max = 3, min = 3)
    private String securityCode;

    public CreditCard(){

    }

    public CreditCard(String cardNumber, int year, int month, String nameOnCard, String securityCode){
        this.cardNumber = cardNumber;
        this.expirationDate = YearMonth.of(year, month);
        this.nameOnCard = nameOnCard;
        this.securityCode = securityCode;

    }
}
