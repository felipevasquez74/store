package com.linktic.productservice.model;

import lombok.Data;

@Data
public class JsonApiRequest<T> {
	private JsonApiData<T> data;

	@Data
	public static class JsonApiData<T> {
		private String type;
		private String id;
		private T attributes;
	}
}