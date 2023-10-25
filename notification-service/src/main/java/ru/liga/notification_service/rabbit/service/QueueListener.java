package ru.liga.notification_service.rabbit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final RabbitMQProducerServiceImpl rabbitMQProducerService;

    @RabbitListener(queues = {"kitchenStatusUpdate", "deliveryStatusUpdate"})
    public void processUpdateQueues(String statusUpdate) {
        log.info("New status update!");
        rabbitMQProducerService.sendMessage(statusUpdate, "order.status.update");
    }

    @RabbitListener(queues = "postNewOrder")
    public void processOrderQueue(String order) {
        log.info("New order!");
        rabbitMQProducerService.sendMessage(order, "new.order");
    }
}
