// src/main/java/com/example/onlinestore/notification/EmailNotificationObserver.java
package com.example.onlinestore.notification;

import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationObserver implements OrderObserver {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailNotificationObserver(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(order.getUser().getEmail());
        msg.setSubject("Замовлення №" + order.getId() + " — статус: " + newStatus);
        msg.setText(
                "Ваше замовлення #" + order.getId() + " змінило статус:\n"
                        + (oldStatus != null ? oldStatus : "—") + " → " + newStatus
        );
        mailSender.send(msg);
    }
}
