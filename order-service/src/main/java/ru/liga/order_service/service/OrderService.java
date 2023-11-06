package ru.liga.order_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ResponseDTO;
import entities.CustomerEntity;
import entities.OrderEntity;
import entities.OrderItemEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.CustomerRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.dto.MenuItemDTO;
import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.dto.RestaurantDTO;
import ru.liga.order_service.rabbit.service.RabbitMQProducerServiceImpl;
import ru.liga.order_service.requests.OrderItemRequest;
import ru.liga.order_service.requests.OrderRequest;
import ru.liga.order_service.response.OrderResponse;
import ru.liga.order_service.mapper.RestaurantMapper;
import statuses.OrderStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ComponentScan(basePackages = "repositories")
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantMapper restaurantMapper;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final ObjectMapper objectMapper;

    private String tryToSerializeOrderEntityAsString(OrderEntity order) {
        String orderInLine;
        try {
            orderInLine = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return orderInLine;
    }

    private OrderItemEntity mapOrderItem(OrderEntity order, Long menuItemId, Long quantity) {
        if (quantity <= 0) throw new IllegalArgumentException();
        RestaurantEntity restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));

        List<RestaurantMenuItemEntity> menu = restaurant.getRestaurantMenuItems();

        RestaurantMenuItemEntity restaurantMenuItemEntity = restaurantMenuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

        if (!menu.contains(restaurantMenuItemEntity)) {
            log.warn("Указанные позиции отсутствуют в меню ресторана " + restaurant.getName()
                    + ". Доступные позиции для заказа: " + menu);
            throw new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND);
        }

        return new OrderItemEntity()
                .setOrderId(order.getId())
                .setQuantity(quantity)
                .setRestaurantMenuItem(restaurantMenuItemEntity)
                .setPrice(restaurantMenuItemEntity.getPrice() * quantity);
    }

    private OrderEntity mapOrderEntity(CustomerEntity customer, RestaurantEntity restaurant) {

        return new OrderEntity()
                .setStatus(OrderStatus.CUSTOMER_CREATED)
                .setCustomerId(customer.getId())
                .setRestaurantId(restaurant.getId())
                .setTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public ResponseEntity<ResponseDTO<OrderDTO>> getOrders(int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageRequest);
        List<OrderEntity> orders = orderPage.getContent();

        if (orders.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (OrderEntity order : orders) {
            String restaurantName = restaurantRepository.findById(order.getRestaurantId())
                    .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND)).getName();
            OrderDTO dto = OrderDTO.convertOrderToOrderDto(order, restaurantName);
            orderDTOS.add(dto);
        }

        ResponseDTO<OrderDTO> response = new ResponseDTO<OrderDTO>()
                .setOrders(orderDTOS)
                .setPageIndex(pageIndex)
                .setPageCount(pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderDTO> getOrderById(Long id) {

        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String restaurantName = restaurantRepository.findById(orderEntity.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND)).getName();

        return ResponseEntity.ok().body(OrderDTO.convertOrderToOrderDto(orderEntity, restaurantName));
    }

    @Transactional
    public ResponseEntity<OrderResponse> postNewOrder(OrderRequest orderRequest) {

        Long restaurantId = orderRequest.getRestaurantId();
        log.info("Поиск ресторана по id = " + restaurantId);
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));

        log.info("Поиск заказчика по id = 5");
        CustomerEntity customer = customerRepository.findById(5L) //TODO авторизованный заказчик
                .orElseThrow(() -> new EntityException(ExceptionStatus.CUSTOMER_NOT_FOUND));
        OrderEntity orderEntity = mapOrderEntity(customer, restaurant);

        log.info("Сохранение заказа в БД...");
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        log.info("Заказ " + savedOrder + " сохранён!");

        List<MenuItemDTO> menuItemDTOS = orderRequest.getMenuItems();

        log.info("Маппинг ДТО позиций заказа: " + menuItemDTOS + " на сущность...");
        List<OrderItemEntity> orderItems = menuItemDTOS.stream()
                .map(orderItem -> mapOrderItem(savedOrder, orderItem.getMenuItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());

        log.info("Маппинг успешный! Сохранение в базу...");
        orderItemRepository.saveAll(orderItems);
        log.info("Позиции заказа: " + orderItems + " сохранены!");

        /*rabbitMQProducerService.sendMessage(tryToSerializeOrderEntityAsString(savedOrder),
                "new.order.notification");*/

        OrderResponse response = new OrderResponse().setId(savedOrder.getId())
                .setSecretPaymentUrl("url")
                .setEstimatedTimeOfArrival("soon");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public ResponseEntity<String> deleteOrderById(Long id) {

        orderRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    public ResponseEntity<String> createNewOrderItem(Long orderId, OrderItemRequest request) {

        Long quantity = request.getQuantity();
        Long restaurantMenuItemId = request.getRestaurantMenuItemId();
        OrderEntity order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));
        orderItemRepository.save(mapOrderItem(order, restaurantMenuItemId, quantity));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<String> deleteOrderItemById(Long id) {

        orderItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<RestaurantDTO> getRestaurantByIdBatis(Long id) {

        RestaurantEntity restaurant = restaurantMapper.findById(id);
        if (restaurant == null)
            throw new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND);
        RestaurantDTO dto = RestaurantDTO.mapRestaurantEntityToDTO(restaurant);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<RestaurantDTO> getRestaurantByNameBatis(String name) {

        RestaurantEntity restaurant = restaurantMapper.findByName(name);
        if (restaurant == null)
            throw new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND);
        RestaurantDTO dto = RestaurantDTO.mapRestaurantEntityToDTO(restaurant);

        return ResponseEntity.ok(dto);
    }
}
