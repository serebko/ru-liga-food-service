package ru.liga.delivery_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.delivery_service.dto.DeliveriesResponse;
import ru.liga.delivery_service.dto.DeliveryDto;
import ru.liga.delivery_service.dto.OrderAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Tag(name = "API для работы с доставками")
@RestController
@RequestMapping("/courier")
public class DeliveryController {

    private List<DeliveryDto> deliveries;
    private List<DeliveryDto> activeDeliveries;
    private List<DeliveryDto> completeDeliveries;
    {
        deliveries = new ArrayList<>();
        deliveries.add(new DeliveryDto().setOrder_id("1"));
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
                                                        @RequestBody OrderAction order_action) {
        long id = Long.parseLong(stringId);
        if (id <= 0 || !"active".equalsIgnoreCase(order_action.getOrder_action())
                && !"complete".equalsIgnoreCase(order_action.getOrder_action())) throw new IllegalArgumentException();

        for (DeliveryDto dto : deliveries) {
            if (stringId.equals(dto.getOrder_id())) {
                dto.setOrder_action(order_action);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
