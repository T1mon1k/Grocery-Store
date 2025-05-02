package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.repository.CartRepository;
import com.example.onlinestore.notification.OrderObserver;
import com.example.onlinestore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.onlinestore.repository.UserRepository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private NotificationService notificationService;


    /** Spring автоматично підставить усі біні, які реалізують OrderObserver */
    @Autowired
    private List<OrderObserver> observers;



    /**
     * Створює нове замовлення та кидає подію CREATED
     */
    public Order createOrder(String username,
                             String customerName,
                             String phone,
                             String address,
                             String deliveryMethod,
                             String paymentMethod,
                             String comment) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Кошик порожній");
        }

        // 1) Створюємо Order
        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setShippingAddress(address);
        order.setDeliveryMethod(deliveryMethod);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem(ci.getProduct(), ci.getQuantity(), ci.getProduct().getPrice());
            oi.setOrder(order);
            order.getItems().add(oi);
            total = total.add(ci.getTotalPrice());
        }
        order.setTotalPrice(total);

        // 2) Зберігаємо в БД
        Order saved = orderRepository.save(order);

        // 3) Очищаємо кошик
        cart.getItems().clear();
        cartRepository.save(cart);

        // 4) Сповіщаємо всіх Observer-ів про створення замовлення
        //    oldStatus = null, newStatus = CREATED
        notificationService.sendOrderConfirmation(saved);

        return saved;
    }


    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(OrderStatus.valueOf(status));;
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void createOrderFromCart(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Unknown user " + username));

        List<CartItem> items = cartItemRepository.findByCartUser(user);
        if (items.isEmpty()) {
            throw new IllegalStateException("Кошик порожній");
        }

        // 1) створюємо замовлення
        Order order = new Order();
        order.setUser(user);
        order = orderRepository.save(order);

        // 2) для кожного CartItem створюємо OrderItem
        for (CartItem ci : items) {
            var oi = new OrderItem(
                    ci.getProduct(),
                    ci.getQuantity(),
                    ci.getProduct().getPrice()
            );
            oi.setOrder(order);
            order.getItems().add(oi);
        }
        // 3) зберігаємо order разом із orderItems (Cascade.PERSIST)
        orderRepository.save(order);

        // 4) очищаємо кошик
        cartItemRepository.deleteAll(items);
    }

}
