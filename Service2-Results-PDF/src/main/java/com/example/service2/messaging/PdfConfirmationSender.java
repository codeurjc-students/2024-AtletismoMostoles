package com.example.service2.messaging;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.entities.PdfRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PdfConfirmationSender {

    private final RabbitTemplate rabbitTemplate;

    public PdfConfirmationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a confirmation message with the generated PDF details
     * to the confirmation queue (cola B).
     *
     * @param pdfRequest the PDF request entity that was processed
     */
    public void sendConfirmation(PdfRequest pdfRequest) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PDF_CONFIRMATION_QUEUE, pdfRequest);
        System.out.println("Confirmation sent to queue B for PDF request: " + pdfRequest.getRequestId());
    }
}
