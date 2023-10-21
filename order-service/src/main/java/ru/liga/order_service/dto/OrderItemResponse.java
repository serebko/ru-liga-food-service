package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItemResponse {

    private Long newOrderItemId;
    private Double price;
}
