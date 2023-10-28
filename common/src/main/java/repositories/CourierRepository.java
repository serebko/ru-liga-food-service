package repositories;

import entities.CourierEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entities.OrderStatus;

import java.util.List;

@Repository
@ComponentScan(basePackages = "entities")
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {

    List<CourierEntity> findAllByStatus(OrderStatus status);
}
