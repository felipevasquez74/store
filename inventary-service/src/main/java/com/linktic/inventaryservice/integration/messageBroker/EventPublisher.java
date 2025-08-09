package com.linktic.inventaryservice.integration.messageBroker;

public interface EventPublisher {
	void publishInventoryChanged(String productId, int newQuantity);
}
