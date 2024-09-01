package com.aurionpro.bank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class RegistrationDto {
	  private Long id;
	    private String username;
	    private String password;
	    private String firstName;
	    private String lastName;
	    private String roleName;

}
