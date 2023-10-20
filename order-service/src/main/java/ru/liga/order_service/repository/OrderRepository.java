package ru.liga.order_service.repository;

import entities.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
@ComponentScan(basePackages = "entities")
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional
    Order findOrderByStatus(String status);
    @Transactional
    Order findOrderById(Long id);
    @Transactional
    @Query("from Restaurant where id = :id")
    Restaurant getRestaurantById(@Param("id") Long id);

    @Transactional
    @Query("from Customer where id = :id")
    Customer getCustomerById(@Param("id") Long id);

    @Transactional
    @Query("from RestaurantMenuItem where id = :id")
    RestaurantMenuItem getRestaurantMenuItemById(@Param("id") Long id);

    @Transactional
    void deleteOrderById(Long id);

}
