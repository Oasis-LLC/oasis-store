package com.oasis.onlinestore.service.security;

import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User getCurrentCustomer() {
        // Get user based on JWT token
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(user.getUsername());
    }
}
