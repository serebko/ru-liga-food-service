package ru.liga.kitchen_service.service;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
