package ru.liga.order_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseOnCreation {

    private Long id;
    private String secretPaymentUrl;
    private String estimatedTimeOfArrival;
}
