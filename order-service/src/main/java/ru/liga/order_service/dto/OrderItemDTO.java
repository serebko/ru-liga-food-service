package ru.liga.order_service.dto;

import entities.OrderItemEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItemDTO {

    private Double price;
    private Long quantity;
    private String description;
    private String image;

    public static OrderItemDTO convertOrderItemToOrderItemDto(OrderItemEntity item) {

        return new OrderItemDTO()
                .setImage(item.getRestaurantMenuItem().getImage())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setDescription(item.getRestaurantMenuItem().getDescription());
    }
}
