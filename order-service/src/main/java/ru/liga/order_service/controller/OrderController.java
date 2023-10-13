package ru.liga.order_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.order_service.dto.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "API для работы с заказами пользователя")
@RestController
@RequestMapping("/customer")
public class OrderController {

    private List<OrderDto> orders;

    {
        orders = new ArrayList<>();
        orders.add(new OrderDto());
    }

    @Operation(summary = "Получить все заказы")
    @GetMapping("/orders")
    public ResponseEntity<OrdersResponse> getOrders() {
        //Не понимаю какой на этом маппинге может быть BAD_REQUEST,
        //но в схеме Д/З он указан, поэтому этим статусом отвечаю, если список orders пуст
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrdersResponse().setOrders(orders));
        }
        return ResponseEntity.ok(new OrdersResponse().setOrders(orders).setPage_count(10));
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") String idString) {

        Long id = Long.parseLong(idString);
        if (id <= 0) throw new IllegalArgumentException();

        for (OrderDto orderDto : orders) {
            if (id.equals(orderDto.getId()))
                return ResponseEntity.ok(orderDto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Создать новый заказ")
    @PostMapping("/order")
    public ResponseOnCreation createOrder(@RequestBody OrderToPost order) {
        orders.add(new OrderDto().setId(2L));
        return new ResponseOnCreation();
    }

    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

}
