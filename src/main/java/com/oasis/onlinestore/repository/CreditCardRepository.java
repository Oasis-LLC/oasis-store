package com.oasis.onlinestore.repository;

import com.oasis.onlinestore.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {
}
