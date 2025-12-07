package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Short> {

    List<Product> findByIsActiveTrue();
}