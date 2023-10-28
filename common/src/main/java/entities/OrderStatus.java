package entities;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum OrderStatus {
    @Enumerated(EnumType.STRING)
    CUSTOMER_CREATED,
    @Enumerated(EnumType.STRING)
    CUSTOMER_PAID,
    @Enumerated(EnumType.STRING)
    CUSTOMER_CANCELLED,
    @Enumerated(EnumType.STRING)
    KITCHEN_ACCEPTED,
    @Enumerated(EnumType.STRING)
    KITCHEN_PREPARING,
    @Enumerated(EnumType.STRING)
    KITCHEN_DENIED,
    @Enumerated(EnumType.STRING)
    KITCHEN_REFUNDED,
    @Enumerated(EnumType.STRING)
    DELIVERY_PENDING,
    @Enumerated(EnumType.STRING)
    DELIVERY_PICKING,
    @Enumerated(EnumType.STRING)
    DELIVERY_DELIVERING,
    @Enumerated(EnumType.STRING)
    DELIVERY_COMPLETE,
    @Enumerated(EnumType.STRING)
    DELIVERY_DENIED,
    @Enumerated(EnumType.STRING)
    DELIVERY_REFUNDED
}
