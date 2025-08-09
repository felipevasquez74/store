package com.linktic.inventaryservice.service;

import com.linktic.inventaryservice.model.InventoryJsonApiResponse;

public interface IInventoryService {
	InventoryJsonApiResponse getInventoryByProductId(String productId);

	InventoryJsonApiResponse updateQuantityAfterPurchase(String productId, int quantity);
}