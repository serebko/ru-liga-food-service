package ru.liga.delivery_service.controller;

import advice.GlobalExceptionHandler;
import dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.delivery_service.dto.DeliveryDTO;
import ru.liga.delivery_service.dto.OrderActionDTO;
import ru.liga.delivery_service.service.DeliveryService;

@Import(GlobalExceptionHandler.class)
@Tag(name = "Приложение на стороне курьера", description = "Управление доставками")
@RestController
@RequestMapping("/courier")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Получить доставки по статусу")
    @GetMapping("/deliveries")
    public ResponseEntity<ResponseDTO<DeliveryDTO>> getDeliveriesByStatus(@RequestParam String status,
                                                                          @RequestParam(defaultValue = "0") int pageIndex,
                                                                          @RequestParam(defaultValue = "10") int pageSize) {
        return deliveryService.getDeliveriesByStatus(status, pageIndex, pageSize);
    }

    @Operation(summary = "Установить статус доставки по ID заказа")
    @PutMapping("/delivery/{id}")
    public ResponseEntity<String> setDeliveryStatusById(@PathVariable("id") Long id,
                                                        @RequestBody OrderActionDTO orderActionDto) {
        return deliveryService.setDeliveryStatusByOrderId(id, orderActionDto);
    }
}
