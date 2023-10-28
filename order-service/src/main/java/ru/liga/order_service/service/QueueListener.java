package ru.liga.order_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueListener {

    /*@RabbitListener(queues = "updatesToCustomerQueue")
    public void processOrderQueue(String status) {
        log.info("Status updated to: <<" + status + ">>");
    }*/
}
