package com.linktic.inventaryservice.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import feign.Request;
import feign.RetryableException;

class TimeoutRetryerTest {

	private TimeoutRetryer retryer;
	private Request request;

	@BeforeEach
	void setup() {
		retryer = new TimeoutRetryer(3, 10);
		request = Request.create(Request.HttpMethod.GET, "/test", new HashMap<>(), null, null, null);
	}

	@Test
	void continueOrPropagate_shouldRetryOnTimeoutException() {
		RetryableException ex = mock(RetryableException.class);
		when(ex.getCause()).thenReturn(new SocketTimeoutException("Read timed out"));
		when(ex.request()).thenReturn(request);
		when(ex.getMessage()).thenReturn("Timeout");

		retryer.continueOrPropagate(ex);
		retryer.continueOrPropagate(ex);

		assertThrows(RetryableException.class, () -> retryer.continueOrPropagate(ex));
	}

	@Test
	void continueOrPropagate_shouldThrowImmediately_ifNotTimeoutException() {
		RetryableException ex = mock(RetryableException.class);
		when(ex.getCause()).thenReturn(new RuntimeException("Other error"));
		when(ex.request()).thenReturn(request);
		when(ex.getMessage()).thenReturn("Other error");

		assertThrows(RetryableException.class, () -> retryer.continueOrPropagate(ex));
	}

	@Test
	void clone_shouldResetAttempt() throws Exception {
		RetryableException ex = mock(RetryableException.class);
		when(ex.getCause()).thenReturn(new SocketTimeoutException("Read timed out"));
		when(ex.request()).thenReturn(request);
		when(ex.getMessage()).thenReturn("Timeout");

		retryer.continueOrPropagate(ex);

		TimeoutRetryer clone = (TimeoutRetryer) retryer.clone();

		Field attemptFieldOriginal = TimeoutRetryer.class.getDeclaredField("attempt");
		attemptFieldOriginal.setAccessible(true);
		int attemptOriginal = (int) attemptFieldOriginal.get(retryer);

		Field attemptFieldClone = TimeoutRetryer.class.getDeclaredField("attempt");
		attemptFieldClone.setAccessible(true);

		assertTrue(attemptOriginal > 1, "El retryer original debe tener attempt > 1");

		clone.continueOrPropagate(ex);
	}
}