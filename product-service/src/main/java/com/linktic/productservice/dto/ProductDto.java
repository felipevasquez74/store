package com.linktic.productservice.dto;

import io.katharsis.resource.annotations.JsonApiId;
import io.katharsis.resource.annotations.JsonApiResource;
import lombok.Data;

@JsonApiResource(type = "products")
@Data
public class ProductDto {
	@JsonApiId
	private String id;
	private String name;
	private String price;
}