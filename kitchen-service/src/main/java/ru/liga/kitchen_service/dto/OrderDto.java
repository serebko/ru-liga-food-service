package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderDto {

    private String id = "";
    private List<MenuItem> menu_items = new ArrayList<>();
    {
        menu_items.add(new MenuItem());
    }
}
