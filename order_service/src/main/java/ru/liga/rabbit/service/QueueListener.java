package ru.liga.rabbit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.liga.service.OrderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final OrderService orderService;

    @RabbitListener(queues = {"toOrder"})
    public void processStatusUpdatesQueue(String statusUpdate) {
        orderService.processStatusUpdate(statusUpdate);
        log.info(statusUpdate);
    }
}
