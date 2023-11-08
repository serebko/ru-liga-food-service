package ru.liga.notification_service.dto;

import lombok.Data;

@Data
public class Message {
    private final OrderStatus status;
    private final String orderUid;
    private final String orderItems;
    private final String courierUid;
}
