package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import com.example.onlinestore.util.TextNormalizer;
import java.util.stream.Collectors;
import java.math.BigDecimal;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CartItemRepository cartItemRepository;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.cartItemRepository = cartItemRepository;
    }

    public List<Product> searchProducts(String name, String brand) {
        if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(name);
        } else if (brand != null && !brand.isEmpty()) {
            return productRepository.findByBrandContainingIgnoreCase(brand);
        } else {
            return productRepository.findByDeletedFalse();

        }
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
    }

    public void softDeleteById(Long id) {
        Product product = getById(id);
        product.setDeleted(true);
        save(product);
    }

    public List<Product> getByCategoryActive(Long categoryId) {
        return productRepository.findByCategoryIdAndDeletedFalse(categoryId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public List<Product> getByCategoryAll(Long categoryId) {
        return productRepository.findAllByCategory_Id(categoryId);
    }

    public List<Product> search(String keyword, Long categoryId, String brand,
                                Double minWeight, Double maxWeight,
                                BigDecimal minPrice, BigDecimal maxPrice,
                                String composition, String sort)
    {
        List<Product> products = productRepository.findByDeletedFalse();

        if (keyword != null && !keyword.isBlank()) {
            String normalizedKeyword = TextNormalizer.normalize(keyword);
            products = products.stream()
                    .filter(p -> TextNormalizer.normalize(p.getName()).contains(normalizedKeyword))
                    .collect(Collectors.toList());
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        if (brand != null && !brand.isBlank()) {
            String normalizedBrand = TextNormalizer.normalize(brand);
            products = products.stream()
                    .filter(p -> TextNormalizer.normalize(p.getBrand()).contains(normalizedBrand))
                    .collect(Collectors.toList());
        }

        if (minWeight != null) {
            products = products.stream()
                    .filter(p -> p.getWeight() != null && p.getWeight() >= minWeight)
                    .collect(Collectors.toList());
        }

        if (maxWeight != null) {
            products = products.stream()
                    .filter(p -> p.getWeight() != null && p.getWeight() <= maxWeight)
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        if (composition != null && !composition.isBlank()) {
            String normalizedComposition = TextNormalizer.normalize(composition);
            products = products.stream()
                    .filter(p -> TextNormalizer.normalize(p.getComposition()).contains(normalizedComposition))
                    .collect(Collectors.toList());
        }


        if (sort != null) {
            switch (sort) {
                case "priceAsc":
                    products.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "priceDesc":
                    products.sort(Comparator.comparing(Product::getPrice).reversed());
                    break;
                case "ratingAsc":
                    products.sort(Comparator.comparing(Product::getAverageRating, Comparator.nullsLast(Double::compareTo)));
                    break;
                case "ratingDesc":
                    products.sort(Comparator.comparing(Product::getAverageRating, Comparator.nullsLast(Double::compareTo)).reversed());
                    break;
                case "nameAsc":
                    products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "nameDesc":
                    products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
            }

        }

        return products;
    }
}
