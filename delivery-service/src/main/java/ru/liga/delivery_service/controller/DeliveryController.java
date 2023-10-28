package ru.liga.delivery_service.controller;

import advice.GlobalExceptionHandler;
import entities.CourierEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.delivery_service.dto.CourierDTO;
import ru.liga.delivery_service.dto.OrderActionDTO;
import ru.liga.delivery_service.service.DeliveryService;

import java.util.List;
import java.util.Map;

@Import(GlobalExceptionHandler.class)
@Tag(name = "API для работы с доставками")
@RestController
@RequestMapping("/courier")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Получить доставки по статусу")
    @GetMapping("/deliveries")
    public ResponseEntity<Map<String, Object>> getDeliveriesByStatus(@RequestParam String status,
                                                                     @RequestParam(defaultValue = "0") int pageIndex,
                                                                     @RequestParam(defaultValue = "10") int pageSize) {
        return deliveryService.getDeliveriesByStatus(status, pageIndex, pageSize);
    }

    @Operation(summary = "Установить статус доставки по ID")
    @PostMapping("/delivery/{id}")
    public ResponseEntity<String> setDeliveryStatusById(@PathVariable("id") Long id,
                                                        @RequestBody OrderActionDTO orderActionDto) {
        return deliveryService.setDeliveryStatusByOrderId(id, orderActionDto);
    }




    @PostMapping("/post")
    public ResponseEntity<CourierEntity> postNewCourier(@RequestBody CourierDTO dto) {
        return deliveryService.postNewCourier(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourierById(@PathVariable("id") Long id) {
        return deliveryService.deleteCourierById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourierEntity>> getAllCouriers() {
        return deliveryService.getAllCouriers();
    }

    @PostMapping("/set")
    public ResponseEntity<String> setCourierForOrder(@RequestParam Long orderId,
                                                     @RequestParam Long courierId) {
        return deliveryService.setCourierForOrder(orderId, courierId);
    }
}
