package ru.liga.kitchen_service.rabbit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.liga.kitchen_service.service.KitchenService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final ObjectMapper objectMapper;
    private final KitchenService kitchenService;

    @RabbitListener(queues = "courierResponse", ackMode = "MANUAL")
    public void processDeliveryQueue(String message) {
        log.info("Response from courier: <<" + message + ">>");
        kitchenService.processCourierResponse(message);
    }

    @RabbitListener(queues = "orderToKitchen")
    public void processOrderQueue(String order) {
        log.info("Received order from customer..");
        OrderEntity orderEntity;
        try {
            orderEntity = objectMapper.readValue(order, OrderEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kitchenService.processNewOrder(orderEntity);
        log.info(orderEntity.toString());
    }
}
