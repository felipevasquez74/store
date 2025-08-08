package com.linktic.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linktic.productservice.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

}
