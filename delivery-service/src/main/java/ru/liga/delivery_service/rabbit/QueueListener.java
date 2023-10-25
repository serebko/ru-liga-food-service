package ru.liga.delivery_service.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.liga.delivery_service.service.DeliveryService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    @RabbitListener(queues = "postNewDelivery")
    public void processDeliveryQueue(String message) {

        log.info("New delivery!");

        OrderEntity order;
        try {
            order = objectMapper.readValue(message, OrderEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        deliveryService.processNewDelivery(order);
    }
}