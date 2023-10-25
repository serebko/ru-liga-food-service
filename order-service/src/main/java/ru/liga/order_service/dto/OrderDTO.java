package ru.liga.order_service.dto;


import entities.OrderEntity;
import entities.OrderItemEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class OrderDTO {

    private Long id;
    private RestaurantDTO restaurant;
    private Timestamp timestamp;
    private List<OrderItemDTO> items;

    public static OrderDTO convertOrderToOrderDto(OrderEntity orderEntity, String restaurantName) {

        List<OrderItemEntity> items =  orderEntity.getItems();
        List<OrderItemDTO> itemDTOS = items.stream()
                .map(OrderItemDTO::convertOrderItemToOrderItemDto)
                .collect(Collectors.toList());

        return new OrderDTO()
                .setId(orderEntity.getId())
                .setRestaurant(new RestaurantDTO().setName(restaurantName))
                .setItems(itemDTOS)
                .setTimestamp(orderEntity.getTimestamp());
    }
}
