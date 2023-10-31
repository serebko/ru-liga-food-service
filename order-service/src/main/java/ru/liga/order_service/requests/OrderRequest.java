package ru.liga.order_service.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.liga.order_service.dto.MenuItemDTO;

import java.util.List;

@Data
@Schema(description = "Сущность запроса на новый заказ")
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @Schema(description = "Идентификатор ресторана")
    private Long restaurantId;

    @Schema(description = "Список позиций из меню ресторана")
    private List<MenuItemDTO> menuItems;
}
