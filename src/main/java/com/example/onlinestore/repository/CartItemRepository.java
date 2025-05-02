package com.example.onlinestore.repository;

import com.example.onlinestore.entity.CartItem;
import com.example.onlinestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartUser(User user);
    Optional<CartItem> findByProduct_Id(Long productId);
}
