package com.example.onlinestore.controller;

import com.example.onlinestore.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.onlinestore.entity.Category;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.PriceType;
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
import java.math.BigDecimal;


@Controller
@RequestMapping("/products")
public class ViewProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @Autowired
    private ProductRepository productRepository;

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
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String composition,
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
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("composition", composition);
        model.addAttribute("newCategory", new Category());
        boolean isFiltering = keyword != null || categoryId != null || brand != null || minWeight != null ||
                maxWeight != null || minPrice != null || maxPrice != null || composition != null || sort != null;
        if (isFiltering) {
            List<Product> products = productService.search(keyword, categoryId, brand, minWeight, maxWeight, minPrice, maxPrice, composition, sort);
            model.addAttribute("products", products);
        } else {
            Map<Category, List<Product>> productsByCategory = new LinkedHashMap<>();
            for (Category c : categories) {
                productsByCategory.put(c, productService.getByCategoryActive(c.getId()));
            }
            model.addAttribute("productsByCategory", productsByCategory);
        }
        return "products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.softDeleteById(id);
        return "redirect:/products";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute("newCategory") Category category) {
        categoryService.save(category);
        return "redirect:/products";
    }

    private Category getOrCreateUncategorizedCategory() {
        String uncategorizedName = "Без категорії";
        return categoryService.findByName(uncategorizedName)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(uncategorizedName);
                    return categoryService.save(newCategory);
                });
    }

    @PostMapping("/categories/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@RequestParam("categoryId") Long categoryId) {
        Category toDelete = categoryService.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категорію не знайдено"));
        Category uncategorized = getOrCreateUncategorizedCategory();
        List<Product> products = productService.getByCategoryAll(categoryId);
        for (Product product : products) {
            product.setCategory(uncategorized);
            productService.save(product);
        }
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
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "edit_product";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam BigDecimal price,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) Double weight,
                                @RequestParam(required = false) String originCountry,
                                @RequestParam(required = false) String composition,
                                @RequestParam Integer stock,
                                @RequestParam(required = false) String brand,
                                @RequestParam PriceType priceType,
                                @RequestParam(required = false) Double volumeInLiters) {
        Product product = productService.getById(id);
        Category category = categoryService.getById(categoryId);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setWeight(weight);
        product.setOriginCountry(originCountry);
        product.setComposition(composition);
        product.setStock(stock);
        product.setBrand(brand);
        product.setPriceType(priceType);
        product.setVolumeInLiters(volumeInLiters);
        productService.save(product);
        return "redirect:/products/" + id;
    }

}
