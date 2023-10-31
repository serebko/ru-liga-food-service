package ru.liga.kitchen_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.liga.kitchen_service.dto.OrderActionDTO;

@FeignClient(name = "delivery-service", url = "${feign.services.delivery-service.url}")
public interface DeliveryServiceClient {

    @PostMapping("/courier/delivery/{id}")
    ResponseEntity<String> setOrderStatusById(@PathVariable("id") Long id,
                                              @RequestBody OrderActionDTO orderAction);
}
