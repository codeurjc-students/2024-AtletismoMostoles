package com.example.service1.Listeners;

import com.example.service1.DTO.EventNotificationDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationReceiver {

    private final SimpMessagingTemplate messagingTemplate;

    public EventNotificationReceiver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "event.notification.queue")
    public void receiveNotification(EventNotificationDto notification) {
        System.out.println("Notification:"+ notification);
        System.out.println("[RabbitMQ] Notificación de evento recibida: " + notification.getEventoId());

        // Enviar al topic del frontend
        messagingTemplate.convertAndSend("/topic/eventos", notification);
    }
}
