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
        Queue queueToKitchen = new Queue("toKitchen", false);
        Queue queueToOrder = new Queue("toOrder", false);
        Queue queueToDelivery = new Queue("toDelivery", false);
        DirectExchange directExchange = new DirectExchange("directExchange");

        return new Declarables(queueToKitchen, queueToOrder, queueToDelivery,
          directExchange,
          BindingBuilder.bind(queueToKitchen).to(directExchange).with("to.kitchen"),
          BindingBuilder.bind(queueToOrder).to(directExchange).with("to.order"),
          BindingBuilder.bind(queueToDelivery).to(directExchange).with("to.delivery"));
    }
}
