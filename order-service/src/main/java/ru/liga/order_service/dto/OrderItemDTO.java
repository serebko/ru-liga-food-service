package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItemDTO {

    private Double price;
    private Long quantity;
    private String description;
    private String image;
}
