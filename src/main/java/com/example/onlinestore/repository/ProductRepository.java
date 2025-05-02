// src/main/java/com/example/onlinestore/repository/ProductRepository.java
package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // вже існуючі методи...
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByBrandContainingIgnoreCase(String brand);

    // новий метод — повертає всі товари певної категорії
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findByCategory(Category category);
}
