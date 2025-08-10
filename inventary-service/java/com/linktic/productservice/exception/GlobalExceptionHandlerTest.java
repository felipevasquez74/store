package com.linktic.productservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.linktic.productservice.model.JsonApiErrorResponse;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void handleEntityNotFound_shouldReturn404() {
		// given
		EntityNotFoundException ex = new EntityNotFoundException("Product with ID 123 not found");

		// when
		ResponseEntity<JsonApiErrorResponse> response = handler.handleEntityNotFound(ex);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getErrors()).hasSize(1);

		JsonApiErrorResponse.Error error = response.getBody().getErrors().get(0);
		assertThat(error.getStatus()).isEqualTo("404");
		assertThat(error.getTitle()).isEqualTo("Resource not found");
		assertThat(error.getDetail()).contains("123");
	}

	@Test
	void handleGenericException_shouldReturn500() {
		// given
		Exception ex = new Exception("Unexpected error happened");

		// when
		ResponseEntity<JsonApiErrorResponse> response = handler.handleGenericException(ex);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getErrors()).hasSize(1);

		JsonApiErrorResponse.Error error = response.getBody().getErrors().get(0);
		assertThat(error.getStatus()).isEqualTo("500");
		assertThat(error.getTitle()).isEqualTo("Internal Server Error");
		assertThat(error.getDetail()).isEqualTo("Unexpected error happened");
	}
}