package repositories;

import entities.Restaurant;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Transactional
    Restaurant findRestaurantById(Long id);
}
