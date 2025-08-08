package com.linktic.productservice.dto;

import io.katharsis.resource.annotations.JsonApiId;
import io.katharsis.resource.annotations.JsonApiResource;

@JsonApiResource(type = "products")
public record ProductDto(@JsonApiId String id, String name, String price) {
}