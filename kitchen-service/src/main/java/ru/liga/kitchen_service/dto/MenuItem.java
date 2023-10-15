package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MenuItem {

    private String quantity = "";
    private String menuItemId = "";
}
