package com.aurionpro.bank.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountDtoForDisplay {
	private Long customerId;
	private BigDecimal balance;
	private String accountNumber;
}