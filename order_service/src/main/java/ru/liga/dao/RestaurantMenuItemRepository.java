package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entities.RestaurantMenuItemEntity;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantMenuItemRepository extends JpaRepository<RestaurantMenuItemEntity, Long> {
}
