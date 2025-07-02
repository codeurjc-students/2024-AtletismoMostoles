package com.example.service2.listeners;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.dto.PdfGenerationRequest;
import com.example.service2.services.PdfServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PdfRequestListener {

    private final PdfServiceImpl pdfService;

    public PdfRequestListener(PdfServiceImpl pdfService) {
        this.pdfService = pdfService;
    }

    @RabbitListener(queues = RabbitMQConfig.PDF_REQUEST_QUEUE)
    public void handlePdfRequest(@Payload PdfGenerationRequest message) {
        if (message.getAthleteId() != null) {
            pdfService.generarPdfParaAtleta(message.getAthleteId());
        } else {
            System.err.println("Mensaje inválido: athleteId es obligatorio.");
        }
    }
}
