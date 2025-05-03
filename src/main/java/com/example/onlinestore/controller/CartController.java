package com.example.onlinestore.controller;

import com.example.onlinestore.entity.CartItem;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.service.CartService;
import com.example.onlinestore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        List<CartItem> items = cartService.getUserCartItems(principal.getName());
        model.addAttribute("items", items);
        return "cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Map<String, String> allParams) {
        cartService.updateQuantities(allParams);
        return "redirect:/orders/new";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal) {
        cartService.addItemForCurrentUser(productId, quantity, principal);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        List<CartItem> items = cartService.getUserCartItems(principal.getName());
        if (!items.isEmpty()) {
            Long cartId = items.get(0).getCart().getId(); // Беремо кошик з першого товару
            cartService.clearCart(cartId);
        }
        return "redirect:/cart";
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
        model.addAttribute("product", product);
        return "product_card";
    }

    @PostMapping("/delete")
    public String deleteItem(@RequestParam("productId") Long productId, Principal principal) {
        cartService.removeItemByProductId(productId, principal.getName());
        return "redirect:/cart";
    }

}
