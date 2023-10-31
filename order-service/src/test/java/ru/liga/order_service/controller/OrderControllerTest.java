package ru.liga.order_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;
import ru.liga.order_service.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void testCreateOrder() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest(/* provide required data */);
        OrderResponse orderResponse = new OrderResponse(/* provide required data */);

        when(orderService.postNewOrder(any(OrderRequest.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(orderResponse));

        // Act
        ResponseEntity<OrderResponse> responseEntity = orderController.createOrder(orderRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        // Add more assertions based on your expected response

        verify(orderService, times(1)).postNewOrder(any(OrderRequest.class));
        // Add more verifications as needed
    }
}