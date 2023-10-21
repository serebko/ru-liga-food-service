package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangePriceResponse {

    private Long itemId;
    private Double newPrice;
}
