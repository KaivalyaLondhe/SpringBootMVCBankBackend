package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.aurionpro.bank.dto.CreditDebitRequest;
import com.aurionpro.bank.dto.TransferRequest;
import com.aurionpro.bank.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/bank/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    // Endpoint to credit an account - Restricted to customers
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/credit")
    public ResponseEntity<String> credit(@RequestBody CreditDebitRequest request) {
        logger.info("Request to credit account ID: {} with amount: {}", request.getAccountNumber(), request.getAmount());
        transactionService.credit(request.getAccountNumber(), request.getAmount());
        logger.info("Credit transaction successful for account ID: {}", request.getAccountNumber());
        return ResponseEntity.ok("Credit transaction successful");
    }

    // Endpoint to debit an account - Restricted to customers
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/debit")
    public ResponseEntity<String> debit(@RequestBody CreditDebitRequest request) {
        logger.info("Request to debit account ID: {} with amount: {}", request.getAccountNumber(), request.getAmount());
        transactionService.debit(request.getAccountNumber(), request.getAmount());
        logger.info("Debit transaction successful for account ID: {}", request.getAccountNumber());
        return ResponseEntity.ok("Debit transaction successful");
    }

    // Endpoint to transfer between accounts - Restricted to customers
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        logger.info("Request to transfer from account ID: {} to account ID: {} with amount: {}", 
            request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());
        transactionService.transfer(request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());
        logger.info("Transfer transaction successful from account ID: {} to account ID: {}", 
            request.getSenderAccountNumber(), request.getReceiverAccountNumber());
        return ResponseEntity.ok("Transfer transaction successful");
    }
}
