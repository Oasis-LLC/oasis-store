package com.oasis.onlinestore.service.security;

import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public User getCurrentCustomer() {
        // Get user based on JWT token
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(user.getUsername());
    }
}
