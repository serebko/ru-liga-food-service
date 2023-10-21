package ru.liga.order_service.service;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.liga.order_service.dto.*;
import ru.liga.order_service.repository.OrderItemRepository;
import ru.liga.order_service.repository.OrderRepository;

import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItemDto> transformOrderItemToOrderItemDto(List<OrderItem> items) {

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

    public List<OrderDto> transformOrderToOrderDto(List<Order> orders) {

        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(new OrderDto()
                    .setId(order.getId())
                    .setRestaurant(new RestaurantDto().setName(order.getRestaurant().getName()))
                    .setItems(transformOrderItemToOrderItemDto(order.getItems()))
                    .setTimestamp(order.getTimestamp()));
        }
        return orderDtos;
    }

    public OrdersResponse transformOrderToOrdersResponse(List<Order> orders) {

        OrdersResponse response = new OrdersResponse();

        if (!orders.isEmpty())
            return response.setOrders(transformOrderToOrderDto(orders)).setPageCount(10);

        return response.setOrders(Collections.emptyList());
    }

    public ResponseEntity<OrdersResponse> getOrders() {

        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transformOrderToOrdersResponse(orders));

        return ResponseEntity.ok(transformOrderToOrdersResponse(orders));
    }

    public ResponseEntity<OrderDto> getOrderById(Long id) {

        if (orderRepository.existsById(id)) {
            Order order = orderRepository.findOrderById(id);
            OrderDto orderDto = transformOrderToOrderDto(Collections.singletonList(order)).get(0);
            return ResponseEntity.ok(orderDto);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public ResponseEntity<ResponseOnCreation> postNewOrder(OrderRequest orderRequest) {

        Long restaurantId = orderRequest.getRestaurantId();
        if (restaurantId <= 0) throw new IllegalArgumentException();

        List<MenuItemDto> menuItemDtos = orderRequest.getMenuItemDtos();

        Order order = new Order();
        Restaurant restaurant = orderRepository.findRestaurantByIdQuery(restaurantId);
        if (restaurant == null) throw new IllegalArgumentException();
        //Пока неизвестно как понимать кто имено заказывает, поэтому допустим, что заказывает customer с id=3
        Customer customer = orderRepository.findCustomerByIdQuery(3L);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        order.setStatus("created")
                .setCustomer(customer)
                .setRestaurant(restaurant)
                .setTimestamp(timestamp);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        for (MenuItemDto dto : menuItemDtos) {
            RestaurantMenuItem restaurantMenuItem = orderRepository.findRestaurantMenuItemByIdQuery(dto.getMenuItemId());
            if (restaurantMenuItem == null) throw new IllegalArgumentException();

            Long quantity = dto.getQuantity();
            if (quantity <= 0) throw new IllegalArgumentException();
            Double price = restaurantMenuItem.getPrice();

            OrderItem item = new OrderItem()
                    .setOrder(savedOrder)
                    .setRestaurantMenuItem(restaurantMenuItem)
                    .setQuantity(quantity)
                    .setPrice(price * quantity);
            OrderItem savedItem = orderItemRepository.save(item);

            items.add(savedItem);
        }

        savedOrder.setItems(items);
        orderRepository.save(savedOrder);

        ResponseOnCreation response = new ResponseOnCreation().setId(savedOrder.getId())
                .setSecretPaymentUrl("url")
                .setEstimatedTimeOfArrival("soon");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<String> deleteOrderById(Long id) {
        if (id <= 0) throw new IllegalArgumentException();

        if (orderRepository.existsById(id)) {
            Order order = orderRepository.findOrderById(id);
            List<OrderItem> orderItems = order.getItems();
            orderItems.forEach(orderItem -> orderItemRepository.deleteById(orderItem.getId()));

            orderRepository.deleteOrderById(id);
            return orderRepository.existsById(id) ?
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
                    : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
