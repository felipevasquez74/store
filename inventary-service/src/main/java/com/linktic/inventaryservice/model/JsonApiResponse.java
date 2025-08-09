package com.linktic.inventaryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiResponse<T> {
	private JsonApiData<T> data;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JsonApiData<T> {
		private String type;
		private String id;
		private T attributes;
	}
}