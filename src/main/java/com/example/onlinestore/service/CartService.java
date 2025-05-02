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

    /**
     * Створити кошик для користувача
     */
    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Додати товар до кошика авторизованого користувача
     */
    public Cart addItemForCurrentUser(Long productId, int quantity, Principal principal) {
        String username = principal.getName();

        // Отримати користувача з бази
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        // Знайти кошик або створити новий
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // Знайти продукт
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

        // Перевірити, чи вже є такий товар у кошику
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


    /**
     * Отримати всі товари в кошику користувача
     */
    public List<CartItem> getUserCartItems(String username) {
        Cart cart = cartRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Кошик не знайдено"));
        return cart.getItems();
    }


    /**
     * Отримати кошик за ID
     */
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Кошик не знайдено: " + cartId));
    }

    /**
     * Додати товар до кошика за ID
     */
    public Cart addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = getCart(cartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не знайдено: " + productId));

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

    /**
     * Очистити кошик
     */
    public void clearCart(Long cartId) {
        Cart cart = getCart(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Загальна сума товарів у кошику
     */
    public BigDecimal getTotal(Long cartId) {
        Cart cart = getCart(cartId);
        return cart.getTotal();
    }

    public Cart getOrCreateCartForUser(User user) {
        // Пошук існуючого кошика за user_id
        Optional<Cart> existing = cartRepository.findByUser(user);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Якщо не існує — створити новий
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    /**
     * Оновлює всі кількості в кошику,
     * keys виду "quantity_{productId}", values — числові рядки.
     */
    public void updateQuantities(Map<String, String> allParams) {
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("quantity_")) {
                try {
                    Long productId = Long.parseLong(key.substring("quantity_".length()));
                    int qty = Integer.parseInt(entry.getValue());
                    // знайти CartItem по productId і оновити quantity
                    CartItem item = cartItemRepository
                            .findByProduct_Id(productId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Немає такого товару в кошику: " + productId));
                    item.setQuantity(qty);
                    cartItemRepository.save(item);
                } catch (NumberFormatException ignored) {
                    // ігноруємо поля, що не конвертуються
                }
            }
        }
    }
}
