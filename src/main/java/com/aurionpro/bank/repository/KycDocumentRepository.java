package com.aurionpro.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.KycDocument;
import com.aurionpro.bank.entity.KycStatus;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

Optional<KycDocument> findByCustomerIdAndDocumentType(Long customerId, String documentType);
    
    // Find KYC document by account number
    Optional<KycDocument> findByAccount_AccountNumber(String accountNumber);

    // Find KYC document by KYC status
    Optional<KycDocument> findByKycStatus(KycStatus kycStatus);

    // Find KYC document by customer ID and KYC status
    Optional<KycDocument> findByCustomerIdAndKycStatus(Long customerId, KycStatus kycStatus);
    List<KycDocument> findAllByCustomerId(Long customerId);
    Optional<KycDocument> findByCustomerId(Long customerId);

}
