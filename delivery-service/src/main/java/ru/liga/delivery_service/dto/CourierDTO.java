package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CourierDTO {

    private String phone;
    private String status;
    private String coordinates;
}
