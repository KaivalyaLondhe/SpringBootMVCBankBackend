package com.aurionpro.bank.service;

import java.math.BigDecimal;

import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.exceptions.InsufficientFundsException;
import com.aurionpro.bank.exceptions.InvalidTransactionTypeException;

public interface TransactionService {
  
   
    void transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) 
            throws InsufficientFundsException, AccountNotFoundException, InvalidTransactionTypeException;
	void credit(String accountNumber, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException;
	void debit(String accountNumber, BigDecimal amount) throws InsufficientFundsException, AccountNotFoundException;
}

