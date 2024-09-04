package com.aurionpro.bank.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.DocumentType;
import com.aurionpro.bank.entity.KycStatus;
import com.aurionpro.bank.service.CloudinaryService;
import com.aurionpro.bank.service.CustomerService;

@RestController
@RequestMapping("/bank/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    // Endpoint to get all customers with pagination - Admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get all customers, page: {}, size: {}", page, size);
        PageResponse<CustomerDto> customerPage = customerService.getAllCustomers(page, size);
        logger.info("Fetched {} customers", customerPage.getContent().size());
        return ResponseEntity.ok(customerPage);
    }

    // Endpoint to get customer by ID - Admin only
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        logger.info("Request to get customer by ID: {}", id);
        CustomerDto customerDto = customerService.getCustomerById(id);
        logger.info("Fetched customer: {}", customerDto);
        return ResponseEntity.ok(customerDto);
    }

    // Endpoint to get all customers with balances - Admin only
    @GetMapping("/with-balances")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerWithBalanceDto>> getAllCustomersWithBalances() {
        logger.info("Request to get all customers with balances");
        List<CustomerWithBalanceDto> customersWithBalances = customerService.getAllCustomersWithBalances();
        logger.info("Fetched {} customers with balances", customersWithBalances.size());
        return ResponseEntity.ok(customersWithBalances);
    }

    // Endpoint to get customer with balances by ID - Admin only
    @GetMapping("/{id}/with-balances")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerWithBalanceDto> getCustomerWithBalancesById(@PathVariable Long id) {
        logger.info("Request to get customer with balances by ID: {}", id);
        CustomerWithBalanceDto customerWithBalances = customerService.getCustomerWithBalancesById(id);
        logger.info("Fetched customer with balances: {}", customerWithBalances);
        return ResponseEntity.ok(customerWithBalances);
    }

    // Endpoint to get transactions for a customer - Customer only
    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCustomerId(@PathVariable Long id) {
        logger.info("Request to get transactions for customer ID: {}", id);
        List<TransactionDto> transactions = customerService.getTransactionsByCustomerId(id);
        logger.info("Fetched {} transactions for customer ID: {}", transactions.size(), id);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to deactivate a customer - Admin only
    @PatchMapping("/{customerId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateCustomer(@PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.ok("Customer account deactivated successfully");
    }


    @PreAuthorize("hasRole('ROLE_CUSTOMER') and @authUtil.getAuthenticatedCustomer().id == #customerId")
    @PostMapping("/{customerId}/kyc")
    public ResponseEntity<KycDocumentDto> uploadKycDocument(
            @PathVariable Long customerId, 
            @RequestParam DocumentType documentType, 
            @RequestParam("file") MultipartFile file) {

        KycDocumentDto kycDocumentDto = customerService.uploadKycDocument(customerId, documentType, file);
        return ResponseEntity.ok(kycDocumentDto);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') and @authUtil.getAuthenticatedCustomer().id == #customerId")
    @GetMapping("/{customerId}/kyc/status")
    public ResponseEntity<KycStatus> getKycStatus(@PathVariable Long customerId) {

        KycStatus kycStatus = customerService.getKycStatus(customerId);
        return ResponseEntity.ok(kycStatus);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') and @authUtil.getAuthenticatedCustomer().id == #customerId")
    @GetMapping("/{customerId}/kyc")
    public ResponseEntity<KycDocumentDto> getKycDocument(@PathVariable Long customerId) {

        KycDocumentDto kycDocumentDto = customerService.getKycDocument(customerId);
        return ResponseEntity.ok(kycDocumentDto);
    }
}
