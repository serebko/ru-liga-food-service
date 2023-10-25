package ru.liga.delivery_service.rabbit;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
