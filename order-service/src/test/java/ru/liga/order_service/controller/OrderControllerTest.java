package ru.liga.order_service.controller;

import dto.ResponseDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;
import ru.liga.order_service.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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
    @SneakyThrows
    public void testCreateOrder_withCorrectArgs_statusCreated() {

        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();

        when(orderService.postNewOrder(any(OrderRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(orderResponse));

        ResponseEntity<OrderResponse> responseEntity = orderController.createOrder(orderRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        verify(orderService, times(1)).postNewOrder(any(OrderRequest.class));
    }

    @Test
    public void testGetOrders_statusOK() {

        ResponseDTO<OrderDTO> responseDTO = new ResponseDTO<>();

        when(orderService.getOrders(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(responseDTO));

        ResponseEntity<ResponseDTO<OrderDTO>> response = orderController.getOrders(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(orderService, times(1)).getOrders(anyInt(), anyInt());
    }

    @Test
    public void postNewOrder_withIncorrectArgs_statusNotFound() {

        OrderRequest orderRequest = new OrderRequest();
        ResponseEntity<OrderResponse> expectedResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(orderService.postNewOrder(any(OrderRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<OrderResponse> actualResponse = orderController.createOrder(orderRequest);

        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());

        verify(orderService, times(1)).postNewOrder(any(OrderRequest.class));
    }
}