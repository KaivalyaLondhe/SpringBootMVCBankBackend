package com.aurionpro.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurionpro.bank.entity.Kyc;

public interface KycRepository extends JpaRepository<Kyc, Long> {
    Kyc findByCustomerId(Long customerId);
}
