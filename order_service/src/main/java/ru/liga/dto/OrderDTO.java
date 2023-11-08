package ru.liga.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.liga.entities.OrderEntity;
import ru.liga.entities.OrderItemEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность заказа")
public class OrderDTO {

    @Schema(description = "Идентификатор заказа")
    private Long id;

    @Schema(description = "Имя ресторана")
    private RestaurantNameDTO restaurant;

    @Schema(description = "Дата и время совершения заказа")
    private Timestamp timestamp;

    @Schema(description = "Список позиции в заказе")
    private List<OrderItemDTO> items;

    public static OrderDTO convertOrderToOrderDto(OrderEntity orderEntity, String restaurantName) {

        List<OrderItemEntity> items =  orderEntity.getItems();
        List<OrderItemDTO> itemDTOS = items.stream()
                .map(OrderItemDTO::convertOrderItemToOrderItemDto)
                .collect(Collectors.toList());

        return new OrderDTO()
                .setId(orderEntity.getId())
                .setRestaurant(new RestaurantNameDTO().setName(restaurantName))
                .setItems(itemDTOS)
                .setTimestamp(orderEntity.getTimestamp());
    }
}
