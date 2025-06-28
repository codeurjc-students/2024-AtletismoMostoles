package com.example.service3.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EVENT_NOTIFICATION_QUEUE = "event.notification.queue";

    @Bean
    public Queue eventNotificationQueue() {
        return new Queue(EVENT_NOTIFICATION_QUEUE, true); // durable
    }
}
