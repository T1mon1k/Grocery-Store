package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProductOrderByCreatedAtDesc(Product product);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :pid")
    Double findAverageRating(@Param("pid") Long productId);
    List<Review> findByProductId(Long productId);
}
