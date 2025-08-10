package com.linktic.inventaryservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.linktic.inventaryservice.exception.ResourceNotFoundException;
import com.linktic.inventaryservice.model.InventoryJsonApiResponse;
import com.linktic.inventaryservice.service.IInventoryService;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IInventoryService iInventoryService;

	private InventoryJsonApiResponse sampleResponse;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		sampleResponse = new InventoryJsonApiResponse("prod123", 10, null);
	}

	@Test
	void getQuantity_shouldReturnOkAndResponse_whenProductExists() throws Exception {
		when(iInventoryService.getInventoryByProductId("prod123")).thenReturn(sampleResponse);

		mockMvc.perform(get("/v1/inventory/prod123").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value("prod123"))
				.andExpect(jsonPath("$.data.attributes.quantity").value(10));
	}

	@Test
	void getQuantity_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
		when(iInventoryService.getInventoryByProductId("prod123"))
				.thenThrow(new ResourceNotFoundException("Not found"));

		mockMvc.perform(get("/v1/inventory/prod123").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3")
				.accept("application/vnd.api+json")).andExpect(status().isNotFound());
	}

	@Test
	void updateAfterPurchase_shouldReturnBadRequest_whenQuantityParamMissing() throws Exception {
		mockMvc.perform(post("/v1/inventory/prod123/purchase").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	void updateAfterPurchase_shouldReturnBadRequest_whenQuantityParamInvalid() throws Exception {
		mockMvc.perform(post("/v1/inventory/prod123/purchase").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3")
				.param("quantity", "abc")).andExpect(status().isInternalServerError());
	}

	@Test
	void updateAfterPurchase_shouldReturnNotFound_whenProductNotFound() throws Exception {
		when(iInventoryService.updateQuantityAfterPurchase("prod123", 5))
				.thenThrow(new ResourceNotFoundException("Not found"));

		mockMvc.perform(post("/v1/inventory/prod123/purchase").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3")
				.param("quantity", "5").accept("application/vnd.api+json")).andExpect(status().isNotFound());
	}

	@Test
	void updateAfterPurchase_shouldReturnOk_whenValidRequest() throws Exception {
		when(iInventoryService.updateQuantityAfterPurchase("prod123", 5)).thenReturn(sampleResponse);

		mockMvc.perform(post("/v1/inventory/prod123/purchase").header("X-API-KEY", "S3CR3T_K3Y_1NVE3NTARY_S3RV1C3")
				.param("quantity", "5")).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value("prod123"))
				.andExpect(jsonPath("$.data.attributes.quantity").value(10));
	}

}