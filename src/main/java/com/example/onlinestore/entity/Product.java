package com.example.onlinestore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "weight")
    private Double weight;

    private Double volumeInLiters;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "composition", length = 2000)
    private String composition;

    @Column(nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    public String getPriceUnit() {
        return priceType == PriceType.PER_KG ? "кг" : "шт";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @Formula(
            "(" +
                    "select coalesce(avg(r.rating),0) " +
                    "from reviews r " +
                    "where r.product_id = id" +
                    ")"
    )

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    public boolean isDeleted() { return deleted; }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public Double getWeight() {
        return weight;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public String getComposition() {
        return composition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public Double getAverageRating() { return averageRating; }

    public PriceType getPriceType() { return priceType; }

    public Double getVolumeInLiters() { return volumeInLiters; }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public void setPriceType(PriceType priceType) { this.priceType = priceType; }

    public void setVolumeInLiters(Double volumeInLiters) { this.volumeInLiters = volumeInLiters; }
}
