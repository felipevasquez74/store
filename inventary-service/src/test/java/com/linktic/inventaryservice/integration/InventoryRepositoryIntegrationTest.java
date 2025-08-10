package com.linktic.inventaryservice.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.linktic.inventaryservice.entity.Inventory;
import com.linktic.inventaryservice.repository.InventoryRepository;

@Testcontainers
@SpringBootTest
class InventoryRepositoryIntegrationTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("testdb").withUsername("test")
			.withPassword("test");

	@Autowired
	private InventoryRepository inventoryRepository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void testSaveAndFindInventory() {
		Inventory inventory = new Inventory();
		inventory.setProductId("P001");
		inventory.setQuantity(10);

		inventoryRepository.save(inventory);

		Optional<Inventory> found = inventoryRepository.findById("P001");
		assertTrue(found.isPresent());
		assertEquals(10, found.get().getQuantity());
	}
}