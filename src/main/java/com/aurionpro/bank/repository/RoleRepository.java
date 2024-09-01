package com.aurionpro.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	  Optional<Role> findByName(String role);
	

}
