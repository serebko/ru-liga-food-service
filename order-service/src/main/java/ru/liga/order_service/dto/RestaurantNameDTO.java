package ru.liga.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность имени ресторана")
public class RestaurantNameDTO {

    @Schema(description = "Имя ресторана")
    private String name;
}
