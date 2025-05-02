// src/main/java/com/example/onlinestore/entity/OrderStatus.java
package com.example.onlinestore.entity;

public enum OrderStatus {
    CREATED("Створено"),
    ACCEPTED("Підтверджено"),
    PAID("Сплачено"),
    PROCESSING("В обробці"),
    SHIPPED("Відправлено"),
    DELIVERED("Доставлено"),
    CANCELLED("Скасовано");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
