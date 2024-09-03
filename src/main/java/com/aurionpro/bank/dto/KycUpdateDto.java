package com.aurionpro.bank.dto;

import com.aurionpro.bank.entity.KycStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Getter
@Setter
public class KycUpdateDto {

	private Long customerId;
	private KycStatus status;
}
