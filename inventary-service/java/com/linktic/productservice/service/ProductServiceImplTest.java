package com.linktic.productservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.entity.Product;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiRequest.JsonApiData;
import com.linktic.productservice.repository.ProductRepository;
import com.linktic.productservice.util.ProductMapper;

import jakarta.persistence.EntityNotFoundException;

class ProductServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductServiceImpl productService;

	private Product product;
	private ProductAttributes attributes;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		attributes = new ProductAttributes("Test Product", 1200L);
		product = new Product();
		product.setId(UUID.randomUUID().toString());
		product.setName("Test Product");
		product.setPrice(1200L);
	}

	@Test
	void create_shouldSaveProduct() {
		JsonApiRequest<ProductAttributes> request = new JsonApiRequest<>();
		JsonApiData<ProductAttributes> data = new JsonApiData<>("products", null, attributes);
		request.setData(data);

		when(productMapper.toEntity(attributes)).thenReturn(product);
		when(productRepository.save(any(Product.class))).thenReturn(product);
		when(productMapper.toDto(product)).thenReturn(attributes);

		var response = productService.create(request);

		assertNotNull(response);
		assertEquals("Test Product", response.getData().getAttributes().getName());
		verify(productRepository).save(any(Product.class));
	}

	@Test
	void getById_shouldReturnProduct_whenExists() {
		when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
		when(productMapper.toDto(product)).thenReturn(attributes);

		var response = productService.getById(product.getId());

		assertEquals("Test Product", response.getData().getAttributes().getName());
	}

	@Test
	void getById_shouldThrow_whenNotFound() {
		when(productRepository.findById(anyString())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> productService.getById("no-id"));
	}

	@Test
	void update_shouldModifyProduct_whenExists() {
		when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);
		when(productMapper.toDto(product)).thenReturn(attributes);

		var response = productService.update(product.getId(), attributes);

		assertEquals("Test Product", response.getData().getAttributes().getName());
	}

	@Test
	void list_shouldReturnProducts() {
		when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(product)));
		when(productMapper.toDto(product)).thenReturn(attributes);

		var result = productService.list(Pageable.ofSize(10));

		assertEquals(1, result.getData().size());
	}

	@Test
	void delete_shouldRemoveProduct_whenExists() {
		when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

		productService.delete(product.getId());

		verify(productRepository).delete(product);
	}

	@Test
	void delete_shouldThrow_whenNotFound() {
		when(productRepository.findById(anyString())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> productService.delete("no-id"));
	}

	@Test
	void update_shouldThrow_whenNotFound() {
		when(productRepository.findById(anyString())).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> productService.update("no-id", attributes));
	}

	@Test
	void create_shouldThrow_whenAttributesNull() {
		JsonApiRequest<ProductAttributes> request = new JsonApiRequest<>();
		request.setData(new JsonApiData<>("products", null, null));
		assertThrows(NullPointerException.class, () -> productService.create(request));
	}
	
	@Test
	void list_shouldReturnEmpty_whenNoProducts() {
	    when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
	    var result = productService.list(Pageable.ofSize(10));
	    assertNotNull(result);
	    assertEquals(0, result.getData().size());
	}
}