package com.example.onlinestore.controller;

import com.example.onlinestore.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String addReview(@PathVariable Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal) {

        reviewService.addReview(
                principal.getName(),
                productId,
                rating,
                comment
        );
        return "redirect:/products/" + productId + "#reviews";
    }
}
