package com.aurionpro.bank.service;


import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.entity.Account;

public interface AccountService {
    Account createAccount(AccountDto accountDto);
}
