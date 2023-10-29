package repositories;

import entities.OrderEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import statuses.OrderStatus;

@Repository
@ComponentScan(basePackages = "entities")
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findOrderEntitiesByStatus(OrderStatus status, Pageable pageable);
}
