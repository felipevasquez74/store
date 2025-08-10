package com.linktic.productservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.model.JsonApiListResponse;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;
import com.linktic.productservice.service.IProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IProductService productService;

	private ProductAttributes attributes;
	private JsonApiRequest<ProductAttributes> request;
	private JsonApiResponse<ProductAttributes> response;

	@BeforeEach
	void setUp() {
		attributes = new ProductAttributes("Test Product", 1000L);
		request = new JsonApiRequest<>();
		request.setData(new JsonApiRequest.JsonApiData<>("products", null, attributes));

		response = new JsonApiResponse<>();
		response.setData(new JsonApiResponse.JsonApiData<>("products", UUID.randomUUID().toString(), attributes));
	}

	@Test
	void create_shouldReturn200() throws Exception {
		when(productService.create(any())).thenReturn(response);

		mockMvc.perform(post("/v1/product").header("X-API-KEY", "S3CR3T_K3Y_PR0DUCT_S3RV1C3").contentType("application/vnd.api+json")
				.content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.attributes.name").value("Test Product"));
	}

	@Test
	void getById_shouldReturn200() throws Exception {
		when(productService.getById("1")).thenReturn(response);

		mockMvc.perform(get("/v1/product/1").header("X-API-KEY", "S3CR3T_K3Y_PR0DUCT_S3RV1C3")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.attributes.name").value("Test Product"));
	}

	@Test
	void update_shouldReturn200() throws Exception {
		when(productService.update(eq("1"), any())).thenReturn(response);

		mockMvc.perform(put("/v1/product/1").header("X-API-KEY", "S3CR3T_K3Y_PR0DUCT_S3RV1C3").contentType("application/vnd.api+json")
				.content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.attributes.name").value("Test Product"));
	}

	@Test
	void delete_shouldReturn204() throws Exception {
		mockMvc.perform(delete("/v1/product/1").header("X-API-KEY", "S3CR3T_K3Y_PR0DUCT_S3RV1C3")).andExpect(status().isNoContent());
	}

	@Test
	void list_shouldReturn200() throws Exception {
		JsonApiResponse<ProductAttributes> productResponse = new JsonApiResponse<>(
				new JsonApiResponse.JsonApiData<>("products", "1", attributes));
		JsonApiListResponse<ProductAttributes> listResponse = new JsonApiListResponse<>();
		listResponse.setData(List.of(productResponse));

		when(productService.list(PageRequest.of(0, 10))).thenReturn(listResponse);

		mockMvc.perform(get("/v1/product?page=0&size=10").header("X-API-KEY", "S3CR3T_K3Y_PR0DUCT_S3RV1C3")).andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].data.attributes.name").value("Test Product"));
	}
}
