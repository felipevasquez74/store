package com.linktic.inventaryservice.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linktic.inventaryservice.util.TimeoutRetryer;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;

@Configuration
public class FeignClientConfig {

	private static final long CONNECT_TIMEOUT_MS = 3000;
	private static final long READ_TIMEOUT_MS = 3000;
	private static final long RETRY_PERIOD_MS = 1000;
	private static final int MAX_RETRIES = 3;

	@Value("${integration.product-service.api-key}")
	private String apiKey;

	@Bean
	RequestInterceptor requestInterceptor() {
		return template -> template.header("X-API-KEY", apiKey);
	}

	@Bean
	Request.Options options() {
		return new Request.Options(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS, READ_TIMEOUT_MS, TimeUnit.MILLISECONDS,
				true);
	}

	@Bean
	Retryer retryer() {
		return new TimeoutRetryer(MAX_RETRIES, RETRY_PERIOD_MS);
	}
}