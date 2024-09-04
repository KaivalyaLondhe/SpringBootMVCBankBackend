package com.aurionpro.bank.service;

import com.aurionpro.bank.dto.KycDocumentDto;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.KycDocument;

public class KycDocumentMapper {
    
    public static KycDocumentDto toDto(KycDocument kycDocument) {
        return new KycDocumentDto(
                kycDocument.getId(),
                kycDocument.getDocumentType(),
                kycDocument.getDocumentUrl(),
                kycDocument.getKycStatus()
        );
    }
    
    public static KycDocument toEntity(KycDocumentDto dto, Customer customer, Account account) {
        KycDocument kycDocument = new KycDocument();
        kycDocument.setId(dto.getId());
        kycDocument.setDocumentType(dto.getDocumentType());
        kycDocument.setDocumentUrl(dto.getDocumentUrl());
        kycDocument.setKycStatus(dto.getKycStatus());
        kycDocument.setCustomer(customer);
        kycDocument.setAccount(account);
        return kycDocument;
    }
}
