package ru.liga.order_service.dto;

import entities.OrderEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import statuses.KitchenStatus;

import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность ресторана")
public class RestaurantDTO {

    @Schema(description = "Имя")
    private String name;

    @Schema(description = "Адрес")
    private String address;

    @Schema(description = "Статус")
    private KitchenStatus status;

    @Schema(description = "Список заказов")
    private List<OrderEntity> orders;

    @Schema(description = "Список позиций в меню")
    private List<RestaurantMenuItemEntity> menuItems;

    public static RestaurantDTO mapRestaurantEntityToDTO(RestaurantEntity restaurant) {
        return new RestaurantDTO()
                .setName(restaurant.getName())
                .setAddress(restaurant.getAddress())
                .setStatus(restaurant.getStatus())
                .setOrders(restaurant.getOrders())
                .setMenuItems(restaurant.getRestaurantMenuItems());
    }
}
