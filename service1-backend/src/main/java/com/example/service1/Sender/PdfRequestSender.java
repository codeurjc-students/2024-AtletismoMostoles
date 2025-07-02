package com.example.service1.Sender;

import com.example.service1.DTO.PdfGenerationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PdfRequestSender {

    private final RabbitTemplate rabbitTemplate;

    public PdfRequestSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRequest(PdfGenerationRequest request) {
        rabbitTemplate.convertAndSend("pdf.request.queue", request);
    }
}
