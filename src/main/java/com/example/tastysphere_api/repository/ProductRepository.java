package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByMerchantId(Long id);
}