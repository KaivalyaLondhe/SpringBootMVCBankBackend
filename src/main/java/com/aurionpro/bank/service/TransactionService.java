package com.aurionpro.bank.service;

import java.math.BigDecimal;

import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.exceptions.InsufficientFundsException;
import com.aurionpro.bank.exceptions.InvalidTransactionTypeException;

public interface TransactionService {
    void credit(Long accountId, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException;
    void debit(Long accountId, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException;
    void transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) 
            throws InsufficientFundsException, AccountNotFoundException, InvalidTransactionTypeException;
}
