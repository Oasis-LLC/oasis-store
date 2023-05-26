package com.oasis.onlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OnlineStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineStoreApplication.class, args);
	}
}
