package com.example.onlinestore.controller;

import com.example.onlinestore.service.CartService;
import com.example.onlinestore.service.OrderService;
import com.example.onlinestore.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // Покажемо форму замовлення (GET /orders/new)
    @GetMapping("/new")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order_form";
    }

    // Обробимо сабміт із форми детальних даних замовлення (POST /orders/submit)
    @PostMapping("/submit")
    public String submitOrder(@RequestParam String name,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam String deliveryMethod,
                              @RequestParam String paymentMethod,
                              @RequestParam(required = false) String comment,
                              Principal principal) {

        orderService.createOrder(
                principal.getName(),
                name, phone, address,
                deliveryMethod,
                paymentMethod,
                comment
        );

        return "redirect:/orders/user_orders";
    }

    @PostMapping("/new")
    public String createOrderFromCart(@RequestParam Map<String, String> allQty, Principal principal) {
        cartService.updateQuantities(allQty);
        orderService.createOrderFromCart(principal.getName());
        return "redirect:/orders/confirmation";
    }

    @GetMapping("/user_orders")
    public String viewUserOrders(Model model, Principal principal) {
        model.addAttribute("orders", orderService.getOrdersByUsername(principal.getName()));
        return "user_orders";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId,
                                   Model model,
                                   Principal principal) {
        Order order = orderService.getOrderById(orderId);
        // Перевірка доступу: або власник, або адмін
        if ("admin".equals(principal.getName()) ||
                order.getUser().getUsername().equals(principal.getName())) {
            model.addAttribute("order", order);
            return "order_details";
        }
        return "redirect:/orders/user_orders";
    }

    @PostMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') "
            + "and (#status == 'PAID' or #status == 'CANCELLED'))")
    public String updateStatus(
            @PathVariable Long orderId,
            @RequestParam("status") String status) {

        orderService.updateOrderStatus(orderId, status);
        // Після зміни вертаємося на сторінку деталей
        return "redirect:/orders/" + orderId;
    }
}
