package ru.liga.delivery_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    @RabbitListener(queues = "deliveryQueue")
    public void processDeliveryQueue(String message) {
        log.info("Message received from kitchen-service: " + message);
    }
}