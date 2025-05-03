package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByName(@Param("keyword") String keyword);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByBrandContainingIgnoreCase(String brand);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findAllByCategoryId(Long categoryId);
    List<Product> findByCategory(Category category);
    List<Product> findByDeletedFalse();
    List<Product> findByCategoryIdAndDeletedFalse(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name);
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findAllByCategory_Id(Long categoryId);
}
