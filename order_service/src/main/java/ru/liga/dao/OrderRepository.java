package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entities.OrderEntity;

import java.util.Optional;

@Repository
@ComponentScan
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findOrderEntityByUid(String uid);
}
