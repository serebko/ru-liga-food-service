package ru.liga.notification_service.service;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
