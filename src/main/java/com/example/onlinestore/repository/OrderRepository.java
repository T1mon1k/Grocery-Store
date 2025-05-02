package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

}
