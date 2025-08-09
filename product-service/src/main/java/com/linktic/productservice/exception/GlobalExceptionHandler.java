package com.linktic.productservice.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.linktic.productservice.model.JsonApiErrorResponse;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<JsonApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
		JsonApiErrorResponse.Error error = new JsonApiErrorResponse.Error("404", "Resource not found", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JsonApiErrorResponse(List.of(error)));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<JsonApiErrorResponse> handleGenericException(Exception ex) {
		JsonApiErrorResponse.Error error = new JsonApiErrorResponse.Error("500", "Internal Server Error",
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JsonApiErrorResponse(List.of(error)));
	}
}