package com.oasis.onlinestore;

import com.oasis.onlinestore.domain.*;
import com.oasis.onlinestore.repository.CreditCardRepository;
import com.oasis.onlinestore.repository.OrderRepository;
import com.oasis.onlinestore.repository.UserRepository;
import com.oasis.onlinestore.service.CreditCardService;
import com.oasis.onlinestore.service.ItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class OnlineStoreApplication implements CommandLineRunner {
	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepo;
	@Autowired
	ItemService itemService;

	@Autowired
	CreditCardService creditCardService;

	@Autowired
	CreditCardRepository creditCardRepository;

	public static void main(String[] args) {
		SpringApplication.run(OnlineStoreApplication.class, args);
	}

	@Transactional
	@Override

	public void run(String... args) throws Exception {}
}
