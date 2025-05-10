package com.example.onlinestore.service;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.util.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Milk");
        product.setBrand("BrandA");
        product.setPrice(new BigDecimal("20.00"));
        product.setWeight(1.0);
        product.setDeleted(false);
        product.setComposition("Milk, sugar");
    }

    @Test
    void searchProducts_shouldSearchByName() {
        when(productRepository.findByNameContainingIgnoreCase("milk")).thenReturn(List.of(product));

        List<Product> result = productService.searchProducts("milk", null);

        assertEquals(1, result.size());
        assertEquals("Milk", result.get(0).getName());
    }

    @Test
    void searchProducts_shouldSearchByBrand() {
        when(productRepository.findByBrandContainingIgnoreCase("BrandA")).thenReturn(List.of(product));

        List<Product> result = productService.searchProducts(null, "BrandA");

        assertEquals(1, result.size());
        assertEquals("BrandA", result.get(0).getBrand());
    }

    @Test
    void getProductById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Milk", result.get().getName());
    }

    @Test
    void addProduct_shouldSaveProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.addProduct(product);

        assertNotNull(result);
        assertEquals("Milk", result.getName());
    }

    @Test
    void getById_shouldReturnProductOrThrow() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("Milk", result.getName());
    }

    @Test
    void softDeleteById_shouldMarkProductAsDeleted() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.softDeleteById(1L);

        assertTrue(product.isDeleted());
        verify(productRepository).save(product);
    }
}
