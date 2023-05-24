package com.oasis.onlinestore.controller;

import com.oasis.onlinestore.domain.Address;
import com.oasis.onlinestore.domain.User;
import com.oasis.onlinestore.service.UserService;
import com.oasis.onlinestore.service.security.JwtResponseModel;
import com.oasis.onlinestore.service.security.TokenManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    TokenManager tokenManager;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) throws Exception {
        if (userAlreadyExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        String origPassword = user.getPassword();
        // Encode password
        user.setPassword(passwordEncoder.encode(origPassword));
        User newUser = userService.save(user);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), origPassword));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws Exception {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            authenticationManager.authenticate(token);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }




    private boolean userAlreadyExists(String email) {
        return userService.existsByEmail(email);
    }
}
