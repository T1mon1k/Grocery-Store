package com.example.onlinestore.repository;

import com.example.onlinestore.entity.Product;
import com.example.onlinestore.entity.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Всі відгуки для товару, відсортовані за датою (останні зверху)
    List<Review> findAllByProductOrderByCreatedAtDesc(Product product);

    // Середній рейтинг (може бути null, якщо немає жодного)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :pid")
    Double findAverageRating(@Param("pid") Long productId);
}
