package com.linktic.inventaryservice.util;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutRetryer implements Retryer, Cloneable {

	private static final int DEFAULT_MAX_ATTEMPTS = 3;
	private static final long DEFAULT_BACKOFF_MS = 1000;

	private final int maxAttempts;
	private final long backoff;
	private int attempt = 1;

	public TimeoutRetryer() {
		this(DEFAULT_MAX_ATTEMPTS, DEFAULT_BACKOFF_MS);
	}

	public TimeoutRetryer(int maxAttempts, long backoff) {
		this.maxAttempts = maxAttempts;
		this.backoff = backoff;
	}

	@Override
	public void continueOrPropagate(RetryableException e) {
		if (isTimeoutException(e)) {
			if (attempt >= maxAttempts) {
				log.error("Max retry attempts ({}) reached for request: {}", maxAttempts,
						maskApiKey(e.request().toString()));
				throw e;
			}
			log.warn("Retry attempt #{} for request {}\n{}", attempt, e.request().httpMethod(),
					maskApiKey(e.request().toString()));
			attempt++;
			try {
				Thread.sleep(backoff);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
		} else {
			throw e;
		}
	}

	private String maskApiKey(String requestLog) {
		return requestLog.replaceAll("X-API-KEY: .*", "");
	}

	private boolean isTimeoutException(RetryableException e) {
		Throwable cause = e.getCause();
		if (cause == null) {
			return false;
		}
		String message = cause.getMessage();
		return message != null && (message.contains("Read timed out") || message.contains("connect timed out"));
	}

	@Override
	public Retryer clone() {
		try {
			TimeoutRetryer copy = (TimeoutRetryer) super.clone();
			copy.attempt = 1;
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}