package ru.liga.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Сущность позиции в меню ресторана")
public class MenuItemDTO {

    @Schema(description = "Количество единиц")
    private Long quantity;

    @Schema(description = "Идентификатор позиции в меню")
    private Long menuItemId;
}
