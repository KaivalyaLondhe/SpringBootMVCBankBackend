package com.aurionpro.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.User;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByUserId(Long userId);
	Optional<Customer> findByUser(User user);
	  @Query("SELECT c FROM Customer c WHERE c.active = true")
	    List<Customer> findAllActive();

}
