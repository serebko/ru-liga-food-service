package ru.liga.kitchen_service.dto;

import entities.OrderEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class OrderDTO {

    private Long id;
    private List<OrderItemDTO> orderItems;

    public static List<OrderDTO> convertOrderToOrderDto(List<OrderEntity> orderEntities) {

        return orderEntities.stream()
                .map(orderEntity -> new OrderDTO()
                        .setId(orderEntity.getId())
                        .setOrderItems(OrderItemDTO.convertOrderItemToOrderItemDTO(orderEntity.getItems())))
                .collect(Collectors.toList());
    }
}
