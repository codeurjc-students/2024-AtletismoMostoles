package com.example.service2.listeners;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.services.PdfService;
import com.example.service2.dto.PdfRequestMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PdfRequestListener {

    private final PdfService pdfService;

    public PdfRequestListener(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @RabbitListener(queues = RabbitMQConfig.PDF_REQUEST_QUEUE)
    public void handlePdfRequest(@Payload PdfRequestMessage message) {
        if (message.getAtletaId() != null) {
            pdfService.generarPdfParaAtleta(message.getAtletaId());
        } else if (message.getEventoId() != null) {
            pdfService.generarPdfParaEvento(message.getEventoId());
        } else {
            System.err.println("Mensaje inválido: debe contener atletaId o eventoId.");
        }
    }
}
