package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {

    private Long id;
    private List<MenuItemDto> orderItems;
}
