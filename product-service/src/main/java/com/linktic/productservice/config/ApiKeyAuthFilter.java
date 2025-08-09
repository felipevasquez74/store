package com.linktic.productservice.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

	@Value("${service.api.key}")
	private String expectedApiKey;

	private static final String HEADER_NAME = "X-API-KEY";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String apiKey = request.getHeader(HEADER_NAME);
		if (!expectedApiKey.equals(apiKey)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json");
			response.getWriter().write("{\"errors\":[{\"status\":\"401\",\"detail\":\"Invalid API Key\"}]}");
			return;
		}
		filterChain.doFilter(request, response);
	}
}