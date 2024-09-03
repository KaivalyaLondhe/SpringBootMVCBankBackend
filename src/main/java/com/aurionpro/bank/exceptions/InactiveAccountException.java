package com.aurionpro.bank.exceptions;

public class InactiveAccountException extends RuntimeException{
	public InactiveAccountException(String message) {
		super(message);
	}
	
}
