package com.aurionpro.bank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWithBalanceDto {
    private Long customerId;
    private String firstName;
    private String lastName;
    private List<AccountDto> accounts;
}
