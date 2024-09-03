package com.aurionpro.bank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.AccountDtoForDisplay;
import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.CustomerWithBalanceDto;
import com.aurionpro.bank.dto.PageResponse;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Kyc;
import com.aurionpro.bank.entity.KycStatus;
import com.aurionpro.bank.entity.Transaction;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.KycRepository;
import com.aurionpro.bank.repository.TransactionRepository;
import com.aurionpro.bank.security.AuthUtil;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountServiceImpl accountServiceImpl;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KycRepository kycRepository;
    
    @Autowired
    private AuthUtil authUtil;

    
    @Override
    public PageResponse<CustomerDto> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        logger.info("Retrieved page {} of customers with size {}", page, size);
        logger.debug("Total customers found: {}", customerPage.getTotalElements());

        return new PageResponse<>(
                customerPage.getTotalPages(),
                customerPage.getTotalElements(),
                size,
                customerPage.getContent().stream().map(this::toCustomerDtoMapper).toList(),
                !customerPage.hasNext()
        );
    }
    
    @Override
    public List<CustomerWithBalanceDto> getAllCustomersWithBalances() {
        logger.info("Fetching all customers with their balances");

        List<Customer> customers = customerRepository.findAll();
        List<CustomerWithBalanceDto> customersWithBalances = customers.stream()
            .map(customer -> new CustomerWithBalanceDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAccounts().stream()
                    .map(account -> new AccountDto(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getBalance()
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        logger.debug("Total customers with balances retrieved: {}", customersWithBalances.size());
        return customersWithBalances;
    }

    @Override
    public CustomerWithBalanceDto getCustomerWithBalancesById(Long customerId) {
        // Fetch the currently logged-in user's customer ID
    	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (!customerId.equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only access your own account.");
        }
        
        logger.info("Fetching customer with ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer with ID {} not found", customerId);
                return new EntityNotFoundException("Customer not found with ID: " + customerId);
            });
        
        return new CustomerWithBalanceDto(
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getAccounts().stream()
                .map(account -> new AccountDto(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getBalance()
                ))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public List<TransactionDto> getTransactionsByCustomerId(Long customerId) {
        // Fetch the currently logged-in user's customer ID
       	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (!customerId.equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only access transactions for your own account.");
        }
        
        logger.info("Fetching transactions for customer ID: {}", customerId);
        
        List<Transaction> transactions = transactionRepository.findByAccountCustomerId(customerId);
        return transactions.stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
    }

    private TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getSenderAccount() != null ? transaction.getSenderAccount().getAccountNumber() : null,
                transaction.getReceiverAccount() != null ? transaction.getReceiverAccount().getAccountNumber() : null
        );
    }

    private CustomerDto toCustomerDtoMapper(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        List<AccountDtoForDisplay> accountDtos = customer.getAccounts().stream()
                .map(accountServiceImpl::toAccountDtoMapper)
                .toList();
        customerDto.setAccounts(accountDtos);
        
        return customerDto;
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        logger.info("Fetching customer by ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", customerId);
                    return new EntityNotFoundException("Customer not found");
                });

        CustomerDto customerDto = toCustomerDtoMapper(customer);
        logger.info("Found customer: {}", customerDto);
        return customerDto;
    }
    
    @Override
    @Transactional
    public void deactivateCustomer(Long customerId) {
        logger.info("Deactivating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> {
                logger.error("Customer with ID {} not found", customerId);
                return new EntityNotFoundException("Customer not found with ID: " + customerId);
            });

        if (!customer.isActive()) {
            throw new IllegalStateException("Customer account is already deactivated");
        }

        customer.setActive(false); // Deactivate customer by setting the active flag to false
        customerRepository.save(customer);
        
        logger.info("Customer with ID: {} has been deactivated", customerId);
    }

    @Override
    public void updateKycDocumentUrl(Long customerId, String documentUrl) {
        Kyc kyc = kycRepository.findByCustomerId(customerId);
        if (kyc == null) {
            throw new EntityNotFoundException("KYC record not found");
        }
        kyc.setDocumentUrl(documentUrl);
        kycRepository.save(kyc);
    }

    @Override
    public void updateKycStatus(Long customerId, KycStatus status) {
        Kyc kyc = kycRepository.findByCustomerId(customerId);
        if (kyc == null) {
            throw new EntityNotFoundException("KYC record not found");
        }
        kyc.setStatus(status);
        kycRepository.save(kyc);
    }
}
