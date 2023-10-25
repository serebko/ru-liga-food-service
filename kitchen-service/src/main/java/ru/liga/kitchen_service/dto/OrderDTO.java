package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDTO {

    private Long id;
    private List<OrderItemDTO> orderItems;
}
