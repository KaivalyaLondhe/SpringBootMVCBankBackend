package com.aurionpro.bank.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.KycUpdateDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.service.CloudinaryService;
import com.aurionpro.bank.service.CustomerService;

@RestController
@RequestMapping("/bank")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

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

    @GetMapping("/customer/with-balances/{id}")
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
    @PreAuthorize("hasRole('ADMIN')")  // Restrict to only admins
    @PatchMapping("/{customerId}/deactivate")
    public ResponseEntity<String> deactivateCustomer(@PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.ok("Customer account deactivated successfully");
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/uploadKycDocument")
    public ResponseEntity<String> uploadKycDocument(@RequestParam("file") MultipartFile file, @RequestParam("customerId") Long customerId) {
        try {
            String documentUrl = cloudinaryService.uploadDocument(file);
            customerService.updateKycDocumentUrl(customerId, documentUrl);
            return ResponseEntity.ok("Document uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload document");
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")  // Restrict to only admins
    @PostMapping("/updateKycStatus")
    public ResponseEntity<String> updateKycStatus(@RequestBody KycUpdateDto kycUpdateDto) {
        customerService.updateKycStatus(kycUpdateDto.getCustomerId(), kycUpdateDto.getStatus());
        return ResponseEntity.ok("KYC status updated successfully");
    }
}
