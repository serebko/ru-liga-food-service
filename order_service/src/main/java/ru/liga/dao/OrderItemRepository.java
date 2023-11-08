package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entities.OrderItemEntity;

import java.util.List;
import java.util.Optional;

@Repository
@ComponentScan(basePackages = "entities")
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    Optional<List<OrderItemEntity>> findAllByRestaurantMenuItemId(Long itemId);
}
