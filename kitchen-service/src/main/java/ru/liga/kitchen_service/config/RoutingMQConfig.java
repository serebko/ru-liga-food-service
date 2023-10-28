package ru.liga.kitchen_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingMQConfig {

    @Bean
    public Declarables routeQueueFromKitchenService() {
        Queue queueDirectFirst = new Queue("postNewDelivery", false);
        Queue queueDirectSecond = new Queue("kitchenStatusUpdate", false);
        DirectExchange directExchange = new DirectExchange("directExchange");

        return new Declarables(queueDirectFirst, queueDirectSecond, directExchange,
                BindingBuilder.bind(queueDirectFirst).to(directExchange).with("new.delivery"),
                BindingBuilder.bind(queueDirectSecond).to(directExchange).with("kitchen.status.update"));
    }
}
