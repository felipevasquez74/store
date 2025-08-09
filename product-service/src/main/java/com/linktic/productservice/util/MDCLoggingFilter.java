package com.linktic.productservice.util;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCLoggingFilter {

	private MDCLoggingFilter() {
	}

	public static void builderMDC() {
		MDC.put("spanId", UUID.randomUUID().toString());
	}
}
