package com.linktic.productservice.controller;

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
import com.linktic.productservice.model.JsonApiListResponse;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;
import com.linktic.productservice.service.IProductService;
import com.toedter.spring.hateoas.jsonapi.MediaTypes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/v1/product", produces = MediaTypes.JSON_API_VALUE)
@Validated
@Tag(name = "Products", description = "Operations related to products")
public class ProductController {

	private final IProductService iProductService;

	public ProductController(IProductService iProductService) {
		this.iProductService = iProductService;
	}

	@Operation(summary = "Create a new product", description = "Creates a product with the given attributes")
	@ApiResponse(responseCode = "200", description = "Product created successfully")
	@ApiResponse(responseCode = "400", description = "Invalid input data")
	@PostMapping(consumes = MediaTypes.JSON_API_VALUE)
	public ResponseEntity<JsonApiResponse<ProductAttributes>> create(
			@Valid @RequestBody JsonApiRequest<ProductAttributes> request) {
		return ResponseEntity.ok(iProductService.create(request));
	}

	@Operation(summary = "Get product by ID", description = "Fetch a single product by its ID")
	@ApiResponse(responseCode = "200", description = "Product found")
	@ApiResponse(responseCode = "404", description = "Product not found")
	@GetMapping("/{id}")
	public ResponseEntity<JsonApiResponse<ProductAttributes>> getById(
			@PathVariable @NotBlank(message = "Product ID is required") String id) {
		JsonApiResponse<ProductAttributes> dto = iProductService.getById(id);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Update product", description = "Updates the attributes of an existing product")
	@ApiResponse(responseCode = "200", description = "Product updated successfully")
	@ApiResponse(responseCode = "404", description = "Product not found")
	@PutMapping("/{id}")
	public ResponseEntity<JsonApiResponse<ProductAttributes>> updateProduct(
			@PathVariable @NotBlank(message = "Product ID is required") String id,
			@Valid @RequestBody JsonApiRequest<ProductAttributes> request) {
		JsonApiResponse<ProductAttributes> updatedProduct = iProductService.update(id,
				request.getData().getAttributes());

		return ResponseEntity.ok(updatedProduct);
	}

	@Operation(summary = "Delete product", description = "Deletes a product by its ID")
	@ApiResponse(responseCode = "204", description = "Product deleted successfully")
	@ApiResponse(responseCode = "404", description = "Product not found")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable @NotBlank(message = "Product ID is required") String id) {
		iProductService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "List products", description = "Lists products with pagination")
	@ApiResponse(responseCode = "200", description = "Products listed successfully")
	@GetMapping
	public ResponseEntity<JsonApiListResponse<ProductAttributes>> list(
			@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page number must be >= 0") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be >= 1") @Max(value = 100, message = "Page size must be <= 100") int size) {
		return ResponseEntity.ok(iProductService.list(PageRequest.of(page, size)));
	}
}