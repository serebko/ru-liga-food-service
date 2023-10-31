package ru.liga.order_service.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность запроса на новую позицию в заказе")
public class OrderItemRequest {

    @Schema(description = "Идентификатор позиции в меню")
    private Long restaurantMenuItemId;

    @Schema(description = "Количество единиц")
    private Long quantity;
}
