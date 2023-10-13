package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {

    private Long id = 1L;
    private Restaurant restaurant = new Restaurant();
    private String timestamp = "";
    private List<OrderItem> items = new ArrayList<>();
    {
        items.add(new OrderItem());
    }
}
