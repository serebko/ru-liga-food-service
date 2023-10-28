package ru.liga.kitchen_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    /*@RabbitListener(queues = "courierResponse")
    public void processDeliveryQueue(String response) {

        log.info("Response from courier: <<" + response + ">>");
    }*/

    /*@RabbitListener(queues = "orderToKitchenQueue")
    public void processOrderQueue(String order) {

        log.info("New order!\n" + order);
    }*/
}
