package repositories;

import entities.RestaurantMenuItemEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItemEntity, Long> {
}
