package ru.liga.kitchen_service.controller;

import advice.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.kitchen_service.dto.*;
import ru.liga.kitchen_service.service.KitchenService;

@Import(GlobalExceptionHandler.class)
@Tag(name = "API для работы с заказами на стороне ресторана")
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final KitchenService kitchenService;

    @Autowired
    public RestaurantController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Operation(summary = "Получить список заказов по статусу")
    @GetMapping("/orders")
    public ResponseEntity<OrdersResponse> getOrdersByStatus(@RequestParam String status) {
        return kitchenService.getOrdersByStatus(status);
    }

    @Operation(summary = "Создать новую позицию в меню")
    @PostMapping("/item")
    public ResponseEntity<ResponseOnCreation> postNewRestaurantMenuItem(@RequestBody RestaurantMenuItemRequest request) {
        return kitchenService.postNewRestaurantMenuItem(request);
    }

    @Operation(summary = "Удалить позицию из меню по ID")
    @DeleteMapping("/item/{id}")
    public ResponseEntity<String> deleteRestaurantMenuItemById(@PathVariable("id") Long id){
        return kitchenService.deleteRestaurantMenuItemById(id);
    }

    @Operation(summary = "Изменить цену позиции в меню")
    @PostMapping("/item/{id}")
    public ResponseEntity<ChangePriceResponse> changePriceInMenuItem(@PathVariable("id") Long id,
                                                                     @RequestBody ChangePriceRequest request) {
        return kitchenService.changePriceInMenuItem(id, request);
    }
}
