package com.example.onlinestore.controller;

import java.util.List;
import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.Role;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.service.UserService;
import com.example.onlinestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public AdminController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/admin/role")
    public String changeRole(@RequestParam Long userId,
                             @RequestParam Role role,
                             RedirectAttributes ra) {
        User u = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        if ("admin".equals(u.getUsername())) {
            ra.addFlashAttribute("error", "Роль головного адміна змінити не можна");
        } else {
            userService.updateRole(userId, role);
            ra.addFlashAttribute("success", "Роль оновлено");
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        List<User> users = userService.getAllUsers();
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("users", users);
        model.addAttribute("roles", List.of("ROLE_USER", "ROLE_ADMIN"));
        model.addAttribute("orders", orders);

        return "admin";
    }

}
