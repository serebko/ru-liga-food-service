package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrdersResponse {

    private List<OrderDto> orders;
    private int pageIndex = 0;
    private int pageCount = 0;
}
