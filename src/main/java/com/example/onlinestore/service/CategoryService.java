// src/main/java/com/example/onlinestore/service/CategoryService.java
package com.example.onlinestore.service;

import com.example.onlinestore.entity.Category;
import com.example.onlinestore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<Category> findAll() {
        return repo.findAll();
    }

    public Optional<Category> findById(Long id) {
        return repo.findById(id);
    }

    public Category save(Category c) {
        return repo.save(c);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
