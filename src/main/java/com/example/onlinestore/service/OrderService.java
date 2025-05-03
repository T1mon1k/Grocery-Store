package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.repository.CartRepository;
import com.example.onlinestore.notification.OrderObserver;
import com.example.onlinestore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.onlinestore.repository.UserRepository;
import com.example.onlinestore.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired private ProductRepository productRepository;


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

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setShippingAddress(address);
        order.setDeliveryMethod(deliveryMethod);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            int newStock = product.getStock() - ci.getQuantity();
            if (newStock < 0) {
                throw new RuntimeException("Недостатньо товару: " + product.getName() +
                        ". Залишилось лише " + product.getStock());
            }

            product.setStock(newStock);
            productRepository.save(product);

            OrderItem oi = new OrderItem(product, ci.getQuantity(), ci.getProduct().getPrice());
            oi.setOrder(order);
            order.getItems().add(oi);
        }


        // 3) Обчислюємо загальну ціну
        BigDecimal total = order.getItems().stream()
                .map(oi -> oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        // 4) Зберігаємо замовлення
        Order saved = orderRepository.save(order);

        // 5) Очищаємо кошик
        cart.getItems().clear();
        cartRepository.save(cart);

        // 6) Сповіщаємо про замовлення
        notificationService.sendOrderConfirmation(saved);

        return saved;
    }



    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Замовлення не знайдено: " + orderId);
        }
        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setStatus(newStatus);
        orderRepository.save(order);
        observers.forEach(obs ->
                obs.onStatusChanged(order, oldStatus, newStatus)
        );
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
