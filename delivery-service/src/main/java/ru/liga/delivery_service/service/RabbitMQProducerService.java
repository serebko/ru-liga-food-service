package ru.liga.delivery_service.service;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
