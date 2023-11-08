package ru.liga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.service.OrderService;

@RestController
@RequestMapping("/pay")
@Tag(name = "Имитация оплаты заказа")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Оплатить заказ по uid заказа")
    public ResponseEntity<String> imitatePayment(@PathVariable("id") String id) {
        return orderService.imitatePayment(id);
    }
}
