package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerDTO {

    private String phone;
    private String email;
    private String address;
}
