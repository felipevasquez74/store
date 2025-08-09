package com.linktic.inventaryservice.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7398690330260955860L;

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}