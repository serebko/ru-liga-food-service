package ru.liga.kitchen_service.controller;

import advice.GlobalExceptionHandler;
import dto.ResponseDTO;
import entities.RestaurantMenuItemEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.OrderDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import ru.liga.kitchen_service.service.KitchenService;

@Import(GlobalExceptionHandler.class)
@Tag(name = "Приложение на стороне ресторана", description = "Управление заказами и меню")
@RestController
@RequestMapping("/restaurant")
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Operation(summary = "Получить список заказов по статусу")
    @GetMapping("/orders")
    public ResponseEntity<ResponseDTO<OrderDTO>> getOrdersByStatus(@RequestParam String status,
                                                                   @RequestParam(defaultValue = "0") int pageIndex,
                                                                   @RequestParam(defaultValue = "10") int pageSize) {
        return kitchenService.getOrdersByStatus(status, pageIndex, pageSize);
    }

    @Operation(summary = "Создать новую позицию в меню")
    @PostMapping("/item")
    public ResponseEntity<RestaurantMenuItemEntity> postNewRestaurantMenuItem(@RequestBody RestaurantMenuItemDTO request) {
        return kitchenService.postNewRestaurantMenuItem(request);
    }

    @Operation(summary = "Удалить позицию из меню по ID")
    @DeleteMapping("/item/{id}")
    public ResponseEntity<String> deleteRestaurantMenuItemById(@PathVariable("id") Long id){
        return kitchenService.deleteRestaurantMenuItemById(id);
    }

    @Operation(summary = "Изменить цену позиции в меню")
    @PutMapping("/item/{id}")
    public ResponseEntity<RestaurantMenuItemEntity> changePriceInMenuItem(@PathVariable("id") Long id,
                                                                          @RequestBody PriceDTO request) {
        return kitchenService.changePriceInMenuItemById(id, request);
    }

    @Operation(summary = "Изменить статус заказа по его id через feign-клиент, используя метод из delivery-service")
    @PutMapping("/order/{id}")
    public ResponseEntity<String> setOrderStatusById(@PathVariable("id") Long id,
                                                     @RequestBody OrderActionDTO orderAction) {
        return kitchenService.setOrderStatusById(id, orderAction);
    }
}
