package ru.liga.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderToPost {

    private String restaurant_id;
    private List<MenuItem> menu_items;
}
