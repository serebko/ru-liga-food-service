package ru.liga.kitchen_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.kitchen_service.dto.OrderDto;
import ru.liga.kitchen_service.dto.OrdersResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Tag(name = "API для работы с заказами на стороне ресторана")
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private List<OrderDto> activeOrders;
    private List<OrderDto> completeOrders;
    private List<OrderDto> deniedOrders;

    {
        activeOrders = new ArrayList<>();
        activeOrders.add(new OrderDto());
        completeOrders = new ArrayList<>();
        completeOrders.add(new OrderDto());
        deniedOrders = new ArrayList<>();
        deniedOrders.add(new OrderDto());
    }

    @Operation(summary = "Получить список заказов по статусу")
    @GetMapping("/orders")
    public ResponseEntity<OrdersResponse> getOrdersByStatus(@RequestParam String status) {
        String statusToLowerCase = status.toLowerCase(Locale.ROOT);
        switch (statusToLowerCase) {
            case "active" :
                return ResponseEntity.ok(new OrdersResponse().setOrders(activeOrders));
            case "complete" :
                return ResponseEntity.ok(new OrdersResponse().setOrders(completeOrders));
            case "denied" :
                return ResponseEntity.ok(new OrdersResponse().setOrders(deniedOrders));
            default :
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
