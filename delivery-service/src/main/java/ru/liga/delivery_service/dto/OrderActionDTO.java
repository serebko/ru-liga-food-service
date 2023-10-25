package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderActionDTO {

    private String orderAction;
}
