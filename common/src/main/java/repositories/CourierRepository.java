package repositories;

import entities.CourierEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = "entities")
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {
}
