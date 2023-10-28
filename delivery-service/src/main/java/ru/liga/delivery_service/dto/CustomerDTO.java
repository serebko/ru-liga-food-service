package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerDTO {

    private String address;
    private Double distance;
}
