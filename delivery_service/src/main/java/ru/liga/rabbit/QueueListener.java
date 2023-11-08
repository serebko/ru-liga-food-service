package ru.liga.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.liga.service.DeliveryService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "toDelivery")
    public void processDeliveryQueue(String message) throws JsonProcessingException {

        log.info("New delivery!");
        deliveryService.processNewDelivery(message);
    }
}