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
	public void run(String... args) throws Exception {
		Item item1 = new Item(
				"Stationary baby swings",
				"Baby Swing for Infants, Electric Portable Baby Swing for Newborn, Bluetooth Touch Screen/Remote Control Timi",
				"image",
				"183837478",
				12, 35.0
		);
		OrderLine ol = new OrderLine(item1);
		Item item2 = new Item(
				"Alienware Ultrawide Curved Gaming Monitor 38 Inch",
				"144Hz Refresh Rate, 3840 x 1600 WQHD , IPS, NVIDIA G-SYNC Ultimate, 1ms Response Time, 2300R Curvature, VESA Display HDR 600, AW3821DW - White",
				"someimage",
				"183837478",
				2, 45.0
		);
		OrderLine ol2 = new OrderLine(item2);
		List<OrderLine>ols = new ArrayList<>();
		ols.add(ol);
		ols.add(ol2);
		itemService.save(item1);
		itemService.save(item2);

		Order o1 = new Order();
		//Order o2 = new Order();
		List<Order> orders = new ArrayList<>();
		orders.add(o1);
		//orders.add(o2);

		o1.setStatus(Status.NEW);
		Address testAddress = new Address("123 Main St", "City", "State", AddressType.SHIPPING  );
		User cust = new User("John", "Bob", "johnBob@gmail.com");
		userRepository.save(cust);
		o1.setShippingAddress(testAddress);
		cust.setOrders(orders);
		o1.setLineItems(ols);
		orderRepo.save(o1);

		Order o2 = new Order();
		orders.add(o2);
		o2.setStatus(Status.SHIPPED);
		o2.setShippingAddress(testAddress);
		cust.setOrders(orders);
		//o2.setLineItems(ols);
		orderRepo.save(o2);



		List<Order> foundOrders = orderRepo.findByShippingAddress(testAddress);
		List<Order> findCustomer = orderRepo.findByCustomer(cust);


		for (Order order:findCustomer) {
			System.out.println(order);

		}
		testUserService();
		//testItemService();

		testCreditCardValidation();
	}

	private void testUserService() {
		User user = new User("Test", "User", "test@gmail.com", Role.CUSTOMER);
		userRepository.save(user);
	}

	private void testItemService() {
		Item item1 = new Item(
				"Stationary baby swings",
				"Baby Swing for Infants, Electric Portable Baby Swing for Newborn, Bluetooth Touch Screen/Remote Control Timi",
				"image",
				"183837478",
				12, 35.0
		);
		OrderLine ol = new OrderLine(item1);
		Item item2 = new Item(
				"Alienware Ultrawide Curved Gaming Monitor 38 Inch",
				"144Hz Refresh Rate, 3840 x 1600 WQHD , IPS, NVIDIA G-SYNC Ultimate, 1ms Response Time, 2300R Curvature, VESA Display HDR 600, AW3821DW - White",
				"someimage",
				"183837478",
				2, 45.0
		);
		OrderLine ol2 = new OrderLine(item2);
		List<OrderLine>ols = new ArrayList<>();
		ols.add(ol);
		ols.add(ol2);
		itemService.save(item1);
		itemService.save(item2);

		List<Item> foundItems = itemService.findNameLike("Alienware");
		System.out.println(foundItems);

		List<Item> foundAllItems = itemService.findAll();
		System.out.println(foundAllItems);
	}

	private void testCreditCardValidation(){
		CreditCard cc = new CreditCard("6788678867886788", 2023, 06, "John D. Doe", "456");
		creditCardService.saveCreditCard(cc);
		for(CreditCard ccd : creditCardService.getAllCreditCards()){
			System.out.println(ccd);
		}
	}
}
