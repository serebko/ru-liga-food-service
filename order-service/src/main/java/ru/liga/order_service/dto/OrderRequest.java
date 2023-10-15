package ru.liga.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private String restaurantId;
    private List<MenuItem> menuItems;
}
