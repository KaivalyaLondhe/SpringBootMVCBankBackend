package com.aurionpro.bank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long customerId;
    private UserDto user;
    private String firstName;
    private String lastName;
    private List<AccountDtoForDisplay> accounts; 
}
