package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.service.AdminService;
import com.aurionpro.bank.dto.PageResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/bank/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    // Endpoint to get all admins with pagination - Admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminDto>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get all admins, page: {}, size: {}", page, size);
        PageResponse<AdminDto> adminPage = adminService.getAllAdmins(page, size);
        logger.info("Fetched {} admins", adminPage.getContent().size());
        return ResponseEntity.ok(adminPage);
    }

    // Endpoint to get admin by ID - Admin only
    @GetMapping("/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Long adminId) {
        logger.info("Request to get admin by ID: {}", adminId);
        AdminDto adminDto = adminService.getAdminById(adminId);
        logger.info("Fetched admin: {}", adminDto);
        return ResponseEntity.ok(adminDto);
    }
}
