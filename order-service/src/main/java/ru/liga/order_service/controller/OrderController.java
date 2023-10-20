package ru.liga.order_service.controller;
import advice.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.order_service.dto.*;
import ru.liga.order_service.service.OrderHelper;

@Import(GlobalExceptionHandler.class)
@ComponentScan
@Tag(name = "API для работы с заказами пользователя")
@RestController
@RequestMapping("/customer")
public class OrderController {
    private final OrderHelper orderHelper;
    @Autowired
    public OrderController(OrderHelper orderHelper) {
        this.orderHelper = orderHelper;
    }

    @Operation(summary = "Получить все заказы")
    @GetMapping("/orders")
    public ResponseEntity<OrdersResponse> getOrders() {
        return orderHelper.getOrders();
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") Long id) {
        return orderHelper.getOrderById(id);
    }

    @Operation(summary = "Создать новый заказ")
    @PostMapping("/order")
    public ResponseEntity<ResponseOnCreation> createOrder(@RequestBody OrderRequest order) {
        return orderHelper.postNewOrder(order);
    }

    @Operation(summary = "Удалить заказ по ID")
    @DeleteMapping("/order/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id) {
        return orderHelper.deleteOrderById(id);
    }
}
