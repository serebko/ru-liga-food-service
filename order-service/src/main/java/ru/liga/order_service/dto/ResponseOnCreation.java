package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseOnCreation {
    private String id = "";
    private String secret_payment_url = "";
    private String estimated_time_of_arrival = "";
}
