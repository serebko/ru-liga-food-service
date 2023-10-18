package service;

public enum OrderStatus {
    CUSTOMER_CREATED("created"),
    CUSTOMER_PAID("paid"),
    CUSTOMER_CANCELLED("cancelled"),
    KITCHEN_ACCEPTED("accepted"),
    KITCHEN_PREPARING("preparing"),
    KITCHEN_DENIED("denied"),
    KITCHEN_REFUNDED("refunded"),
    DELIVERY_PENDING("pending"),
    DELIVERY_PICKING("picking"),
    DELIVERY_DELIVERING("delivering"),
    DELIVERY_COMPLETE("complete"),
    DELIVERY_DENIED("denied"),
    DELIVERY_REFUNDED("refunded");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
