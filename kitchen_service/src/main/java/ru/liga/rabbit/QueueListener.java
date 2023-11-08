package ru.liga.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.liga.service.KitchenService;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    private final KitchenService kitchenService;

    @RabbitListener(queues = "toKitchen")
    public void processOrderQueue(String order) throws JsonProcessingException {
        log.info("Новый заказ: " + order);
        kitchenService.processNewOrder(order);
    }
}