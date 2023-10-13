package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Restaurant {

    private String address = "";
    private String distance = "";
}
