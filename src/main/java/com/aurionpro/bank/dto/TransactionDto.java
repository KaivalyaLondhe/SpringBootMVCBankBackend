package com.aurionpro.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
    private String senderAccountNumber; 
    private String receiverAccountNumber;
}
