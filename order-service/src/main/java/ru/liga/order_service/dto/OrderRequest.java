package ru.liga.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private Long restaurantId;
    private List<MenuItemDto> menuItems;
}
