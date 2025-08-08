package com.linktic.productservice.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.entity.Product;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;
import com.linktic.productservice.model.JsonApiResponse.JsonApiData;
import com.linktic.productservice.repository.ProductRepository;

@Service
public class ProductServiceImpl implements IProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public JsonApiResponse<ProductAttributes> create(JsonApiRequest<ProductAttributes> request) {
		ProductAttributes attrs = request.getData().getAttributes();
		Product entity = new Product();
		entity.setId(UUID.randomUUID().toString());
		entity.setName(attrs.getName());
		entity.setPrice(attrs.getPrice());
		Product saved = productRepository.save(entity);

		return new JsonApiResponse<>(
				new JsonApiData<>("products", saved.getId(), new ProductAttributes(saved.getName(), saved.getPrice())));
	}

	@Override
	public Optional<JsonApiResponse<ProductAttributes>> getById(String id) {
		return productRepository.findById(id).map(p -> new JsonApiResponse<>(
				new JsonApiData<>("products", p.getId(), new ProductAttributes(p.getName(), p.getPrice()))));
	}

	@Override
	public Optional<JsonApiResponse<ProductAttributes>> update(String id, JsonApiRequest<ProductAttributes> request) {
		ProductAttributes attrs = request.getData().getAttributes();
		return productRepository.findById(id).map(existing -> {
			existing.setName(attrs.getName());
			existing.setPrice(attrs.getPrice());
			Product saved = productRepository.save(existing);
			return new JsonApiResponse<>(new JsonApiData<>("products", saved.getId(),
					new ProductAttributes(saved.getName(), saved.getPrice())));
		});
	}

	@Override
	public void delete(String id) {
		productRepository.deleteById(id);
	}

	@Override
	public Page<JsonApiResponse<ProductAttributes>> list(Pageable pageable) {
		return productRepository.findAll(pageable).map(p -> new JsonApiResponse<>(
				new JsonApiData<>("products", p.getId(), new ProductAttributes(p.getName(), p.getPrice()))));
	}
}