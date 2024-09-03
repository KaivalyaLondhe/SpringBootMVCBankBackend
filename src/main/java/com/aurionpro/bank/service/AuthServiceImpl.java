package com.aurionpro.bank.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.LoginDto;
import com.aurionpro.bank.dto.RegistrationDto;
import com.aurionpro.bank.entity.Admin;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Role;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.exceptions.UserApiException;
import com.aurionpro.bank.repository.AdminRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.RoleRepository;
import com.aurionpro.bank.repository.UserRepository;
import com.aurionpro.bank.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider tokenProvider;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Override
	public User register(RegistrationDto registrationDto) {
	    if (userRepository.existsByUsername(registrationDto.getUsername())) {
	        throw new UserApiException(HttpStatus.BAD_REQUEST, "User already exists");
	    }

	    User user = new User();
	    user.setUsername(registrationDto.getUsername());
	    user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
	    user.setFirstName(registrationDto.getFirstName());
	    user.setLastName(registrationDto.getLastName());
	    
	    Set<Role> roles = new HashSet<>();
	    Role userRole = roleRepository.findByName(registrationDto.getRoleName())
	           .orElseThrow(() -> new UserApiException(HttpStatus.BAD_REQUEST, "Role not found"));
	    roles.add(userRole);
	    user.setRoles(roles);
	    userRepository.save(user);
	    if (registrationDto.getRoleName().equals("ROLE_CUSTOMER")) {
	    	throw new UserApiException(HttpStatus.FORBIDDEN, "Only admins can register users.");
        } else if (registrationDto.getRoleName().equals("ROLE_ADMIN")) {
            Admin admin = new Admin();
            admin.setUser(user);
            admin.setFirstName(user.getFirstName());
            admin.setLastName(user.getLastName());
            adminRepository.save(admin);
        }
	    return user;
	}


	@Override
	public String login(LoginDto loginDto) {
	    try {
	        // Authenticate the user
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
	        );

	        // Set the authentication in the security context
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // Get the authenticated user
	        User user = userRepository.findByUsername(loginDto.getUsername())
	            .orElseThrow(() -> new UserApiException(HttpStatus.NOT_FOUND, "User not found"));

	        // Check if the user has a CUSTOMER role
	        boolean isCustomer = user.getRoles().stream()
	            .anyMatch(role -> role.getName().equalsIgnoreCase("ROLE_CUSTOMER"));

	        if (isCustomer) {
	            // Fetch the customer using the user entity
	            Customer customer = customerRepository.findByUser(user)
	                .orElseThrow(() -> new UserApiException(HttpStatus.NOT_FOUND, "Customer not found"));

	            // Check if the customer is active
	            if (!customer.isActive()) {
	                throw new UserApiException(HttpStatus.FORBIDDEN, "Customer account is inactive");
	            }
	        }

	        // Generate JWT token
	        String token = tokenProvider.generateToken(authentication);
	        return token;

	    } catch (BadCredentialsException e) {
	        throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
	    }
	}



}
