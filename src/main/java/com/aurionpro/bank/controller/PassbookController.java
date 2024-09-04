package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.PassbookDto;
import com.aurionpro.bank.security.AuthUtil;
import com.aurionpro.bank.service.CustomerService;

@RestController
@RequestMapping("/bank/customers")
public class PassbookController {

    private final CustomerService customerService;
    private final AuthUtil authUtil;

    @Autowired
    public PassbookController(CustomerService customerService, AuthUtil authUtil) {
        this.customerService = customerService;
        this.authUtil = authUtil;
    }

    @GetMapping("/{customerId}/passbook")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') and @authUtil.getAuthenticatedCustomer().id == #customerId")
    public ResponseEntity<PassbookDto> getPassbook(@PathVariable Long customerId) {
        PassbookDto passbookDto = customerService.getPassbook(customerId);
        return ResponseEntity.ok(passbookDto);
    }
}
