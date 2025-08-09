package com.linktic.inventaryservice.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.linktic.inventaryservice.config.FeignClientConfig;
import com.linktic.inventaryservice.dto.ProductAttributes;
import com.linktic.inventaryservice.model.JsonApiResponse;

@FeignClient(name = "productClient", url = "${integration.product-service.url}", configuration = FeignClientConfig.class)
public interface ProductClient {

	@GetMapping("/{id}")
	JsonApiResponse<ProductAttributes> getByProductById(@PathVariable("id") String productId);

}