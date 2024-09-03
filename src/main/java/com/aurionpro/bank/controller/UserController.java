package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.UserDto;
import com.aurionpro.bank.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/bank/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Endpoint to get all users - Restricted to admins
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get all users, page: {}, size: {}", page, size);
        PageResponse<UserDto> response = userService.getAllUsers(page, size);
        logger.info("Fetched {} users", response.getContent().size());
        return ResponseEntity.ok(response);
    }

    // Endpoint to add a user - Restricted to admins
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Request to add user: {}", userDto);
        UserDto createdUser = userService.addUser(userDto);
        logger.info("User created successfully with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Endpoint to update a user - Restricted to admins
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto userDto) {
        logger.info("Request to update user ID: {}", userId);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }
}
