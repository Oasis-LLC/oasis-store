package com.oasis.onlinestore.service;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.AddressType;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.AddressRepository;
import com.oasis.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<Address> getShippingAddresses(){
           User user = getCurrentCustomer();
           List<Address> shippingAddresses = user.getAddresses().stream()
                   .filter(i -> i.getAddressType().equals(AddressType.SHIPPING))
                   .toList();
           return shippingAddresses;
    }

    public void addAddress(Address address){
        User user = getCurrentCustomer();
        user.getAddresses().add(address);
        addressRepository.save(address);
        userRepository.save(user);

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

    private User getCurrentCustomer() {
        // Get user based on JWT token
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(user.getUsername());
    }

    private Set<SimpleGrantedAuthority> getAuthority(com.oasis.onlinestore.domain.User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return authorities;
    }
}
