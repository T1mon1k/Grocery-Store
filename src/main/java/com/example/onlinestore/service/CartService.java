package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.repository.CartRepository;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart addItemForCurrentUser(Long productId, int quantity, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public List<CartItem> getUserCartItems(String username) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Кошик не знайдено"));
        return cart.getItems();
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Кошик не знайдено: " + cartId));
    }

    public void clearCart(Long cartId) {
        Cart cart = getCart(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void updateQuantities(Map<String, String> allParams) {
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("quantity_")) {
                try {
                    Long productId = Long.parseLong(key.substring("quantity_".length()));
                    int qty = Integer.parseInt(entry.getValue());
                    CartItem item = cartItemRepository
                            .findByProduct_Id(productId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Немає такого товару в кошику: " + productId));
                    item.setQuantity(qty);
                    cartItemRepository.save(item);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public void removeItemByProductId(Long productId, String username) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Кошик не знайдено"));
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }


}
