package com.linktic.productservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.entity.Product;
import com.linktic.productservice.model.JsonApiListResponse;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;
import com.linktic.productservice.model.JsonApiResponse.JsonApiData;
import com.linktic.productservice.model.Links;
import com.linktic.productservice.model.MetaPage;
import com.linktic.productservice.repository.ProductRepository;
import com.linktic.productservice.util.ProductMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

	private static final String RESOURCE_NAME = "products";

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
	}

	@Override
	public JsonApiResponse<ProductAttributes> create(JsonApiRequest<ProductAttributes> request) {
		log.info("Creating new product with attributes: {}", request.getData().getAttributes());
		Product product = productMapper.toEntity(request.getData().getAttributes());
		product.setId(UUID.randomUUID().toString());

		Product savedProduct = productRepository.save(product);
		log.info("Product created successfully with ID: {}", savedProduct.getId());

		return wrapResponse(savedProduct);
	}

	@Override
	@Transactional(readOnly = true)
	public JsonApiResponse<ProductAttributes> getById(String id) {
		log.info("Getting product by ID: {}", id);

		Product product = productRepository.findById(id).orElseThrow(
				() -> new EntityNotFoundException(String.format("The product with ID '%s' was not found.", id)));

		log.debug("Product found: {}", product.getId());
		return wrapResponse(product);
	}

	@Override
	@Transactional
	public JsonApiResponse<ProductAttributes> update(String id, ProductAttributes productAttributes) {
		log.info("Updating product with ID: {}", id);

		Product product = productRepository.findById(id).orElseThrow(
				() -> new EntityNotFoundException(String.format("The product with ID '%s' was not found.", id)));

		product.setName(productAttributes.getName());
		product.setPrice(productAttributes.getPrice());

		Product updated = productRepository.save(product);
		log.debug("Product updated: {}", updated.getId());

		return wrapResponse(updated);
	}

	@Override
	@Transactional
	public void delete(String id) {
		log.info("Deleting product with ID: {}", id);
		Product product = productRepository.findById(id).orElseThrow(
				() -> new EntityNotFoundException(String.format("The product with ID '%s' was not found.", id)));
		log.info("Product with ID {} deleted successfully", id);
		productRepository.delete(product);
	}

	@Override
	@Transactional(readOnly = true)
	public JsonApiListResponse<ProductAttributes> list(Pageable pageable) {
		log.info("Listing products. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

		Page<JsonApiResponse<ProductAttributes>> pageResult = productRepository.findAll(pageable)
				.map(this::wrapResponse);

		log.debug("Products returned: {}", pageResult.getTotalElements());

		return new JsonApiListResponse<>(pageResult.getContent(),
				new MetaPage(pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(),
						pageResult.getTotalPages()),
				new Links(
						String.format("/products?page=%d&size=%d", pageable.getPageNumber(), pageable.getPageSize())));
	}

	private JsonApiResponse<ProductAttributes> wrapResponse(Product product) {
		return new JsonApiResponse<>(new JsonApiData<>(RESOURCE_NAME, product.getId(), productMapper.toDto(product)));
	}
}
