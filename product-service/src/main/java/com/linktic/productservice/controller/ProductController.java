package com.linktic.productservice.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;
import com.linktic.productservice.service.IProductService;
import com.toedter.spring.hateoas.jsonapi.MediaTypes;

@RestController
@RequestMapping(path = "/v1/product", produces = MediaTypes.JSON_API_VALUE)
@Validated
public class ProductController {

	private final IProductService iProductService;

	public ProductController(IProductService iProductService) {
		this.iProductService = iProductService;
	}

	@PostMapping(consumes = MediaTypes.JSON_API_VALUE)
	public ResponseEntity<JsonApiResponse<ProductAttributes>> create(
			@RequestBody JsonApiRequest<ProductAttributes> request) {
		return ResponseEntity.ok(iProductService.create(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<JsonApiResponse<ProductAttributes>> getById(@PathVariable String id) {
		Optional<JsonApiResponse<ProductAttributes>> dto = iProductService.getById(id);
		return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping(value = "/{id}", consumes = MediaTypes.JSON_API_VALUE)
	public ResponseEntity<JsonApiResponse<ProductAttributes>> update(@PathVariable String id,
			@RequestBody JsonApiRequest<ProductAttributes> request) {
		Optional<JsonApiResponse<ProductAttributes>> updated = iProductService.update(id, request);
		return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		iProductService.delete(id);
		return ResponseEntity.ok(null);
	}

	@GetMapping
	public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(iProductService.list(PageRequest.of(page, size)));
	}
}