package ru.liga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.dto.Message;
import ru.liga.dto.OrderStatus;
import ru.liga.service.DeliveryService;

import java.util.Collection;

@Tag(name = "Приложение на стороне курьера", description = "Управление доставками")
@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Получить доступные доставки")
    @GetMapping
    public ResponseEntity<Collection<Message>> getAvailableDeliveries() {
        return ResponseEntity.ok(deliveryService.getAvailableDeliveries());
    }

    @Operation(summary = "Принять заказ на доставку")
    @PostMapping("/{uid}/take")
    public ResponseEntity<String> setDeliveryPickingById(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(deliveryService.setDeliveryStatusByOrderId(uid, OrderStatus.DELIVERY_PICKING));
    }

    @Operation(summary = "Завершить доставку заказа")
    @PostMapping("/{uid}/complete")
    public ResponseEntity<String> setDeliveryCompleteById(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(deliveryService.setDeliveryStatusByOrderId(uid, OrderStatus.DELIVERY_COMPLETE));
    }
}
