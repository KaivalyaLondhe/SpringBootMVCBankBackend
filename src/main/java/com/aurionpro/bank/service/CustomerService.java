package com.aurionpro.bank.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.PassbookDto;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.DocumentType;
import com.aurionpro.bank.entity.KycStatus;

public interface CustomerService {
    PageResponse<CustomerDto> getAllCustomers(int page, int size);
    CustomerDto getCustomerById(Long customerId);
    List<CustomerWithBalanceDto> getAllCustomersWithBalances();
    CustomerWithBalanceDto getCustomerWithBalancesById(Long customerId);
    List<TransactionDto> getTransactionsByCustomerId(Long customerId);
    void deactivateCustomer(Long customerId);
    KycDocumentDto uploadKycDocument(Long customerId, DocumentType documentType, MultipartFile file);

    // Get KYC document status for the customer
    KycStatus getKycStatus(Long customerId);

    // Fetch the KYC document for customer
    KycDocumentDto getKycDocument(Long customerId);
    
    PassbookDto getPassbook(Long customerId);

}
