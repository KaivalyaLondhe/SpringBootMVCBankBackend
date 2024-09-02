package com.aurionpro.bank.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.UserDto;
import com.aurionpro.bank.entity.Admin;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Role;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.repository.AdminRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.RoleRepository;
import com.aurionpro.bank.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
	private PasswordEncoder passwordEncoder;
    
    @Override
    public PageResponse<UserDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
            .map(this::toUserDtoMapper)
            .collect(Collectors.toList());

        return new PageResponse<>(
            userPage.getTotalPages(),
            userPage.getTotalElements(),
            size,
            userDtos,
            userPage.isLast()
        );
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
      
        Role role = roleRepository.findByName(userDto.getRoleName())
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + userDto.getRoleName()));

    
        User user = toUserMapper(userDto);
        user.setRoles(Set.of(role)); 

    
        User savedUser = userRepository.save(user);

      
        if (role.getName().equals("ROLE_CUSTOMER")) {
            logger.info("Creating a customer for user: {}", savedUser.getUsername());
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setFirstName(user.getFirstName());
            customer.setLastName(user.getLastName());
            customerRepository.save(customer);
            logger.info("Customer saved for user: {}", savedUser.getUsername());
        } else if (role.getName().equals("ROLE_ADMIN")) {
            logger.info("Creating an admin for user: {}", savedUser.getUsername());
            Admin admin = new Admin();
            admin.setUser(savedUser);
            admin.setFirstName(user.getFirstName());
            admin.setLastName(user.getLastName());
            adminRepository.save(admin);
            logger.info("Admin saved for user: {}", savedUser.getUsername());
        }


        return toUserDtoMapper(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));  
        }
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }

        if (userDto.getRoleName() != null) {
            Role role = roleRepository.findByName(userDto.getRoleName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + userDto.getRoleName()));
            user.setRoles(Set.of(role));
        }

        User updatedUser = userRepository.save(user);

        
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CUSTOMER"))) {
            Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with user ID: " + userId));
            if (userDto.getFirstName() != null) {
                customer.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() != null) {
                customer.setLastName(userDto.getLastName());
            }
            customerRepository.save(customer);
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with user ID: " + userId));
            if (userDto.getFirstName() != null) {
                admin.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() != null) {
                admin.setLastName(userDto.getLastName());
            }
            adminRepository.save(admin);
        }

        return toUserDtoMapper(updatedUser);
    }

    @Override
    public UserDto toUserDtoMapper(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(passwordEncoder.encode(user.getPassword()));
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoleName(user.getRoles().iterator().next().getName()); 
        return userDto;
    }

    @Override
    public User toUserMapper(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        Role role = roleRepository.findByName(userDto.getRoleName())
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + userDto.getRoleName()));
        user.setRoles(Set.of(role));

        return user;
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));
    }
}
