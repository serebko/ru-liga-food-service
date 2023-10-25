package repositories;

import entities.RestaurantEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
