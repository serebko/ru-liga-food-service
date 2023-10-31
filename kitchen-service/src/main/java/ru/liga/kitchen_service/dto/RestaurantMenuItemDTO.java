package ru.liga.kitchen_service.dto;

import entities.RestaurantMenuItemEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RestaurantMenuItemDTO {

    private String name;
    private Double price;
    private String image;
    private String description;

    public static RestaurantMenuItemEntity mapRestaurantMenuItem(RestaurantMenuItemDTO request) {

        return new RestaurantMenuItemEntity()
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setImage(request.getImage())
                .setDescription(request.getDescription());
    }
}
