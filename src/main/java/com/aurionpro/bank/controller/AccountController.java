package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/bank/admin")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts")
    public Account createAccount(@RequestBody AccountDto accountDto) {
        logger.info("Request received to create account with details: {}", accountDto);
        Account account = accountService.createAccount(accountDto);
        logger.info("Account created successfully with ID: {}", account.getId());
        return account;
    }
}
