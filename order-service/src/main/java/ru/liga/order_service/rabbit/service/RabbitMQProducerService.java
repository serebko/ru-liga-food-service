package ru.liga.order_service.rabbit.service;

public interface RabbitMQProducerService {

    void sendMessage(String message, String routingKey);

}
