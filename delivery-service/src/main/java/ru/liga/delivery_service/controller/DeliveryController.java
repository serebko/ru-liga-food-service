package ru.liga.delivery_service.controller;

import advice.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.delivery_service.dto.DeliveriesResponse;
import ru.liga.delivery_service.dto.OrderActionDto;
import ru.liga.delivery_service.service.DeliveryService;

@Import(GlobalExceptionHandler.class)
@Tag(name = "API для работы с доставками")
@RestController
@RequestMapping("/courier")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Получить доставки по статусу")
    @GetMapping("/deliveries")
    public ResponseEntity<DeliveriesResponse> getDeliveriesByStatus(@RequestParam String status) {
        return deliveryService.getDeliveriesByStatus(status);
    }


    @Operation(summary = "Установить статус доставки по ID")
    @PostMapping("/delivery/{id}")
    public ResponseEntity<String> setDeliveryStatusById(@PathVariable("id") Long id,
                                                        @RequestBody OrderActionDto orderActionDto) {
        return deliveryService.setDeliveryStatusById(id, orderActionDto);
    }
}
