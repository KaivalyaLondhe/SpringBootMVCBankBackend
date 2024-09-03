package com.aurionpro.bank.service;

import java.util.List;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.KycStatus;

public interface CustomerService {
    PageResponse<CustomerDto> getAllCustomers(int page, int size);
    CustomerDto getCustomerById(Long customerId);
    List<CustomerWithBalanceDto> getAllCustomersWithBalances();
    CustomerWithBalanceDto getCustomerWithBalancesById(Long customerId);
    List<TransactionDto> getTransactionsByCustomerId(Long customerId);
    void deactivateCustomer(Long customerId);
	void updateKycDocumentUrl(Long customerId, String documentUrl);
	void updateKycStatus(Long customerId, KycStatus status);
}
