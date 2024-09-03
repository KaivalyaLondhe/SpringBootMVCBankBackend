package com.aurionpro.bank.exceptions;

public class AccessDeniedException extends RuntimeException{
	public AccessDeniedException(String message) {
		super(message);
	}
	
}
