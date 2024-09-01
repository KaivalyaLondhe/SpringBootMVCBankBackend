package com.aurionpro.bank.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.AdminDto;
import com.aurionpro.bank.service.AdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/bank/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @GetMapping("/")
    public ResponseEntity<PageResponse<AdminDto>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get all admins, page: {}, size: {}", page, size);
        PageResponse<AdminDto> adminPage = adminService.getAllAdmins(page, size);
        logger.info("Fetched {} admins", adminPage.getContent().size());
        return ResponseEntity.ok(adminPage);
    }

    @GetMapping("/findbyid/")
    public ResponseEntity<AdminDto> getAdminById(@RequestParam Long adminId) {
        logger.info("Request to get admin by ID: {}", adminId);
        AdminDto adminDto = adminService.getAdminById(adminId);
        logger.info("Fetched admin: {}", adminDto);
        return ResponseEntity.ok(adminDto);
    }
}
