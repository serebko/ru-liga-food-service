package ru.liga.order_service.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderResponse {

    private Long id;
    private String secretPaymentUrl;
    private String estimatedTimeOfArrival;
}
