package ru.liga.delivery_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeliveryDto {

    private Long orderId;
    private RestaurantDto restaurant;
    private CustomerDto customer;
    private String payment;
    @JsonIgnore
    private OrderActionDto orderAction;
}
