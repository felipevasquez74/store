package com.linktic.productservice.service;

import org.springframework.data.domain.Pageable;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.model.JsonApiListResponse;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;

public interface IProductService {

	JsonApiResponse<ProductAttributes> create(JsonApiRequest<ProductAttributes> request);

	JsonApiResponse<ProductAttributes> getById(String id);

	JsonApiResponse<ProductAttributes> update(String id, ProductAttributes productAttributes);

	void delete(String id);

	JsonApiListResponse<ProductAttributes> list(Pageable pageable);
}