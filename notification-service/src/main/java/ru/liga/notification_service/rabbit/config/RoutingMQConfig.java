package ru.liga.notification_service.rabbit.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingMQConfig {

    @Bean
    public Declarables routeQueuesFromNotificationService() {
        Queue queueDirectFirst = new Queue("orderToKitchen", false);
        Queue queueDirectSecond = new Queue("updatesToCustomer", false);
        DirectExchange directExchange = new DirectExchange("directExchange");

        return new Declarables(queueDirectFirst, queueDirectSecond, directExchange,
                BindingBuilder.bind(queueDirectFirst).to(directExchange).with("new.order"),
                BindingBuilder.bind(queueDirectSecond).to(directExchange).with("order.status.update"));
    }
}
