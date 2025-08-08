package com.example.service1.Listeners;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PdfConfirmationListener {

    private final SimpMessagingTemplate messagingTemplate;

    public PdfConfirmationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queuesToDeclare = @Queue("pdf.confirmation.queue"))
    public void handleConfirmation(Map<String, String> message) {
        String athleteId = message.get("athleteId");
        String url = message.get("url");

        messagingTemplate.convertAndSend("/topic/pdf/" + athleteId, url);
    }
}
