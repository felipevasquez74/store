package com.linktic.productservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.linktic.productservice.dto.ProductAttributes;
import com.linktic.productservice.model.JsonApiRequest;
import com.linktic.productservice.model.JsonApiResponse;

public interface IProductService {

    JsonApiResponse<ProductAttributes> create(JsonApiRequest<ProductAttributes> request);

    Optional<JsonApiResponse<ProductAttributes>> getById(String id);

    Optional<JsonApiResponse<ProductAttributes>> update(String id, JsonApiRequest<ProductAttributes> request);

    void delete(String id);

    Page<JsonApiResponse<ProductAttributes>> list(Pageable pageable);
}