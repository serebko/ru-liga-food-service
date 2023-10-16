package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {

    private String id = "";
    private List<MenuItem> menuItems = new ArrayList<>();
    {
        menuItems.add(new MenuItem());
    }
}
