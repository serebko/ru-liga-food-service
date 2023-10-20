package ru.liga.order_service.service;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.liga.order_service.dto.*;
import ru.liga.order_service.repository.OrderRepository;

import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderHelper {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderItemDto> transformOrderItemsToOrderItemDto(List<OrderItem> items) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItem item : items) {
            orderItemDtos.add(new OrderItemDto()
                    .setImage(item.getRestaurantMenuItem().getImage())
                    .setPrice(item.getPrice())
                    .setQuantity(item.getQuantity())
                    .setDescription(item.getRestaurantMenuItem().getDescription()));
        }
        return orderItemDtos;
    }

    public List<OrderDto> transformOrdersToOrderDto(List<Order> orders) {
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(new OrderDto()
                    .setId(order.getId())
                    .setRestaurant(new RestaurantDto().setName(order.getRestaurant().getName()))
                    .setItems(transformOrderItemsToOrderItemDto(order.getItems()))
                    .setTimestamp(order.getTimestamp()));
        }
        return orderDtos;
    }

    public OrdersResponse transformOrdersToOrdersResponse(List<Order> orders) {
        OrdersResponse response = new OrdersResponse();

        if (!orders.isEmpty())
            return response.setOrders(transformOrdersToOrderDto(orders)).setPageCount(10);

        return response.setOrders(Collections.emptyList());
    }

    public ResponseEntity<OrdersResponse> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(transformOrdersToOrdersResponse(orders));
    }

    public ResponseEntity<OrderDto> getOrderById(Long id) {
        Order order = orderRepository.findOrderById(id);
        OrderDto orderDto = transformOrdersToOrderDto(Collections.singletonList(order)).get(0);
        return ResponseEntity.ok(orderDto);
    }

    public ResponseEntity<ResponseOnCreation> postNewOrder(OrderRequest orderRequest) {
        Long restaurantId = orderRequest.getRestaurantId();
        List<MenuItemDto> menuItemDtos = orderRequest.getMenuItemDtos();

        Restaurant restaurant = orderRepository.getRestaurantById(restaurantId);
        Customer customer = orderRepository.getCustomerById(3L);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Order order = new Order();

        List<OrderItem> items = new ArrayList<>();
        Long id = 1L;
        for (MenuItemDto dto : menuItemDtos) {
            RestaurantMenuItem restaurantMenuItem = orderRepository.getRestaurantMenuItemById(dto.getMenuItemId());
            Long quantity = dto.getQuantity();
            Double price = restaurantMenuItem.getPrice();
            OrderItem item = new OrderItem()
                    .setOrder(order)
                    .setRestaurantMenuItem(restaurantMenuItem)
                    .setQuantity(quantity)
                    .setId(id)
                    .setPrice(price * quantity);
            items.add(item);
            id++;
        }

        order.setItems(items)
                .setStatus("created")
                .setCustomer(customer)
                .setRestaurant(restaurant)
                .setTimestamp(timestamp);

        orderRepository.save(order);

        ResponseOnCreation response = new ResponseOnCreation().setId(order.getId())
                .setSecretPaymentUrl("url")
                .setEstimatedTimeOfArrival("soon");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<String> deleteOrderById(Long id) {
        orderRepository.deleteOrderById(id);
        return ResponseEntity.ok("Deleted!");
    }
}
