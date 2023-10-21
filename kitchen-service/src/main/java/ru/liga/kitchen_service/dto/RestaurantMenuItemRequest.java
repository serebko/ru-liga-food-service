package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RestaurantMenuItemRequest {

    private String name;
    private Double price;
    private String image;
    private String description;
}
