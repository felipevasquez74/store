package com.linktic.inventaryservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linktic.inventaryservice.model.InventoryJsonApiResponse;
import com.linktic.inventaryservice.service.IInventoryService;
import com.toedter.spring.hateoas.jsonapi.MediaTypes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping(path = "/v1/inventory", produces = MediaTypes.JSON_API_VALUE)
@Validated
@Tag(name = "Inventories", description = "Operations related to inventory")
public class InventoryController {

	private final IInventoryService iInventoryService;

	public InventoryController(IInventoryService iInventoryService) {
		this.iInventoryService = iInventoryService;
	}

	@Operation(summary = "Get quantity by product ID", description = "Fetch a quantity of a product")
	@ApiResponse(responseCode = "200", description = "Product found")
	@ApiResponse(responseCode = "404", description = "Product not found")
	@GetMapping("/{productId}")
	public ResponseEntity<InventoryJsonApiResponse> getQuantity(
			@PathVariable @NotBlank(message = "Product ID is required") String productId) {
		return ResponseEntity.ok(iInventoryService.getInventoryByProductId(productId));
	}

	@Operation(summary = "Update product quantity", description = "Updates the quantity of a product after purchase")
	@ApiResponse(responseCode = "200", description = "Product quantity update successfully")
	@ApiResponse(responseCode = "400", description = "Invalid input data")
	@PostMapping(path = "/{productId}/purchase")
	public ResponseEntity<InventoryJsonApiResponse> updateAfterPurchase(
			@PathVariable @NotBlank(message = "Product ID is required") String productId,
			@RequestParam @Min(value = 1, message = "Quantity size must be >= 1") Integer quantity) {
		return ResponseEntity.ok(iInventoryService.updateQuantityAfterPurchase(productId, quantity));
	}
}