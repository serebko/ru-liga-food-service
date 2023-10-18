package ru.liga.delivery_service.controller;

import advice.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.delivery_service.dto.DeliveriesResponse;
import ru.liga.delivery_service.dto.DeliveryDto;
import ru.liga.delivery_service.dto.OrderAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Import(GlobalExceptionHandler.class)
@Tag(name = "API для работы с доставками")
@RestController
@RequestMapping("/courier")
public class DeliveryController {

    private List<DeliveryDto> deliveries;
    private List<DeliveryDto> activeDeliveries;
    private List<DeliveryDto> completeDeliveries;
    {
        deliveries = new ArrayList<>();
        deliveries.add(new DeliveryDto().setOrderId("1"));
        activeDeliveries = new ArrayList<>();
        activeDeliveries.add(new DeliveryDto());
        completeDeliveries = new ArrayList<>();
        completeDeliveries.add(new DeliveryDto());
    }

    @Operation(summary = "Получить доставки по статусу")
    @GetMapping("/deliveries")
    public ResponseEntity<DeliveriesResponse> getDeliveriesByStatus(@RequestParam String status) {

        String statusToLowerCase = status.toLowerCase(Locale.ROOT);
        switch (statusToLowerCase) {
            case "active" :
                return ResponseEntity.ok(new DeliveriesResponse().setDelivery(activeDeliveries));
            case "complete" :
                return ResponseEntity.ok(new DeliveriesResponse().setDelivery(completeDeliveries));
            default :
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Установить статус доставки по ID")
    @PostMapping("/delivery/{id}")
    public ResponseEntity<String> setDeliveryStatusById(@PathVariable("id") String stringId,
                                                        @RequestBody OrderAction orderAction) {
        long id = Long.parseLong(stringId);
        if (id <= 0 || !"active".equalsIgnoreCase(orderAction.getOrderAction())
                && !"complete".equalsIgnoreCase(orderAction.getOrderAction())) throw new IllegalArgumentException();

        for (DeliveryDto dto : deliveries) {
            if (stringId.equals(dto.getOrderId())) {
                dto.setOrderAction(orderAction);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
