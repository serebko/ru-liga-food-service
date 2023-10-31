package ru.liga.order_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность ответа на создание нового заказа")
public class OrderResponse {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Секрет оплаты")
    private String secretPaymentUrl;

    @Schema(description = "Рассчётное время прибытия")
    private String estimatedTimeOfArrival;
}
