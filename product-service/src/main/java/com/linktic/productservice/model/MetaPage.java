package com.linktic.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaPage {
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
}