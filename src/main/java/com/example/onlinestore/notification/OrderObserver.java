// src/main/java/com/example/onlinestore/notification/OrderObserver.java
package com.example.onlinestore.notification;

import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.OrderStatus;

public interface OrderObserver {
    /** Викликається щоразу, коли статус замовлення змінюється чи створюється нове замовлення */
    void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}
