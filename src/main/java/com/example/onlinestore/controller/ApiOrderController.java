package com.example.onlinestore.controller;

import com.example.onlinestore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request
    ) {
        String newStatus = request.get("status");
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(Map.of("message", "Статус оновлено успішно"));
    }
}
