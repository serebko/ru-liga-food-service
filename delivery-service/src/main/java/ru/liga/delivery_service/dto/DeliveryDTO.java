package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeliveryDTO {

    private Long orderId;
    private RestaurantDTO restaurant;
    private CustomerDTO customer;
    private String payment;
}
