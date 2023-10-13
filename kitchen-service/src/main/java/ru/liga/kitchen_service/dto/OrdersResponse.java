package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrdersResponse {

    private List<OrderDto> orders;
    private int page_index = 0;
    private int page_count = 10;

}
