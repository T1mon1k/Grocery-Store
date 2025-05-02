package com.example.onlinestore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.onlinestore.entity.Category;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.service.CategoryService;
import com.example.onlinestore.service.ProductService;
import com.example.onlinestore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/products")
public class ViewProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @Autowired
    public ViewProductController(ProductService productService, CategoryService categoryService, ReviewService reviewService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minWeight,
            @RequestParam(required = false) Double maxWeight,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("brand", brand);
        model.addAttribute("minWeight", minWeight);
        model.addAttribute("maxWeight", maxWeight);
        model.addAttribute("sort", sort);
        model.addAttribute("newCategory", new Category());

        boolean isFiltering = keyword != null || categoryId != null || brand != null || minWeight != null || maxWeight != null || sort != null;

        if (isFiltering) {
            List<Product> products = productService.search(keyword, categoryId, brand, minWeight, maxWeight, sort);
            model.addAttribute("products", products);
        } else {
            Map<Category, List<Product>> productsByCategory = new LinkedHashMap<>();
            for (Category c : categories) {
                productsByCategory.put(c, productService.getByCategory(c.getId()));
            }
            model.addAttribute("productsByCategory", productsByCategory);
        }

        return "products";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute("newCategory") Category category) {
        categoryService.save(category);
        return "redirect:/products";
    }

    @PostMapping("/categories/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@RequestParam("categoryId") Long categoryId) {
        categoryService.deleteById(categoryId);
        return "redirect:/products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "product_form";
    }

    @PostMapping
    public String createProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("categoryId") Long categoryId
    ) {
        Category cat = categoryService.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категорію не знайдено"));
        product.setCategory(cat);
        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не знайдено: " + id));
        model.addAttribute("product", product);

        model.addAttribute("reviews", reviewService.getReviewsForProduct(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));

        return "product_detail";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "product_form"; // сторінка з формою редагування
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }


}
