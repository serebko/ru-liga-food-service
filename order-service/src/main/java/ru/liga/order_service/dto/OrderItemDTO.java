package ru.liga.order_service.dto;

import entities.OrderItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность позиции в заказе")
public class OrderItemDTO {

    @Schema(description = "Цена позиции")
    private Double price;

    @Schema(description = "Количество единиц")
    private Long quantity;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Изображение")
    private String image;

    public static OrderItemDTO convertOrderItemToOrderItemDto(OrderItemEntity item) {

        return new OrderItemDTO()
                .setImage(item.getRestaurantMenuItem().getImage())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setDescription(item.getRestaurantMenuItem().getDescription());
    }
}
