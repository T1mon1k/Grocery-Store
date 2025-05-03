package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         ProductRepository productRepo,
                         UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public List<Review> getReviewsForProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No product " + productId));
        return reviewRepo.findAllByProductOrderByCreatedAtDesc(product);
    }

    public double getAverageRating(Long productId) {
        Double avg = reviewRepo.findAverageRating(productId);
        return avg == null ? 0.0 : avg;
    }

    @Transactional
    public void addReview(String username,
                          Long productId,
                          int rating,
                          String comment) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No user " + username));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No product " + productId));

        Review r = new Review();
        r.setUser(user);
        r.setProduct(product);
        r.setRating(rating);
        r.setComment(comment);
        reviewRepo.save(r);
        reviewRepo.save(r);
        updateProductAverageRating(productId);
    }

    public void updateProductAverageRating(Long productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);
        double avg = reviews.isEmpty() ? 0.0 :
                reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
        product.setAverageRating(avg);
        productRepo.save(product);
    }


}
