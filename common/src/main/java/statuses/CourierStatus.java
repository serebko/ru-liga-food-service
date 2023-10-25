package statuses;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum CourierStatus {

    @Enumerated(EnumType.STRING)
    PENDING,
    @Enumerated(EnumType.STRING)
    PICKING,
    @Enumerated(EnumType.STRING)
    DENIED,
    @Enumerated(EnumType.STRING)
    DELIVERING,
    @Enumerated(EnumType.STRING)
    INACTIVE
}
