package com.aurionpro.bank.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AccountDto {
    public AccountDto(Long id, String accountNumber, BigDecimal balance) {
		this.customerId = id;
		this.accountNumber = accountNumber;
		this.balance = balance;
	}
	private Long customerId;
    private BigDecimal balance;
    private String accountNumber;
}