package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private final CategoryService categoryService;  // щоб знайти Category по id

    public ProductService(ProductRepository repo, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }


    /** Повернути всі товари */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /** Пошук товарів за назвою або брендом */
    public List<Product> searchProducts(String name, String brand) {
        if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(name);
        } else if (brand != null && !brand.isEmpty()) {
            return productRepository.findByBrandContainingIgnoreCase(brand);
        } else {
            return productRepository.findAll();
        }
    }

    /** Повернути товар за ID */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /** Створити або оновити товар */
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }


    // Новий метод для всіх продуктів
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void save(Product product) {
        productRepository.save(product);
    }


    public List<Product> getByCategory(Long categoryId) {
        if (categoryId == null) {
            return productRepository.findAll();
        } else {
            return productRepository.findByCategoryId(categoryId);
        }
    }

    public List<Product> search(String name, Long categoryId, String brand) {
        // початковий набір — всі товари
        List<Product> all = productRepository.findAll();

        if (name != null && !name.isBlank()) {
            all = all.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (brand != null && !brand.isBlank()) {
            all = all.stream()
                    .filter(p -> p.getBrand() != null
                            && p.getBrand().toLowerCase().contains(brand.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (categoryId != null) {
            Category cat = categoryService.findById(categoryId)
                    .orElse(null);
            if (cat != null) {
                all = all.stream()
                        .filter(p -> p.getCategory() != null
                                && p.getCategory().getId().equals(categoryId))
                        .collect(Collectors.toList());
            }
        }
        return all;
    }

    public List<Product> search(String keyword, Long categoryId, String brand,
                                Double minWeight, Double maxWeight, String sort) {
        List<Product> results = productRepository.findAll().stream()
                .filter(p -> keyword == null || p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> categoryId == null || (p.getCategory() != null && p.getCategory().getId().equals(categoryId)))
                .filter(p -> brand == null || (p.getBrand() != null && p.getBrand().toLowerCase().contains(brand.toLowerCase())))
                .filter(p -> minWeight == null || p.getWeight() >= minWeight)
                .filter(p -> maxWeight == null || p.getWeight() <= maxWeight)
                .sorted(getComparator(sort))
                .collect(Collectors.toList());

        return results;
    }

    private Comparator<Product> getComparator(String sort) {
        if ("price_asc".equals(sort)) {
            return Comparator.comparing(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            return Comparator.comparing(Product::getPrice).reversed();
        } else if ("weight_asc".equals(sort)) {
            return Comparator.comparing(Product::getWeight);
        } else if ("weight_desc".equals(sort)) {
            return Comparator.comparing(Product::getWeight).reversed();
        } else if ("category_asc".equals(sort)) {
            return Comparator.comparing(p -> p.getCategory() != null ? p.getCategory().getName() : "");
        } else if ("category_desc".equals(sort)) {
            return Comparator.comparing((Product p) -> p.getCategory() != null ? p.getCategory().getName() : "").reversed();
        } else if ("brand_asc".equals(sort)) {
            return Comparator.comparing(p -> p.getBrand() != null ? p.getBrand() : "");
        } else if ("brand_desc".equals(sort)) {
            return Comparator.comparing((Product p) -> p.getBrand() != null ? p.getBrand() : "").reversed();
        }

        return Comparator.comparing(Product::getId); // default
    }



}
