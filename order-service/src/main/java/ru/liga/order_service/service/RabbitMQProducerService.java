package ru.liga.order_service.service;

public interface RabbitMQProducerService {

    void sendMessage(String message, String routingKey);

}
