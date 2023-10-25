package ru.liga.kitchen_service.dto;

import entities.OrderItemEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class OrderItemDTO {

    private Long quantity;
    private Long menuItemId;

    public static List<OrderItemDTO> convertOrderItemToOrderItemDTO(List<OrderItemEntity> orderItemEntities) {

        return orderItemEntities.stream()
                .map(item -> new OrderItemDTO()
                        .setMenuItemId(item.getRestaurantMenuItem().getId())
                        .setQuantity(item.getQuantity()))
                .collect(Collectors.toList());
    }
}
