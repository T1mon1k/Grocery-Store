package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByBrandContainingIgnoreCase(String brand);
    List<Product> findByDeletedFalse();
    List<Product> findByCategoryIdAndDeletedFalse(Long categoryId);
    List<Product> findAllByCategory_Id(Long categoryId);
}
