package com.aurionpro.bank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.UserRepository;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public User getAuthenticatedUser() {
        String username = getLoggedInUsername();
        return username != null ? userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found")) : null;
    }

    public Customer getAuthenticatedCustomer() {
        User user = getAuthenticatedUser();
        return customerRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Customer not found for the authenticated user"));
    }
   
}
