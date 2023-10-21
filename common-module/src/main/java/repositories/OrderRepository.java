package repositories;

import entities.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
@ComponentScan(basePackages = "entities")
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional
    Order findOrderByStatus(String status);
    @Transactional
    Order findOrderById(Long id);
    @Transactional
    void deleteOrderById(Long id);
}
