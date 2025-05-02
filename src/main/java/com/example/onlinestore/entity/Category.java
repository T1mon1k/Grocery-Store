package com.example.onlinestore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories") // 🔄 згідно зі специфікацією – у множині
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // ======== Конструктори ========
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // ======== Геттери / Сеттери ========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
