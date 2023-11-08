package ru.liga.notification_service.rabbit.service;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);

}
