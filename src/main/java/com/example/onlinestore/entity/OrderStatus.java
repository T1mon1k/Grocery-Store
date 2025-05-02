package com.example.onlinestore.entity;

public enum OrderStatus {
    CREATED,    // тільки створене
    ACCEPTED,   // прийняте в обробку
    PAID,       // оплачено
    PROCESSING, // в обробці
    SHIPPED,    // відправлено
    DELIVERED   // доставлено
}
