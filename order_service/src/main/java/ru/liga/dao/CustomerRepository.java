package ru.liga.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.entities.CustomerEntity;

@Repository
@ComponentScan(basePackages = "entities")
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
