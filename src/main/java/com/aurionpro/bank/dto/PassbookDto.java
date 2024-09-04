package com.aurionpro.bank.dto;

import java.math.BigDecimal;
import java.util.List;

import com.aurionpro.bank.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class PassbookDto {

    private String accountNumber;
    private List<TransactionDto> transactions;

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    @Setter
    @Getter
    public static class TransactionDto {
        private String date;
        private BigDecimal amount;
        private TransactionType transactionType;
        private String senderAccountNumber;  // Optional, for transfer transactions
        private String receiverAccountNumber;  // Optional, for transfer transactions
    }

}
