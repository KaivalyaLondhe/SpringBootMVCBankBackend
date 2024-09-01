package com.aurionpro.bank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.AccountDtoForDisplay;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.repository.AccountRepository;
import com.aurionpro.bank.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Account createAccount(AccountDto accountDto) {
        logger.info("Attempting to create account for customer with ID: {}", accountDto.getCustomerId());

        Customer customer = customerRepository.findById(accountDto.getCustomerId())
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", accountDto.getCustomerId());
                    return new RuntimeException("Customer not found");
                });

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(accountDto.getBalance());
        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully with account number: {}", savedAccount.getAccountNumber());

        return savedAccount;
    }

    public AccountDtoForDisplay toAccountDtoMapper(Account account) {
        AccountDtoForDisplay accountDtoForDisplay = new AccountDtoForDisplay();
        accountDtoForDisplay.setCustomerId(account.getId());
        accountDtoForDisplay.setBalance(account.getBalance());
        accountDtoForDisplay.setAccountNumber(account.getAccountNumber());

        logger.debug("Mapped Account entity to AccountDtoForDisplay: {}", accountDtoForDisplay);
        
        return accountDtoForDisplay;
    }

    private String generateAccountNumber() {
        String accountNumber = "ICIC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        logger.debug("Generated account number: {}", accountNumber);
        return accountNumber;
    }
}
