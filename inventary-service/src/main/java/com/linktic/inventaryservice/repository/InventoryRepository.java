package com.linktic.inventaryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linktic.inventaryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
