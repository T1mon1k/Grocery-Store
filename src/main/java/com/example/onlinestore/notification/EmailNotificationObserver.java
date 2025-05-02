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

        // Тема з емоджі
        msg.setSubject("🔔 Замовлення №" + order.getId() + " — " + newStatus.getDisplayName());

        // Будуємо тіло листа
        StringBuilder text = new StringBuilder();
        text.append("👋 Доброго дня, ")
                .append(order.getUser().getUsername())
                .append("!\n\n");

        text.append("Ваше замовлення №")
                .append(order.getId())
                .append(" щойно оновило статус:\n\n");

        text.append("───────────────\n")
                .append("🔄 СТАРИЙ СТАТУС ЗАМОВЛЕННЯ: ")
                .append(oldStatus != null ? oldStatus.getDisplayName() : "—")
                .append("\n")
                .append("✅ НОВИЙ СТАТУС ЗАМОВЛЕННЯ: ")
                .append(newStatus.getDisplayName())
                .append("\n")
                .append("───────────────\n\n");

        text.append("📦 Загальна сума: ")
                .append(order.getTotalPrice())
                .append(" ₴\n");
        text.append("📅 Дата: ")
                .append(order.getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.MINUTES)
                        .toString().replace("T"," "))
                .append("\n\n");

        text.append("<p>Якщо у вас є питання — відповідайте на цей лист.</p>");
        text.append("Дякуємо, що обираєте FoodMarket! ❤️\n");

        msg.setText(text.toString());
        mailSender.send(msg);
    }

}
