package com.linktic.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiSingleResponse<T> {
	private JsonApiResponse.JsonApiData<T> data;
	private Links links;
}