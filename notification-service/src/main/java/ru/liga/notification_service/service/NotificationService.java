package ru.liga.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final QueueListener queueListener;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationService(RabbitMQProducerServiceImpl rabbitMQProducerService,
                               QueueListener queueListener,
                               ObjectMapper objectMapper) {
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.queueListener = queueListener;
        this.objectMapper = objectMapper;
    }

    public void sendOr(){}
}
