package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDTO {

    private Long id;
    private RestaurantDTO restaurant;
    private Timestamp timestamp;
    private List<OrderItemDTO> items;
}
