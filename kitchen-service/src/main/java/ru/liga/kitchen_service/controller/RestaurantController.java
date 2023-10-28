package ru.liga.kitchen_service.controller;

import advice.GlobalExceptionHandler;
import entities.RestaurantEntity;
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
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.RestaurantDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import ru.liga.kitchen_service.service.KitchenService;

import java.util.List;
import java.util.Map;

@Import(GlobalExceptionHandler.class)
@Tag(name = "API для работы с заказами на стороне ресторана")
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final KitchenService kitchenService;

    public RestaurantController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Operation(summary = "Получить список заказов по статусу")
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@RequestParam String status,
                                                                 @RequestParam(defaultValue = "0") int pageIndex,
                                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return kitchenService.getOrdersByStatus(status, pageIndex, pageSize);
    }

    @Operation(summary = "Создать новую позицию в меню")
    @PostMapping("/item")
    public ResponseEntity<String> postNewRestaurantMenuItem(@RequestBody RestaurantMenuItemDTO request) {
        return kitchenService.postNewRestaurantMenuItem(request);
    }

    @Operation(summary = "Удалить позицию из меню по ID")
    @DeleteMapping("/item/{id}")
    public ResponseEntity<String> deleteRestaurantMenuItemById(@PathVariable("id") Long id){
        return kitchenService.deleteRestaurantMenuItemById(id);
    }

    @Operation(summary = "Изменить цену позиции в меню")
    @PostMapping("/item/{id}")
    public ResponseEntity<String> changePriceInMenuItem(@PathVariable("id") Long id,
                                                        @RequestBody PriceDTO request) {
        return kitchenService.changePriceInMenuItemById(id, request);
    }

    @Operation(summary = "Изменить статус заказа через feign-клиент, используя метод из delivery-service")
    @PostMapping("/order/{id}")
    public ResponseEntity<String> setOrderStatusById(@PathVariable("id") Long id,
                                                     @RequestBody OrderActionDTO orderAction) {
        return kitchenService.setOrderStatusById(id, orderAction);
    }





    @PostMapping("/post")
    public ResponseEntity<RestaurantEntity> postNewRestaurant(@RequestBody RestaurantDTO dto) {
        return kitchenService.postNewRestaurant(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRestaurantById(@PathVariable("id") Long id) {
        return kitchenService.deleteRestaurantById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() {
        return kitchenService.getAllRestaurants();
    }
}
