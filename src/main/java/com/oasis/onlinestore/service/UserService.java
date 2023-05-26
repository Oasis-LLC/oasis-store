package com.oasis.onlinestore.service;

import com.oasis.onlinestore.contract.SimpleResponse;
import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.AddressType;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.AddressRepository;
import com.oasis.onlinestore.contract.CreditCardResponse;
import com.oasis.onlinestore.integration.PaymentServiceClient;
import com.oasis.onlinestore.repository.UserRepository;
import com.oasis.onlinestore.service.security.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    PaymentServiceClient paymentServiceClient;

    @Autowired
    AuthUtil authUtil;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<Address> getShippingAddresses(){
           User user = authUtil.getCurrentCustomer();
           List<Address> shippingAddresses = user.getAddresses().stream()
                   .filter(i -> i.getAddressType().equals(AddressType.SHIPPING))
                   .toList();
           return shippingAddresses;
    }

    public SimpleResponse addAddress(Address address){
        User user = authUtil.getCurrentCustomer();
        address.setAddressType(AddressType.SHIPPING);
        user.getAddresses().add(address);
        addressRepository.save(address);
        userRepository.save(user);
        return new SimpleResponse(true, "Shipping Address is added", address);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User signup(User user) {
        if (existsByEmail(user.getEmail())) {
            return null;
        }
        return userRepository.save(user);
    }

    private User getCurrentUser() {
        // Get user based on JWT token
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(user.getUsername());
    }

    //Billing Address

    public List<Address> getBillingAddresses() {
        // Get the currently authenticated user
        User user = getCurrentUser();

        // Get the user's addresses
        List<Address> addresses = user.getAddresses();

        // Filter the addresses to get only the billing addresses
        List<Address> billingAddresses = addresses.stream()
                .filter(address -> address.getAddressType() == AddressType.BILLING)
                .collect(Collectors.toList());

        return billingAddresses;
    }

    public SimpleResponse addBillingAddress(Address address) {
        User user = getCurrentUser();
        List<Address> addresses = user.getAddresses();
        for(Address a : addresses) {
            if (a.getAddressType().equals(AddressType.BILLING)) {
                return new SimpleResponse(false, "User has a Billing address");
            }
        }

        address.setAddressType(AddressType.BILLING);
        user.getAddresses().add(address);
        addressRepository.save(address);
        userRepository.save(user);
        return new SimpleResponse(true, "Successfully add billing address", user);

    }

    //update
    public SimpleResponse updateBillingAddress(UUID userId, Address newBillingAddress) {
        Optional<User> optionalUser = userRepository.findById(userId);

        optionalUser.ifPresent(user -> {
            List<Address> addresses = user.getAddresses();
            for (Address address : addresses) {
                if (address.getAddressType() == AddressType.BILLING) {
                    address.setStreet(newBillingAddress.getStreet());
                    address.setCity(newBillingAddress.getCity());
                    address.setState(newBillingAddress.getState());
                    addressRepository.save(address);
                }
            }
            userRepository.save(user);
        });

        if (optionalUser.isEmpty()) {
            return new SimpleResponse(false, "Failed to update");
        }
        return new SimpleResponse(true, "Successfully update address", optionalUser.get());
    }
    public boolean deleteBillingAddress(UUID addressId) {
        User user = authUtil.getCurrentCustomer();

        if (user != null) {
            List<Address> addresses = user.getAddresses();
            // Find the billing address and remove it
            addresses.removeIf(address -> address.getAddressType() == AddressType.BILLING);
            addressRepository.deleteById(addressId);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Cards

    public ResponseEntity<CreditCardResponse> addCreditCard(CreditCardResponse card) {
        User user = authUtil.getCurrentCustomer();
        card.setUserId(user.getId());
        return paymentServiceClient.addCard(card);
    }

    public ResponseEntity<CreditCardResponse> getCardById(String cardId) {
        User user = authUtil.getCurrentCustomer();
        return paymentServiceClient.getCardById(cardId);
    }

    public List<CreditCardResponse> getAllCustomerCreditCards() {
        String userId = authUtil.getCurrentCustomer().getId().toString();
        ResponseEntity<List<CreditCardResponse>> obj = paymentServiceClient.getCreditCards(userId);
        System.out.println(obj.getBody());
        return obj.getBody();
    }

    public ResponseEntity<CreditCardResponse> updateCreditCard(String cardId, CreditCardResponse card) {
        return paymentServiceClient.updateCard(cardId, card);
    }

    public ResponseEntity<?> deleteCreditCard(String cardId) {
        return paymentServiceClient.deleteCard(cardId);
    }

    // Authentication ----

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    getAuthority(user));
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }


    private Set<SimpleGrantedAuthority> getAuthority(com.oasis.onlinestore.domain.User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return authorities;
    }
}
