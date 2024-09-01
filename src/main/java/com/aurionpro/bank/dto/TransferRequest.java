package com.aurionpro.bank.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
}