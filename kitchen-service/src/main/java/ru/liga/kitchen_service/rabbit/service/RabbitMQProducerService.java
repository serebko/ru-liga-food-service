package ru.liga.kitchen_service.rabbit.service;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
