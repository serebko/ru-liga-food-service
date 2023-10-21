package repositories;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entities.*;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ComponentScan(basePackages = "entities")
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Transactional
    OrderItem findOrderItemByRestaurantMenuItemId(Long itemId);

    @Transactional
    void deleteOrderItemById(Long id);
}
