package com.example.service1.Listeners;

import com.example.service1.DTO.PdfConfirmationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PdfConfirmationListener {

    private final SimpMessagingTemplate messagingTemplate;

    public PdfConfirmationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "pdf.confirmation.queue")
    public void handlePdfConfirmation(PdfConfirmationMessage confirmation) {
        String destino = "/topic/pdf/" + confirmation.getAtletaId();
        messagingTemplate.convertAndSend(destino, confirmation);
        System.out.println("📨 Enviado PDF a WebSocket para atleta " + confirmation.getAtletaId());
    }
}
