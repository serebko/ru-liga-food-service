package ru.liga.order_service.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entities.*;

@Repository
@ComponentScan(basePackages = "entities")
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    OrderItem findOrderItemById(Long id);

}
