package repositories;

import entities.RestaurantMenuItem;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItem, Long> {
    @Transactional
    RestaurantMenuItem findRestaurantMenuItemById(Long id);
}
