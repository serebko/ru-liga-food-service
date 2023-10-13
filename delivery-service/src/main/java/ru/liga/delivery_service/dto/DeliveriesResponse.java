package ru.liga.delivery_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DeliveriesResponse {

    private List<DeliveryDto> delivery;
    private int page_index = 0;
    private int page_count = 10;
}
