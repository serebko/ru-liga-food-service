package ru.liga.order_service.service;

import entities.CustomerEntity;
import entities.OrderEntity;
import entities.RestaurantEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repositories.CustomerRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testPostNewOrder() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest(/* provide required data */);
        RestaurantEntity restaurantEntity = new RestaurantEntity(/* provide required data */);
        CustomerEntity customerEntity = new CustomerEntity(/* provide required data */);
        OrderEntity savedOrderEntity = new OrderEntity(/* provide required data */);

        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurantEntity));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customerEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrderEntity);

        // Act
        ResponseEntity<OrderResponse> responseEntity = orderService.postNewOrder(orderRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        // Add more assertions based on your expected response

        verify(restaurantRepository, times(1)).findById(anyLong());
        verify(customerRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        // Add more verifications as needed
    }
}
