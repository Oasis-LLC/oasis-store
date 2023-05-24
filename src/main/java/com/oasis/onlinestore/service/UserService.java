package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.AddressType;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.AddressRepository;
import com.oasis.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;


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

    public void addBillingAddress(Address address) {
        // Get the currently authenticated user
        User user = getCurrentUser();

        // Set the address type as billing
        address.setAddressType(AddressType.BILLING);

        // Add the address to the user's addresses
        user.getAddresses().add(address);

        // Save the user to update the addresses
        addressRepository.save(address);
        userRepository.save(user);

    }


    // Authentication ----

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.oasis.onlinestore.domain.User user = userRepository.findByEmail(username);

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
