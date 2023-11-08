package ru.liga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.dto.OrderActionDTO;
import ru.liga.requests.OrderRequest;
import ru.liga.response.ResponseDTO;
import ru.liga.service.OrderService;
import ru.liga.dto.OrderDTO;
import ru.liga.statuses.OrderStatus;

import javax.validation.constraints.Min;

@Tag(name = "Приложение на стороне заказчика", description = "Управление заказами")
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Получить все заказы", description = "Возвращает все заказы пользователя постранично")
    @GetMapping
    public ResponseEntity<ResponseDTO<OrderDTO>> getOrders(@RequestParam(defaultValue = "0")
                                                           @Parameter(description = "Индекс показываемой страницы") int pageIndex,
                                                           @RequestParam(defaultValue = "10")
                                                           @Parameter(description = "Размер страницы") int pageSize) {
        return orderService.getOrders(pageIndex, pageSize);
    }

    @Operation(summary = "Получить заказ по ID", description = "Возвращает заказ по конкретному идентификатору")
    @GetMapping("/{uid}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("uid") @Min(0)
                                                 @Parameter(description = "Идентификатор заказа", required = true)
                                                 String orderId) {
        return orderService.getOrderById(orderId);
    }

    @Operation(summary = "Создать новый заказ", description = "Создаёт новый заказ от пользователя")
    @PostMapping
    public ResponseEntity<String> postNewOrder(@RequestBody
                                                      @Parameter(description = "Данные нового заказа",
                                                              required = true) OrderRequest order) {
        return orderService.postNewOrder(order); //todo сделать авторизацию из таблицы customers
    }

    @PutMapping("/{uid}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("uid") String orderId,
                                                    @RequestBody OrderActionDTO orderAction) {
        OrderStatus newStatus = OrderStatus.valueOf(orderAction.getOrderAction().toUpperCase());
        String response = orderService.updateOrderStatusById(orderId, newStatus);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restCoord/{orderUid}")
    public ResponseEntity<String> getRestaurantCoordinates(@PathVariable("orderUid") String orderId) {
        return ResponseEntity.ok(orderService.getRestaurantCoordinates(orderId));
    }
}
