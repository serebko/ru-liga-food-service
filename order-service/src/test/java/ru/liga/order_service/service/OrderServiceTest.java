package ru.liga.order_service.service;

import entities.CustomerEntity;
import entities.OrderEntity;
import entities.OrderItemEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repositories.CustomerRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.dto.MenuItemDTO;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testPostNewOrder_withCorrectArgs_statusCreated() {

        List<MenuItemDTO> menuItems = new ArrayList<>();
        menuItems.add(new MenuItemDTO().setMenuItemId(1L).setQuantity(2L));
        OrderRequest orderRequest = new OrderRequest(5L, menuItems);
        Optional<RestaurantMenuItemEntity> menuItem =
                Optional.of(new RestaurantMenuItemEntity().setId(1L).setPrice(155.00));
        List<RestaurantMenuItemEntity> menu = new ArrayList<>();
        menu.add(menuItem.get());
        Optional<RestaurantEntity> restaurant =
                Optional.of(new RestaurantEntity().setId(5L).setRestaurantMenuItems(menu));
        Optional<CustomerEntity> customer = Optional.of(new CustomerEntity().setId(5L));
        OrderEntity order = new OrderEntity().setCustomerId(customer.get().getId())
                .setRestaurantId(restaurant.get().getId()).setId(5L);
        List<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemEntity().setOrderId(order.getId())
                .setPrice(menuItem.get().getPrice()).setQuantity(2L).setId(5L)
                .setRestaurantMenuItem(menuItem.get()));

        when(restaurantRepository.findById(orderRequest.getRestaurantId())).thenReturn(restaurant);
        when(customerRepository.findById(customer.get().getId())).thenReturn(customer);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);
        when(restaurantMenuItemRepository.findById(orderRequest.getMenuItems().get(0).getMenuItemId())).thenReturn(menuItem);
        when(orderItemRepository.saveAll(orderItems)).thenReturn(orderItems);

        ResponseEntity<OrderResponse> responseEntity = orderService.postNewOrder(orderRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        verify(restaurantRepository, times(2)).findById(anyLong());
        verify(customerRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(restaurantMenuItemRepository, times(1)).findById(anyLong());
        verify(orderItemRepository, times(1)).saveAll(anyList());
    }
}
