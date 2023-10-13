package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItem {

    private String price = "";
    private String quantity = "";
    private String description = "";
    private String image = "";
}
