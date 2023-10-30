package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MenuItemDTO {

    private Long quantity;
    private Long menuItemId;
}