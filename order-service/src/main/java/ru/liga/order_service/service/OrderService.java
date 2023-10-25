package ru.liga.order_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.OrderEntity;
import entities.OrderItemEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.dto.MenuItemDTO;
import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.rabbit.service.RabbitMQProducerServiceImpl;
import ru.liga.order_service.requests.OrderItemRequest;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;
import ru.liga.order_service.mapper.RestaurantMapper;
import statuses.OrderStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final RestaurantMapper restaurantMapper;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        RestaurantRepository restaurantRepository,
                        RestaurantMenuItemRepository restaurantMenuItemRepository,
                        RestaurantMapper restaurantMapper,
                        RabbitMQProducerServiceImpl rabbitMQProducerService,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.restaurantMapper = restaurantMapper;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.objectMapper = objectMapper;
    }

    private String tryToSerializeOrderEntityAsString(OrderEntity order) {
        String orderInLine;
        try {
            orderInLine = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return orderInLine;
    }

    private OrderItemEntity mapOrderItem(Long orderId, Long menuItemId, Long quantity) {
        if (quantity <= 0) throw new IllegalArgumentException();

        RestaurantMenuItemEntity restaurantMenuItemEntity = restaurantMenuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

        return new OrderItemEntity()
                .setOrderId(orderId)
                .setQuantity(quantity)
                .setRestaurantMenuItem(restaurantMenuItemEntity)
                .setPrice(restaurantMenuItemEntity.getPrice() * quantity);
    }

    public ResponseEntity<Map<String, Object>> getOrders(int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageRequest);
        List<OrderEntity> orders = orderPage.getContent();

        if (orders.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (OrderEntity order : orders) {
            //TODO: вытаскивать имя ресторана требуется по заданию
            String restaurantName = restaurantRepository.findById(order.getRestaurantId())
                    .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND)).getName();
            OrderDTO dto = OrderDTO.convertOrderToOrderDto(order, restaurantName);
            orderDTOS.add(dto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderDTOS);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderDTO> getOrderById(Long id) {

        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String restaurantName = restaurantRepository.findById(orderEntity.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND)).getName();

        return ResponseEntity.ok().body(OrderDTO.convertOrderToOrderDto(orderEntity, restaurantName));
    }

    private OrderEntity mapOrderEntity(Long customerId, Long restaurantId) {

        return new OrderEntity()
                .setStatus(OrderStatus.CUSTOMER_CREATED)
                .setCustomerId(customerId)
                .setRestaurantId(restaurantId)
                .setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public ResponseEntity<OrderResponse> postNewOrder(OrderRequest orderRequest) {

        Long restaurantId = orderRequest.getRestaurantId();
        if (!restaurantRepository.existsById(orderRequest.getRestaurantId()))
            throw new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND);

        //TODO: когда будет авторизация, поменять customerId на залогиненного
        OrderEntity orderEntity = mapOrderEntity(5L, restaurantId);
        OrderEntity savedOrder = orderRepository.save(orderEntity);

        List<MenuItemDTO> menuItemDTOS = orderRequest.getMenuItems();

        List<OrderItemEntity> orderItems = menuItemDTOS.stream()
                .map(orderItem -> mapOrderItem(savedOrder.getId(), orderItem.getMenuItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        rabbitMQProducerService.sendMessage(tryToSerializeOrderEntityAsString(savedOrder),
                "new.order.notification");


        OrderResponse response = new OrderResponse().setId(savedOrder.getId())
                .setSecretPaymentUrl("url")
                .setEstimatedTimeOfArrival("soon");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<Void> deleteOrderById(Long id) {

        orderRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Void> createNewOrderItem(Long orderId, OrderItemRequest request) {

        Long quantity = request.getQuantity();
        Long restaurantMenuItemId = request.getRestaurantMenuItemId();
        orderItemRepository.save(mapOrderItem(orderId, restaurantMenuItemId, quantity));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Void> deleteOrderItemById(Long id) {

        orderItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Map<String, Object>> getRestaurantByIdBatis(Long id) {

        RestaurantEntity restaurant = restaurantMapper.findById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", restaurant);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getRestaurantByNameBatis(String name) {

        RestaurantEntity restaurant = restaurantMapper.findByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", restaurant);

        return ResponseEntity.ok(response);
    }
}
