package ru.liga.order_service.controller;
import advice.GlobalExceptionHandler;
import dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import ru.liga.order_service.dto.RestaurantDTO;
import ru.liga.order_service.requests.OrderItemRequest;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;
import ru.liga.order_service.service.OrderService;

import javax.validation.constraints.Min;

@Import(GlobalExceptionHandler.class)
@ComponentScan
@Tag(name = "Приложение на стороне заказчика", description = "Управление заказами")
@RestController
@RequestMapping("/customer")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Получить все заказы", description = "Возвращает все заказы пользователя постранично")
    @GetMapping("/orders")
    public ResponseEntity<ResponseDTO<OrderDTO>> getOrders(@RequestParam(defaultValue = "0")
                                                               @Parameter(description = "Индекс показываемой страницы") int pageIndex,
                                                           @RequestParam(defaultValue = "10")
                                                           @Parameter(description = "Размер страницы") int pageSize) {
        return orderService.getOrders(pageIndex, pageSize);
    }

    @Operation(summary = "Получить заказ по ID", description = "Возвращает заказ по конкретному идентификатору")
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") @Min(0)
                                                     @Parameter(description = "Идентификатор заказа", required = true)
                                                     Long id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "Создать новый заказ", description = "Создаёт новый заказ от пользователя")
    @PostMapping("/order")
    public ResponseEntity<OrderResponse> postNewOrder(@RequestBody
                                                         @Parameter(description = "Данные нового заказа",
                                                                 required = true) OrderRequest order) {
        return orderService.postNewOrder(order);
    }

    @Operation(summary = "Удалить заказ по ID", description = "Удаляет заказ по конкретному идентификатору")
    @DeleteMapping("/order/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable("id") @Min(0)
                                                      @Parameter(description = "Идентификатор заказа", required = true)
                                                      Long id) {
        return orderService.deleteOrderById(id);
    }

    @Operation(summary = "Добавить позицию в заказ по ID заказа", description = "Добавляет новую позицию в существующий заказ")
    @PostMapping("/order/{id}/item")
    public ResponseEntity<String> createNewOrderItem(@PathVariable("id") @Min(0)
                                                         @Parameter(description = "Идентификатор заказа", required = true)
                                                         Long id,
                                                     @RequestBody
                                                     @Parameter(description = "Данные новой позиции", required = true)
                                                     OrderItemRequest request) {
        return orderService.createNewOrderItem(id, request);
    }

    @Operation(summary = "Удалить позицию из заказа по ID", description = "Удаляет позицию из заказа по идентификатору позиции")
    @DeleteMapping("/order/item/{id}")
    public ResponseEntity<String> deleteOrderItemById(@PathVariable("id") @Min(0)
                                                          @Parameter(description = "Идентификатор позиции заказа", required = true)
                                                          Long id) {
        return orderService.deleteOrderItemById(id);
    }

    @Operation(summary = "Получить ресторан по ID. *Тест MyBatis*",
            description = "Возвращает сущность ресторана по его идентификатору.\nМетод для теста работы MyBatis")
    @GetMapping("/batis/restaurant/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable("id") @Min(0)
                                                               @Parameter(description = "Идентификатор ресторана",
                                                                       required = true) Long id) {
        return orderService.getRestaurantByIdBatis(id);
    }

    @Operation(summary = "Получить ресторан по имени. *Тест MyBatis*",
            description = "Возвращает сущность ресторана по его имени.\nМетод для теста работы MyBatis")
    @GetMapping("/batis/restaurant")
    public ResponseEntity<RestaurantDTO> getRestaurantByName(@RequestParam
                                                                 @Parameter(description = "Имя ресторана",
                                                                         required = true) String name) {
        return orderService.getRestaurantByNameBatis(name);
    }
}



