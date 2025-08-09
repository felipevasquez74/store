package com.linktic.inventaryservice.integration.messageBroker;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimulatedEventPublisher implements EventPublisher {

	@Override
	public void publishInventoryChanged(String productId, int newQuantity) {
		log.info("[Simulated Queue] InventoryChangedEvent published for productId={}, newQuantity={}", productId,
				newQuantity);
	}
}