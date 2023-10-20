package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MenuItemDto {

    private Long quantity;
    private Long menuItemId;
}
