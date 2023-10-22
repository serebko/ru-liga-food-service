package ru.liga.order_service.service;

import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.liga.order_service.dto.*;
import repositories.*;
import service.OrderStatus;

import java.sql.Timestamp;
import java.util.*;

@Service
@ComponentScan(basePackages = "repositories")
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        RestaurantRepository restaurantRepository,
                        CustomerRepository customerRepository,
                        RestaurantMenuItemRepository restaurantMenuItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
    }

    private List<OrderItemDto> transformOrderItemToOrderItemDto(List<OrderItem> items) {

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

    private List<OrderDto> transformOrderToOrderDto(List<Order> orders) {

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

    private OrdersResponse transformOrderToOrdersResponse(List<Order> orders) {

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

        List<MenuItemDto> menuItemDtos = orderRequest.getMenuItems();

        Order order = new Order();
        Restaurant restaurant = restaurantRepository.findRestaurantById(restaurantId);
        if (restaurant == null) throw new IllegalArgumentException();
        //Пока неизвестно как понимать кто именно заказывает, поэтому допустим, что заказывает customer с id=4
        Customer customer = customerRepository.findCustomerById(4L);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        order.setStatus(OrderStatus.CUSTOMER_CREATED)
                .setCustomer(customer)
                .setRestaurant(restaurant)
                .setTimestamp(timestamp);
        Order savedOrder = orderRepository.save(order);

        if (!orderRepository.existsById(savedOrder.getId()))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        List<OrderItem> items = new ArrayList<>();
        for (MenuItemDto dto : menuItemDtos) {

            RestaurantMenuItem restaurantMenuItem = restaurantMenuItemRepository.findRestaurantMenuItemById(dto.getMenuItemId());
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
        savedOrder.getCustomer().getOrders().add(savedOrder);
        savedOrder.getRestaurant().getOrders().add(savedOrder);

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
            order.getCustomer().getOrders().remove(order);
            order.getRestaurant().getOrders().remove(order);
            if (order.getCourier() != null)
                order.getCourier().getOrders().remove(order);

            orderRepository.deleteOrderById(id);
            return orderRepository.existsById(id) ?
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() :
                    ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<OrderItemResponse> createNewOrderItem(Long id, OrderItemRequest request) {

        if (id <= 0) throw new IllegalArgumentException();

        Order order = orderRepository.findOrderById(id);
        if (order == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Long restaurantMenuItemId = request.getRestaurantMenuItemId();
        Long quantity = request.getQuantity();

        if (restaurantMenuItemId <= 0 || quantity <= 0) throw new IllegalArgumentException();

        RestaurantMenuItem restaurantMenuItem = restaurantMenuItemRepository.findRestaurantMenuItemById(restaurantMenuItemId);
        OrderItem newOrderItem = new OrderItem()
                .setOrder(order)
                .setQuantity(quantity)
                .setRestaurantMenuItem(restaurantMenuItem)
                .setPrice(restaurantMenuItem.getPrice() * quantity);

        OrderItem savedOrderItem = orderItemRepository.save(newOrderItem);

        order.getItems().add(savedOrderItem);

        OrderItemResponse response = new OrderItemResponse()
                .setNewOrderItemId(savedOrderItem.getId())
                .setPrice(savedOrderItem.getPrice());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<String> deleteOrderItemById(Long id) {

        if (id <= 0) throw new IllegalArgumentException();

        if (orderItemRepository.existsById(id)) {

            OrderItem item = orderItemRepository.findOrderItemById(id);
            item.getOrder().getItems().remove(item);
            orderItemRepository.deleteOrderItemById(id);

            return orderItemRepository.existsById(id) ?
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() :
                    ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
