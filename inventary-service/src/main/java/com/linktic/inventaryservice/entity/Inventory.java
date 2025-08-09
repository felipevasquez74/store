package com.linktic.inventaryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory")
public class Inventory {
	@Id
	private String productId;
	private Integer quantity;
}