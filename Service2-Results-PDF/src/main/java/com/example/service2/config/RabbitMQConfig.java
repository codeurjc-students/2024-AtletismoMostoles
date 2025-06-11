package com.example.service2.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PDF_REQUEST_QUEUE = "pdf.request.queue";
    public static final String PDF_CONFIRMATION_QUEUE = "pdf.confirmation.queue";

    @Bean
    public Queue pdfRequestQueue() {
        return new Queue(PDF_REQUEST_QUEUE, true); // durable
    }

    @Bean
    public Queue pdfConfirmationQueue() {
        return new Queue(PDF_CONFIRMATION_QUEUE, true); // durable
    }
}
