package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entity.CourierEntity;
import ru.liga.status.CourierStatus;

import java.util.List;

@Repository
@ComponentScan
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {

    List<CourierEntity> findAllByStatus(CourierStatus status);
}
