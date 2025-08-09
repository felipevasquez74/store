package com.linktic.inventaryservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linktic.inventaryservice.dto.ProductAttributes;
import com.linktic.inventaryservice.entity.Inventory;
import com.linktic.inventaryservice.exception.ResourceNotFoundException;
import com.linktic.inventaryservice.integration.ProductClient;
import com.linktic.inventaryservice.model.InventoryJsonApiResponse;
import com.linktic.inventaryservice.model.JsonApiResponse;
import com.linktic.inventaryservice.repository.InventoryRepository;
import com.linktic.inventaryservice.util.MDCLoggingFilter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryServiceImpl implements IInventoryService {

	private static final String PRODUCT_NOT_FOUND_MSG = "Product with ID %s not found";
	private static final String INVENTORY_NOT_FOUND_MSG = "Inventory for product ID %s not found";
	private static final String INSUFFICIENT_INVENTORY_MSG = "Insufficient inventory for product ID %s";

	private final InventoryRepository repository;
	private final ProductClient productClient;

	public InventoryServiceImpl(InventoryRepository repository, ProductClient productClient) {
		this.repository = repository;
		this.productClient = productClient;
	}

	@Override
	public InventoryJsonApiResponse getInventoryByProductId(String productId) {
		MDCLoggingFilter.builderMDC();

		log.info("Request received to get inventory and product info for productId={}", productId);

		Inventory inventory = repository.findById(productId).orElseThrow(() -> {
			log.warn("Inventory not found for productId={}", productId);
			return new ResourceNotFoundException(String.format(INVENTORY_NOT_FOUND_MSG, productId));
		});

		log.debug("Inventory found for productId={}, quantity={}", productId, inventory.getQuantity());

		JsonApiResponse<ProductAttributes> productResponse;
		try {
			productResponse = productClient.getByProductById(productId);
			log.debug("Product service returned successfully for productId={}", productId);
		} catch (feign.FeignException.NotFound e) {
			log.warn("Product not found via productClient for productId={}", productId);
			throw new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId));
		} catch (feign.FeignException e) {
			log.error("Error calling product service for productId={}, status={}, message={}", productId, e.status(),
					e.getMessage());
			throw e;
		}

		if (productResponse == null || productResponse.getData() == null) {
			log.warn("Product response empty or null for productId={}", productId);
			throw new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId));
		}

		log.info("Returning inventory and product info for productId={}, quantity={}", productId,
				inventory.getQuantity());

		return new InventoryJsonApiResponse(productId, inventory.getQuantity(),
				productResponse.getData().getAttributes());
	}

	@Transactional
	@Override
	public InventoryJsonApiResponse updateQuantityAfterPurchase(String productId, int quantityToDeduct) {
		MDCLoggingFilter.builderMDC();

		log.info("Request received to deduct quantity={} from inventory for productId={}", quantityToDeduct, productId);

		Inventory inventory = repository.findById(productId).orElseThrow(() -> {
			log.warn("Inventory not found when updating for productId={}", productId);
			return new ResourceNotFoundException(String.format(INVENTORY_NOT_FOUND_MSG, productId));
		});

		int newQuantity = inventory.getQuantity() - quantityToDeduct;
		if (newQuantity < 0) {
			log.warn("Insufficient inventory for productId={}, requested deduction={}, available={}", productId,
					quantityToDeduct, inventory.getQuantity());
			throw new IllegalArgumentException(String.format(INSUFFICIENT_INVENTORY_MSG, productId));
		}

		inventory.setQuantity(newQuantity);
		repository.save(inventory);
		log.info("Inventory updated successfully for productId={}, newQuantity={}", productId, newQuantity);

		JsonApiResponse<ProductAttributes> productResponse;
		try {
			productResponse = productClient.getByProductById(productId);
			log.debug("Product service returned successfully after update for productId={}", productId);
		} catch (feign.FeignException.NotFound e) {
			log.warn("Product not found via productClient after inventory update for productId={}", productId);
			throw new ResourceNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId));
		} catch (feign.FeignException e) {
			log.error("Error calling product service after inventory update for productId={}, status={}, message={}",
					productId, e.status(), e.getMessage());
			throw e;
		}

		return new InventoryJsonApiResponse(productId, newQuantity, productResponse.getData().getAttributes());
	}
}