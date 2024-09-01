package com.aurionpro.bank.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.JwtAuthResponse;
import com.aurionpro.bank.dto.LoginDto;
import com.aurionpro.bank.dto.RegistrationDto;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.service.AuthService;

@RestController
@RequestMapping("/bank")
public class LoginController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody RegistrationDto registrationDto) {
		
		
		return ResponseEntity.ok(authService.register(registrationDto));
	}
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
		String token = authService.login(loginDto);
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		
		return ResponseEntity.ok(jwtAuthResponse);
	}
	
	
}
