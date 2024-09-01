package com.aurionpro.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/bank")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping("/admin/customers")
    public ResponseEntity<PageResponse<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get all customers, page: {}, size: {}", page, size);
        PageResponse<CustomerDto> customerPage = customerService.getAllCustomers(page, size);
        logger.info("Fetched {} customers", customerPage.getContent().size());
        return ResponseEntity.ok(customerPage);
    }

    @GetMapping("/admin/customers/getById")
    public ResponseEntity<CustomerDto> getCustomerById(@RequestParam Long id) {
        logger.info("Request to get customer by ID: {}", id);
        CustomerDto customerDto = customerService.getCustomerById(id);
        logger.info("Fetched customer: {}", customerDto);
        return ResponseEntity.ok(customerDto);
    }

    @GetMapping("/admin/customers/with-balances")
    public ResponseEntity<List<CustomerWithBalanceDto>> getAllCustomersWithBalances() {
        logger.info("Request to get all customers with balances");
        List<CustomerWithBalanceDto> customersWithBalances = customerService.getAllCustomersWithBalances();
        logger.info("Fetched {} customers with balances", customersWithBalances.size());
        return ResponseEntity.ok(customersWithBalances);
    }

    @GetMapping("/admin/customers/with-balances/{id}")
    public ResponseEntity<CustomerWithBalanceDto> getCustomerWithBalancesById(@PathVariable Long id) {
        logger.info("Request to get customer with balances by ID: {}", id);
        CustomerWithBalanceDto customerWithBalances = customerService.getCustomerWithBalancesById(id);
        logger.info("Fetched customer with balances: {}", customerWithBalances);
        return ResponseEntity.ok(customerWithBalances);
    }

    @GetMapping("/customer/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCustomerId(@PathVariable Long id) {
        logger.info("Request to get transactions for customer ID: {}", id);
        List<TransactionDto> transactions = customerService.getTransactionsByCustomerId(id);
        logger.info("Fetched {} transactions for customer ID: {}", transactions.size(), id);
        return ResponseEntity.ok(transactions);
    }
}
