package com.linktic.productservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.linktic.productservice.dto.ProductDto;

public interface IProductService {
	ProductDto create(ProductDto dto);

	Optional<ProductDto> getById(String id);

	Optional<ProductDto> update(String id, ProductDto dto);

	void delete(String id);

	Page<ProductDto> list(Pageable pageable);
}
