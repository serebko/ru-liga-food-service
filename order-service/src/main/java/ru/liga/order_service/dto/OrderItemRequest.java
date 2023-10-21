package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItemRequest {

    private Long restaurantMenuItemId;
    private Long quantity;
}
