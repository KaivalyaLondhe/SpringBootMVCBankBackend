package com.aurionpro.bank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Transaction;
import com.aurionpro.bank.entity.TransactionType;
import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.exceptions.InsufficientFundsException;
import com.aurionpro.bank.exceptions.InvalidTransactionTypeException;
import com.aurionpro.bank.repository.AccountRepository;
import com.aurionpro.bank.repository.TransactionRepository;
import com.aurionpro.bank.security.AuthUtil;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AuthUtil authUtil;


    @Transactional
    @Override
    public void credit(String accountNumber, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException {
        // Fetch the currently logged-in user's customer ID
     	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found with number: {}", accountNumber);
                    return new AccountNotFoundException("Account not found");
                });

        // Check if the account belongs to the currently logged-in user
        if (!account.getCustomer().getId().equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only credit your own accounts.");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.CREDIT);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        logger.info("Credit transaction completed for account number: {}. New balance: {}", accountNumber, account.getBalance());
    }

    @Transactional
    @Override
    public void debit(String accountNumber, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException {
        // Fetch the currently logged-in user's customer ID
    	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found with number: {}", accountNumber);
                    return new AccountNotFoundException("Account not found");
                });

        // Check if the account belongs to the currently logged-in user
        if (!account.getCustomer().getId().equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only debit your own accounts.");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            logger.error("Insufficient funds for account number: {}. Balance: {}, Requested amount: {}", accountNumber, account.getBalance(), amount);
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.DEBIT);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        logger.info("Debit transaction completed for account number: {}. New balance: {}", accountNumber, account.getBalance());
    }

    @Transactional
    @Override
    public void transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException, InvalidTransactionTypeException {
        // Fetch the currently logged-in user's customer ID
    	Customer loggedInCustomer = authUtil.getAuthenticatedCustomer();
        Long loggedInCustomerId = loggedInCustomer.getId();
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid amount: {}. Transfer amount must be positive", amount);
            throw new InvalidTransactionTypeException("Transfer amount must be greater than zero.");
        }

        if (senderAccountNumber.equals(receiverAccountNumber)) {
            logger.error("Sender account number {} and receiver account number {} cannot be the same", senderAccountNumber, receiverAccountNumber);
            throw new InvalidTransactionTypeException("Sender and receiver accounts cannot be the same.");
        }

        Account sender = accountRepository.findByAccountNumber(senderAccountNumber)
                .orElseThrow(() -> {
                    logger.error("Sender account not found with number: {}", senderAccountNumber);
                    return new AccountNotFoundException("Sender account not found");
                });

        Account receiver = accountRepository.findByAccountNumber(receiverAccountNumber)
                .orElseThrow(() -> {
                    logger.error("Receiver account not found with number: {}", receiverAccountNumber);
                    return new AccountNotFoundException("Receiver account not found");
                });

        // Check if the sender account belongs to the currently logged-in user
        if (!sender.getCustomer().getId().equals(loggedInCustomerId)) {
            throw new AccessDeniedException("You can only transfer from your own accounts.");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            logger.error("Insufficient funds for sender account number: {}. Balance: {}, Requested amount: {}", senderAccountNumber, sender.getBalance(), amount);
            throw new InsufficientFundsException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Create and save transactions
        Transaction debitTransaction = new Transaction();
        debitTransaction.setAmount(amount);
        debitTransaction.setDate(LocalDateTime.now());
        debitTransaction.setType(TransactionType.DEBIT);
        debitTransaction.setAccount(sender);
        debitTransaction.setReceiverAccount(receiver);
        transactionRepository.save(debitTransaction);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setAmount(amount);
        creditTransaction.setDate(LocalDateTime.now());
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setAccount(receiver);
        creditTransaction.setSenderAccount(sender);
        transactionRepository.save(creditTransaction);

        logger.info("Transfer completed from account number: {} to account number: {}. Amount: {}", senderAccountNumber, receiverAccountNumber, amount);
    }

}
