package com.example.service1.Listeners;

import com.example.service1.DTO.PdfConfirmationMessage;
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
        String atletaId = message.get("atletaId");
        String url = message.get("url");

        // Enviar por WebSocket a: /topic/pdf/A001 (por ejemplo)
        messagingTemplate.convertAndSend("/topic/pdf/" + atletaId, url);
    }
}
