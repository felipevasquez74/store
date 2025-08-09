package com.linktic.productservice.util;

import org.springframework.stereotype.Component;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.entity.Product;

@Component
public class ProductMapper {

	public Product toEntity(ProductAttributes dto) {
		Product entity = new Product();
		entity.setName(dto.getName());
		entity.setPrice(dto.getPrice());
		return entity;
	}

	public ProductAttributes toDto(Product entity) {
		return new ProductAttributes(entity.getName(), entity.getPrice());
	}

	public void updateEntityFromDto(ProductAttributes dto, Product entity) {
		entity.setName(dto.getName());
		entity.setPrice(dto.getPrice());
	}
}