package com.example.service3.messaging;

import com.example.service3.config.RabbitMQConfig;
import com.example.service3.dto.EventNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public EventNotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(EventNotification notification) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EVENT_NOTIFICATION_QUEUE, notification);
        System.out.println("Evento enviado a cola: " + notification.getEventoId());
    }
}
