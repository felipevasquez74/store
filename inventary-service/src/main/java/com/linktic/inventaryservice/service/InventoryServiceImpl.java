package com.linktic.inventaryservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linktic.inventaryservice.dto.ProductAttributes;
import com.linktic.inventaryservice.entity.Inventory;
import com.linktic.inventaryservice.exception.ResourceNotFoundException;
import com.linktic.inventaryservice.integration.ProductClient;
import com.linktic.inventaryservice.model.InventoryJsonApiResponse;
import com.linktic.inventaryservice.model.JsonApiResponse;
import com.linktic.inventaryservice.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryServiceImpl implements IInventoryService {

	private static final String PRODUCT_NOT_FOUND_MSG = "Product with ID %s not found";

	private final InventoryRepository repository;
	private final ProductClient productClient;

	public InventoryServiceImpl(InventoryRepository repository, ProductClient productClient) {
		this.repository = repository;
		this.productClient = productClient;
	}

	@Override
	public InventoryJsonApiResponse getInventoryByProductId(String productId) {
		try {
			JsonApiResponse<ProductAttributes> response = productClient.getByProductById(productId);

			if (response == null || response.getData() == null) {
				throw new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId));
			}

			Optional<Inventory> inventoryOpt = repository.findById(productId);
			Integer quantity = inventoryOpt.map(Inventory::getQuantity).orElse(0);

			return new InventoryJsonApiResponse(productId, quantity);

		} catch (feign.FeignException.NotFound e) {
			throw new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId));
		} catch (feign.FeignException e) {
			throw e;
		}
	}

	@Transactional
	@Override
	public InventoryJsonApiResponse updateQuantityAfterPurchase(String productId, int quantityToDeduct) {
		Inventory inventory = repository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory for product " + productId + " not found"));

		int newQuantity = inventory.getQuantity() - quantityToDeduct;
		if (newQuantity < 0) {
			throw new IllegalArgumentException("Insufficient inventory for product " + productId);
		}

		inventory.setQuantity(newQuantity);
		repository.save(inventory);

		// Emitir evento simple (log)
		log.info("Inventory updated for product {}: new quantity = {}", productId, newQuantity);

		return new InventoryJsonApiResponse(productId, newQuantity);
	}
}