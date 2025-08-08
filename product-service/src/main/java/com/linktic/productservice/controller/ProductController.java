package com.linktic.productservice.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
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

import com.linktic.productservice.dto.ProductDto;
import com.linktic.productservice.service.IProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/v1/product")
@Validated
@Slf4j
public class ProductController {

	private final IProductService iProductService;

	public ProductController(IProductService iProductService) {
		this.iProductService = iProductService;
	}

	@PostMapping
	public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
		return ResponseEntity.ok(iProductService.create(dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDto> getById(@PathVariable String id) {
		Optional<ProductDto> dto = iProductService.getById(id);
		return dto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductDto> update(@PathVariable String id, @RequestBody ProductDto dto) {
		Optional<ProductDto> updated = iProductService.update(id, dto);
		return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		iProductService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public Page<ProductDto> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return iProductService.list(PageRequest.of(page, size));
	}
}
