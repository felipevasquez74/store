package com.linktic.inventaryservice.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(List.of(new ErrorObject("404", "Not Found", ex.getMessage())));
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				List.of(new ErrorObject("400", "Bad Request", ex.getMessage())));
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		List<ErrorObject> errors = ex.getConstraintViolations().stream()
				.map(cv -> new ErrorObject("400", "Bad Request", cv.getPropertyPath() + ": " + cv.getMessage()))
				.toList();
		return new ResponseEntity<>(new ErrorResponse(errors), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(
				List.of(new ErrorObject("500", "Internal Server Error", "An unexpected error occurred")));
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ErrorResponse {
		private List<ErrorObject> errors;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ErrorObject {
		private String status;
		private String title;
		private String detail;
	}
}