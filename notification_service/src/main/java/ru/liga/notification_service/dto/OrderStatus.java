package ru.liga.notification_service.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OrderStatus {
    CUSTOMER_PAID,
    KITCHEN_DENIED,
    KITCHEN_ACCEPTED,
    DELIVERY_PENDING,
    DELIVERY_PICKING,
    DELIVERY_COMPLETE,
    KITCHEN_REFUNDED;

    static final Map<OrderStatus, List<String>> routKeysByStatus = new HashMap<>();

    static {

        routKeysByStatus.put(CUSTOMER_PAID, List.of("to.kitchen"));
        routKeysByStatus.put(KITCHEN_DENIED, List.of("to.order"));
        routKeysByStatus.put(KITCHEN_ACCEPTED, List.of("to.order"));
        routKeysByStatus.put(DELIVERY_PENDING, List.of("to.order", "to.delivery"));
        routKeysByStatus.put(DELIVERY_PICKING, List.of("to.order"));
        routKeysByStatus.put(DELIVERY_COMPLETE, List.of("to.order"));
        routKeysByStatus.put(KITCHEN_REFUNDED, List.of("to.order"));
    }

    public List<String> getRoutKeys() {
        return routKeysByStatus.get(this);
    }
}
