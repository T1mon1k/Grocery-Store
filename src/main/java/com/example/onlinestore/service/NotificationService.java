package com.example.onlinestore.service;

import com.example.onlinestore.entity.Order;
import com.example.onlinestore.entity.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Відправляє HTML-лист з деталями замовлення
     */
    public void sendOrderConfirmation(Order order) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Підтвердження замовлення №" + order.getId());

            // будуємо HTML тіло
            StringBuilder html = new StringBuilder();
            html.append("<html><body>")
                    .append("<h2>Дякуємо за ваше замовлення №")
                    .append(order.getId())
                    .append("</h2>")
                    .append("<p>Дата та час замовлення: ")
                    .append(
                            order.getCreatedAt()
                                    .format(
                                            java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy 'о' HH:mm")
                                    )
                    )
                    .append("</p>")


                    .append("<h3>Деталі замовлення:</h3>")
                    .append("<table style='border-collapse: collapse; width: 100%;'>")
                    .append("<tr>")
                    .append("<th style='border:1px solid #ddd; padding:8px;'>Товар</th>")
                    .append("<th style='border:1px solid #ddd; padding:8px;'>Кількість</th>")
                    .append("<th style='border:1px solid #ddd; padding:8px;'>Ціна за од.</th>")
                    .append("<th style='border:1px solid #ddd; padding:8px;'>Сума</th>")
                    .append("</tr>");

            for (OrderItem item : order.getItems()) {
                html.append("<tr>")
                        .append("<td style='border:1px solid #ddd; padding:8px;'>")
                        .append(item.getProduct().getName())
                        .append("</td>")
                        .append("<td style='border:1px solid #ddd; padding:8px; text-align:center;'>")
                        .append(item.getQuantity())
                        .append("</td>")
                        .append("<td style='border:1px solid #ddd; padding:8px; text-align:right;'>")
                        .append(item.getUnitPrice()).append(" ₴")
                        .append("</td>")
                        .append("<td style='border:1px solid #ddd; padding:8px; text-align:right;'>")
                        .append(item.getTotalPrice()).append(" ₴")
                        .append("</td>")
                        .append("</tr>");
            }

            html.append("<tr>")
                    .append("<td colspan='3' style='border:1px solid #ddd; padding:8px; text-align:right; font-weight:bold;'>Загалом:</td>")
                    .append("<td style='border:1px solid #ddd; padding:8px; text-align:right; font-weight:bold;'>")
                    .append(order.getTotalPrice()).append(" ₴")
                    .append("</td>")
                    .append("</tr>")
                    .append("</table>")

                    .append("<h3>Дані доставки:</h3>")
                    .append("<p>")
                    .append(order.getCustomerName()).append("<br/>")
                    .append(order.getPhone()).append("<br/>")
                    .append(order.getShippingAddress())
                    .append("</p>")

                    .append("<p>Статус замовлення: <b>")
                    .append(order.getStatus())
                    .append("</b></p>")

                    .append("<hr>")
                    .append("<p>Якщо у вас є питання — відповідайте на цей лист.</p>")
                    .append("<p>З повагою, команда FoodMarket</p>")
                    .append("</body></html>");

            helper.setText(html.toString(), true);
            mailSender.send(mime);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
