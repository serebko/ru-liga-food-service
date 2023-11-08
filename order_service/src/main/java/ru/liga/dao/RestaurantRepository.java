package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entities.RestaurantEntity;

@Repository
@ComponentScan(basePackages = "entities")
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
