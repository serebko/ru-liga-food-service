package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {

    private Long id;
    private RestaurantDto restaurant;
    private Timestamp timestamp;
    private List<OrderItemDto> items;
}
