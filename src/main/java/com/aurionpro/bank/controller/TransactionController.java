package com.aurionpro.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.CreditDebitRequest;
import com.aurionpro.bank.dto.TransferRequest;
import com.aurionpro.bank.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/bank/customer")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transaction/credit")
    public ResponseEntity<String> credit(@RequestBody CreditDebitRequest request) {
        logger.info("Request to credit account ID: {} with amount: {}", request.getAccountId(), request.getAmount());
        transactionService.credit(request.getAccountId(), request.getAmount());
        logger.info("Credit transaction successful for account ID: {}", request.getAccountId());
        return ResponseEntity.ok("Credit transaction successful");
    }

    @PostMapping("/transaction/debit")
    public ResponseEntity<String> debit(@RequestBody CreditDebitRequest request) {
        logger.info("Request to debit account ID: {} with amount: {}", request.getAccountId(), request.getAmount());
        transactionService.debit(request.getAccountId(), request.getAmount());
        logger.info("Debit transaction successful for account ID: {}", request.getAccountId());
        return ResponseEntity.ok("Debit transaction successful");
    }

    @PostMapping("/transaction/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        logger.info("Request to transfer from account ID: {} to account ID: {} with amount: {}", 
            request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());
        transactionService.transfer(request.getSenderAccountNumber(), request.getReceiverAccountNumber(), request.getAmount());
        logger.info("Transfer transaction successful from account ID: {} to account ID: {}", 
            request.getSenderAccountNumber(), request.getReceiverAccountNumber());
        return ResponseEntity.ok("Transfer transaction successful");
    }
}
