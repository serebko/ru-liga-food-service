package repositories;

import entities.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import service.OrderStatus;

import java.util.List;

@Repository
@ComponentScan(basePackages = "entities")
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional
    List<Order> findOrdersByStatus(OrderStatus status);
    @Transactional
    Order findOrderById(Long id);
    @Transactional
    void deleteOrderById(Long id);
}