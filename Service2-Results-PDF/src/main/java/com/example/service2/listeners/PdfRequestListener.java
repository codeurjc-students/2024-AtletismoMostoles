package com.example.service2.listeners;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.dto.PdfRequestDto;
import com.example.service2.services.PdfServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PdfRequestListener {

    private final PdfServiceImpl pdfService;

    public PdfRequestListener(PdfServiceImpl pdfService) {
        this.pdfService = pdfService;
    }

    @RabbitListener(queues = RabbitMQConfig.PDF_REQUEST_QUEUE)
    public void handlePdfRequest(PdfRequestDto request) {
        String athleteId = request.getAthleteId();
        if (athleteId != null && !athleteId.isBlank()) {
            System.out.println("📄 Recibida solicitud de PDF para atleta: " + athleteId);
            pdfService.generatePdfForAthlete(athleteId);
        } else {
            System.err.println("❌ Mensaje inválido: atletaId ausente o vacío");
        }
    }
}
