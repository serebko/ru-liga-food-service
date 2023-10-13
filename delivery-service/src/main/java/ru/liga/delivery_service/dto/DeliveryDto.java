package ru.liga.delivery_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeliveryDto {

    private String order_id = "";
    private Restaurant restaurant = new Restaurant();
    private Customer customer = new Customer();
    private String payment = "";
    @JsonIgnore
    private OrderAction order_action;
}
