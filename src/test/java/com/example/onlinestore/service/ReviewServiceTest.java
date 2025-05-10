package com.example.onlinestore.service;

import com.example.onlinestore.entity.*;
import com.example.onlinestore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepo;
    @Mock private ProductRepository productRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks private ReviewService reviewService;

    private Product product;
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);

        user = new User();
        user.setUsername("testUser");

        review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Great product");
    }

    @Test
    void getReviewsForProduct_shouldReturnReviews() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepo.findAllByProductOrderByCreatedAtDesc(product)).thenReturn(List.of(review));

        List<Review> result = reviewService.getReviewsForProduct(1L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void getAverageRating_shouldReturnAverage() {
        when(reviewRepo.findAverageRating(1L)).thenReturn(4.5);

        double avg = reviewService.getAverageRating(1L);

        assertEquals(4.5, avg);
    }

    @Test
    void getAverageRating_shouldReturnZeroWhenNull() {
        when(reviewRepo.findAverageRating(1L)).thenReturn(null);

        double avg = reviewService.getAverageRating(1L);

        assertEquals(0.0, avg);
    }

    @Test
    void addReview_shouldSaveReviewAndUpdateRating() {
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepo.save(any())).thenReturn(review);
        when(reviewRepo.findByProductId(1L)).thenReturn(List.of(review));

        reviewService.addReview("testUser", 1L, 5, "Great product");

        verify(reviewRepo, times(2)).save(any());
        verify(productRepo).save(product);
        assertEquals(5.0, product.getAverageRating());
    }

    @Test
    void updateProductAverageRating_shouldUpdateProduct() {
        Review r1 = new Review(); r1.setRating(4);
        Review r2 = new Review(); r2.setRating(5);

        when(reviewRepo.findByProductId(1L)).thenReturn(List.of(r1, r2));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        reviewService.updateProductAverageRating(1L);

        verify(productRepo).save(product);
        assertEquals(4.5, product.getAverageRating());
    }
}
