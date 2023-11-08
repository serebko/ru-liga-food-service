package ru.liga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.dto.Message;
import ru.liga.dto.OrderStatus;
import ru.liga.service.KitchenService;

@Tag(name = "Приложение на стороне ресторана", description = "Управление статусами заказов")
@RestController
@RequestMapping("/kitchen")
@Slf4j
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Operation(summary = "Принять заказ")
    @PostMapping("/{uid}/accept")
    public ResponseEntity<String> acceptOrder(@PathVariable("uid") String uid) {
        String result = kitchenService.sendMessageOfStatusUpdate(
          new Message(null, uid, null, null),
          OrderStatus.KITCHEN_ACCEPTED);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Отклонить заказ")
    @PostMapping("/{uid}/decline")
    public ResponseEntity<String> declineOrder(@PathVariable("uid") String uid) {
        String result = kitchenService.sendMessageOfStatusUpdate(
          new Message(null, uid, null, null),
          OrderStatus.KITCHEN_DENIED);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Завершить заказ")
    @PostMapping("/{uid}/ready")
    public ResponseEntity<String> finishOrder(@PathVariable("uid") String uid) {
        String result = kitchenService.sendMessageOfStatusUpdate(
          new Message(null, uid, null, null),
          OrderStatus.DELIVERY_PENDING);

        log.info(result);
        return ResponseEntity.ok(result);
    }

}