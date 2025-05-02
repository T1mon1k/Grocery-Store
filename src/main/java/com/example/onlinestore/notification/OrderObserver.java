package com.example.onlinestore.notification;

import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.OrderStatus;

public interface OrderObserver {
    void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
}
