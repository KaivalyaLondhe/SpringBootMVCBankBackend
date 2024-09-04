package com.aurionpro.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	  @Query("SELECT t FROM Transaction t WHERE t.account.customer.id = :customerId")
	    List<Transaction> findByAccountCustomerId(@Param("customerId") Long customerId);
	    List<Transaction> findByAccountId(Long accountId);

}