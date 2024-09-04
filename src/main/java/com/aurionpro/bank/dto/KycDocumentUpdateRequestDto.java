package com.aurionpro.bank.dto;

import com.aurionpro.bank.entity.KycStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class KycDocumentUpdateRequestDto {
    private Long documentId;
    private KycStatus status;

   
}
