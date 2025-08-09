package com.linktic.inventaryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributes {
	@NotBlank(message = "Name is required")
	private String name;
	@PositiveOrZero(message = "Price must be >= 0")
	private Long price;
}