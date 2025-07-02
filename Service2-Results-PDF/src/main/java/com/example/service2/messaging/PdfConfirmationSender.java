package com.example.service2.messaging;

import com.example.service2.config.RabbitMQConfig;
import com.example.service2.entities.PdfHistory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PdfConfirmationSender {

    private static final Logger logger = LoggerFactory.getLogger(PdfConfirmationSender.class);
    private final RabbitTemplate rabbitTemplate;

    public PdfConfirmationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendConfirmation(Long athleteId, String url) {
        PdfHistory confirmation = new PdfHistory(athleteId, url);
        rabbitTemplate.convertAndSend(RabbitMQConfig.PDF_CONFIRMATION_QUEUE, confirmation);
        logger.info("✅ Confirmation sent to queue B for athlete ID: {}", athleteId);
    }
}
