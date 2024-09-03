package com.aurionpro.bank.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	@NotNull(message = "Username cannot be null")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email address")
	@NotNull(message = "Email cannot be null")
	@NotBlank(message = "Eamil cannot be blank")
	@Email
	private String username;

	@Column(nullable = false)
	@NotNull(message = "Password cannot be null")
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 9 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.")
	private String password;

	@Column(nullable = false)
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetic characters are allowed")
	@NotNull(message = "FirstName cannot be null")
	@NotBlank(message = "FirstName cannot be blank")
	private String firstName;

	@Column(nullable = false)
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetic characters are allowed")
	@NotNull(message = "Lastname cannot be null")
	@NotBlank(message = "lastname cannot be blank")
	private String lastName;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Admin admin;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Customer customer;
}
