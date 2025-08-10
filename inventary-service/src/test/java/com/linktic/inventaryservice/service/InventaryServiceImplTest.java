package com.linktic.inventaryservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.linktic.inventaryservice.dto.ProductAttributes;
import com.linktic.inventaryservice.entity.Inventory;
import com.linktic.inventaryservice.exception.ResourceNotFoundException;
import com.linktic.inventaryservice.integration.feign.ProductClient;
import com.linktic.inventaryservice.integration.messageBroker.EventPublisher;
import com.linktic.inventaryservice.model.InventoryJsonApiResponse;
import com.linktic.inventaryservice.model.JsonApiResponse;
import com.linktic.inventaryservice.repository.InventoryRepository;

import feign.FeignException;
import feign.Request;
import feign.Response;

class InventoryServiceImplTest {

	@Mock
	InventoryRepository repository;

	@Mock
	ProductClient productClient;

	@Mock
	EventPublisher eventPublisher;

	@InjectMocks
	InventoryServiceImpl service;

	Inventory inventory;
	JsonApiResponse<ProductAttributes> productResponse;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		inventory = new Inventory();
		inventory.setProductId("prod123");
		inventory.setQuantity(20);

		ProductAttributes attrs = new ProductAttributes("Test Product", 100L);
		JsonApiResponse.JsonApiData<ProductAttributes> data = new JsonApiResponse.JsonApiData<>();
		data.setId("prod123");
		data.setType("products");
		data.setAttributes(attrs);

		productResponse = new JsonApiResponse<>();
		productResponse.setData(data);
	}

	@Test
	void getInventoryByProductId_shouldReturnInventoryJsonApiResponse_whenProductAndInventoryExist() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		when(productClient.getByProductById("prod123")).thenReturn(productResponse);

		InventoryJsonApiResponse response = service.getInventoryByProductId("prod123");

		assertNotNull(response);
		assertEquals("prod123", response.getData().getId());
		assertEquals(20, response.getData().getAttributes().getQuantity());
		assertNotNull(response.getIncluded());
		assertEquals("Test Product", response.getIncluded().get(0).getAttributes().getName());
	}

	@Test
	void getInventoryByProductId_shouldThrowNotFoundException_whenInventoryNotFound() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.getInventoryByProductId("prod123");
		});
		assertTrue(ex.getMessage().contains("Inventory for product ID"));
	}

	@Test
	void getInventoryByProductId_shouldThrowNotFoundException_whenProductClientReturnsNotFound() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		when(productClient.getByProductById("prod123")).thenThrow(FeignException.NotFound.class);

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.getInventoryByProductId("prod123");
		});
		assertTrue(ex.getMessage().contains("Product with ID"));
	}

	@Test
	void updateQuantityAfterPurchase_shouldUpdateInventoryAndPublishEvent() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		when(productClient.getByProductById("prod123")).thenReturn(productResponse);

		InventoryJsonApiResponse response = service.updateQuantityAfterPurchase("prod123", 5);

		assertEquals(15, response.getData().getAttributes().getQuantity());
		verify(repository).save(argThat(inv -> inv.getQuantity() == 15));
		verify(eventPublisher).publishInventoryChanged("prod123", 15);
	}

	@Test
	void updateQuantityAfterPurchase_shouldThrowIllegalArgumentException_whenInsufficientInventory() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			service.updateQuantityAfterPurchase("prod123", 50);
		});

		assertTrue(ex.getMessage().contains("Insufficient inventory"));
		verify(repository, never()).save(any());
		verify(eventPublisher, never()).publishInventoryChanged(anyString(), anyInt());
	}

	@Test
	void updateQuantityAfterPurchase_shouldThrowNotFoundException_whenInventoryNotFound() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.empty());

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.updateQuantityAfterPurchase("prod123", 5);
		});

		assertTrue(ex.getMessage().contains("Inventory for product ID"));
	}

	@Test
	void getInventoryByProductId_shouldThrowFeignException_whenFeignExceptionOccurs() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		FeignException feignEx = FeignException.errorStatus("getByProductById", Response.builder().status(500)
				.request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, null, null)).build());

		when(productClient.getByProductById("prod123")).thenThrow(feignEx);

		FeignException ex = assertThrows(FeignException.class, () -> {
			service.getInventoryByProductId("prod123");
		});

		assertEquals(500, ex.status());
	}

	@Test
	void getInventoryByProductId_shouldThrowNotFoundException_whenProductResponseIsNull() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		when(productClient.getByProductById("prod123")).thenReturn(null);

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.getInventoryByProductId("prod123");
		});

		assertTrue(ex.getMessage().contains("Product with ID"));
	}

	@Test
	void getInventoryByProductId_shouldThrowNotFoundException_whenProductResponseDataIsNull() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));
		JsonApiResponse<ProductAttributes> emptyResponse = new JsonApiResponse<>();
		emptyResponse.setData(null);
		when(productClient.getByProductById("prod123")).thenReturn(emptyResponse);

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.getInventoryByProductId("prod123");
		});

		assertTrue(ex.getMessage().contains("Product with ID"));
	}

	@Test
	void updateQuantityAfterPurchase_shouldThrowNotFoundException_whenProductClientReturnsNotFound() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));

		when(productClient.getByProductById("prod123")).thenThrow(FeignException.NotFound.class);

		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
			service.updateQuantityAfterPurchase("prod123", 5);
		});

		assertTrue(ex.getMessage().contains("Product with ID"));
	}

	@Test
	void updateQuantityAfterPurchase_shouldThrowFeignException_whenOtherFeignExceptionOccurs() {
		when(repository.findById("prod123")).thenReturn(java.util.Optional.of(inventory));

		Request request = Request.create(Request.HttpMethod.GET, "/products/prod123", Map.of(), null, null, null);
		Response response = Response.builder().status(500).reason("Internal Server Error").request(request).build();

		FeignException feignEx = FeignException.errorStatus("getByProductById", response);

		when(productClient.getByProductById("prod123")).thenThrow(feignEx);

		FeignException ex = assertThrows(FeignException.class, () -> {
			service.updateQuantityAfterPurchase("prod123", 5);
		});

		assertEquals(500, ex.status());
	}
}
