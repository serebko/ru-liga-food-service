package ru.liga.kitchen_service.service;

import entities.Order;
import entities.OrderItem;
import entities.Restaurant;
import entities.RestaurantMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import repositories.OrderItemRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.kitchen_service.dto.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@ComponentScan(basePackages = "repositories")
public class KitchenService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public KitchenService(RestaurantRepository restaurantRepository,
                          RestaurantMenuItemRepository restaurantMenuItemRepository,
                          OrderItemRepository orderItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    private List<MenuItemDto> transformOrderDtoToMenuItemDto(List<OrderItem> orderItems) {
        List<MenuItemDto> menuItemDtos = new ArrayList<>();
        for (OrderItem item : orderItems) {
            MenuItemDto dto = new MenuItemDto()
                    .setMenuItemId(item.getRestaurantMenuItem().getId())
                    .setQuantity(item.getQuantity());
            menuItemDtos.add(dto);
        }
        return menuItemDtos;
    }
    private List<OrderDto> transformOrderToOrderDto(List<Order> orders) {
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = order.getItems();

            OrderDto dto = new OrderDto()
                    .setId(order.getId())
                    .setOrderItems(transformOrderDtoToMenuItemDto(items));
            orderDtos.add(dto);
        }
        return orderDtos;
    }
    private OrdersResponse transformOrderToOrdersResponse(List<Order> orders) {
        OrdersResponse response = new OrdersResponse();
        if (!orders.isEmpty()) {
            return response.setOrders(transformOrderToOrderDto(orders)).setPageCount(10);
        }
        return response.setOrders(Collections.emptyList());
    }

    public ResponseEntity<OrdersResponse> getOrdersByStatus(String status) {
        //Пока неизвестно как понимать какой ресторан делает запрос, поэтому допустим, что это ресторан с id=4
        Restaurant restaurant = restaurantRepository.findRestaurantById(4L);
        List<Order> orderList = restaurant.getOrders();
        List<Order> resultOrderList = new ArrayList<>();
        for (Order order : orderList) {
            if (status.equalsIgnoreCase(order.getStatus().toString()))
                resultOrderList.add(order);
            else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(transformOrderToOrdersResponse(resultOrderList));
    }

    public ResponseEntity<ResponseOnCreation> postNewRestaurantMenuItem(RestaurantMenuItemRequest request) {

        ResponseOnCreation response = new ResponseOnCreation();
        //Пока неизвестно как понимать какой ресторан делает запрос, поэтому допустим, что это ресторан с id=4
        Restaurant restaurant = restaurantRepository.findRestaurantById(4L);

        RestaurantMenuItem item = new RestaurantMenuItem()
                .setRestaurant(restaurant)
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setImage(request.getImage())
                .setDescription(request.getDescription());

        restaurant.getRestaurantMenuItems().add(item);

        RestaurantMenuItem savedItem = restaurantMenuItemRepository.save(item);

        if (!restaurantMenuItemRepository.existsById(savedItem.getId()))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response.setMenuItemId(savedItem.getId()).setName(savedItem.getName()));
    }

    public ResponseEntity<String> deleteRestaurantMenuItemById(Long id) {
        if (id <= 0) throw new IllegalArgumentException();

        if (!restaurantMenuItemRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        RestaurantMenuItem menuItem = restaurantMenuItemRepository.findRestaurantMenuItemById(id);
        //проверяем использует ли хоть один order_item данный menu_item, если да, то удаляем соответствующий order_item
        if (orderItemRepository.findOrderItemByRestaurantMenuItemId(menuItem.getId()) != null) {
            OrderItem orderItem = orderItemRepository.findOrderItemByRestaurantMenuItemId(menuItem.getId());
            orderItemRepository.deleteById(orderItem.getId());
        }

        restaurantMenuItemRepository.deleteById(id);

        if (restaurantMenuItemRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<ChangePriceResponse> changePriceInMenuItem(Long id, ChangePriceRequest request) {

        if (id <= 0) throw new IllegalArgumentException();

        RestaurantMenuItem menuItem = restaurantMenuItemRepository.findRestaurantMenuItemById(id);
        Double newPrice = request.getNewPrice();

        if (menuItem == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if (newPrice <= 0) throw new IllegalArgumentException();

        menuItem.setPrice(newPrice);
        restaurantMenuItemRepository.save(menuItem);

        ChangePriceResponse response = new ChangePriceResponse()
                .setItemId(menuItem.getId())
                .setNewPrice(newPrice);

        return ResponseEntity.ok(response);
    }

}
