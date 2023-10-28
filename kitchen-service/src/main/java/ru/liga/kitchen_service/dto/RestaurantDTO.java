package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RestaurantDTO {

    private String address;
    private String status;
    private String name;
}
