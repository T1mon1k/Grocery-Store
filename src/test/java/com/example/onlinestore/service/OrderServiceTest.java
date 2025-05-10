package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.notification.OrderObserver;
import com.example.onlinestore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private NotificationService notificationService;
    @Mock private ProductRepository productRepository;
    @Mock private OrderObserver observer;

    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        orderService = new OrderService();

        inject(orderService, "orderRepository", orderRepository);
        inject(orderService, "cartRepository", cartRepository);
        inject(orderService, "userRepository", userRepository);
        inject(orderService, "cartItemRepository", cartItemRepository);
        inject(orderService, "notificationService", notificationService);
        inject(orderService, "productRepository", productRepository);
        inject(orderService, "observers", List.of(observer));

        user = new User();
        user.setUsername("testUser");

        product = new Product();
        product.setId(1L);
        product.setName("Apple");
        product.setStock(10);
        product.setPrice(new BigDecimal("5.00"));

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(cartRepository.save(any())).thenReturn(cart);

        Order result = orderService.createOrder(
                "testUser", "Ім'я", "123456", "Адреса",
                "Доставка", "Готівка", "Коментар"
        );

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("10.00"), result.getTotalPrice());
        verify(notificationService).sendOrderConfirmation(result);
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateOrderStatus_shouldNotifyObservers() {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(1L, "SHIPPED");

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify(observer).onStatusChanged(order, OrderStatus.CREATED, OrderStatus.SHIPPED);
        verify(orderRepository).save(order);
    }

    @Test
    void getOrdersByUsername_shouldReturnUserOrders() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(new Order()));

        List<Order> orders = orderService.getOrdersByUsername("testUser");

        assertEquals(1, orders.size());
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order(), new Order()));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
    }
}
