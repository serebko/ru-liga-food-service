package ru.liga.order_service.controller;
import advice.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.ComponentScan;
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
import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.dto.OrderItemRequest;
import ru.liga.order_service.dto.OrderRequest;
import ru.liga.order_service.dto.ResponseOnCreation;
import ru.liga.order_service.service.OrderService;

import java.util.Map;

@Import(GlobalExceptionHandler.class)
@ComponentScan
@Tag(name = "API для работы с заказами пользователя")
@RestController
@RequestMapping("/customer")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Получить все заказы")
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestParam(defaultValue = "0") int pageIndex,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.getOrders(pageIndex, pageSize);
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "Создать новый заказ")
    @PostMapping("/order")
    public ResponseEntity<ResponseOnCreation> createOrder(@RequestBody OrderRequest order) {
        return orderService.postNewOrder(order);
    }

    @Operation(summary = "Удалить заказ по ID")
    @DeleteMapping("/order/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id) {
        return orderService.deleteOrderById(id);
    }

    @Operation(summary = "Добавить позицию в заказ по ID")
    @PostMapping("/order/{id}/item")
    public ResponseEntity<String> createNewOrderItem(@PathVariable("id") Long id,
                                                     @RequestBody OrderItemRequest request) {
        return orderService.createNewOrderItem(id, request);
    }

    @Operation(summary = "Удалить позицию из заказа по ID")
    @DeleteMapping("/order/item/{id}")
    public ResponseEntity<String> deleteOrderItemById(@PathVariable("id") Long id) {
        return orderService.deleteOrderItemById(id);
    }
}
