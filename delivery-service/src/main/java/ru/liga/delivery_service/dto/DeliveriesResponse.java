package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DeliveriesResponse {

    private List<DeliveryDto> delivery;
    private int pageIndex = 0;
    private int pageCount = 10;
}
