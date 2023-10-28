package ru.liga.kitchen_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.OrderEntity;
import entities.OrderItemEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.kitchen_service.client.DeliveryServiceClient;
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.OrderItemDTO;
import ru.liga.kitchen_service.dto.OrderDTO;
import ru.liga.kitchen_service.dto.RestaurantDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import entities.OrderStatus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
@Slf4j
public class KitchenService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final OrderRepository orderRepository;
    private final DeliveryServiceClient deliveryServiceClient;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KitchenService(RestaurantRepository restaurantRepository,
                          RestaurantMenuItemRepository restaurantMenuItemRepository,
                          OrderRepository orderRepository,
                          DeliveryServiceClient deliveryServiceClient,
                          RabbitMQProducerServiceImpl rabbitMQProducerService,
                          ObjectMapper objectMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.orderRepository = orderRepository;
        this.deliveryServiceClient = deliveryServiceClient;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.objectMapper = objectMapper;
    }

    private List<OrderItemDTO> convertOrderItemToOrderItemDTO(List<OrderItemEntity> orderItemEntities) {
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        for (OrderItemEntity item : orderItemEntities) {
            OrderItemDTO dto = new OrderItemDTO()
                    .setMenuItemId(item.getRestaurantMenuItem().getId())
                    .setQuantity(item.getQuantity());
            orderItemDTOS.add(dto);
        }
        return orderItemDTOS;
    }
    private List<OrderDTO> convertOrderToOrderDto(List<OrderEntity> orderEntities) {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            List<OrderItemEntity> items = orderEntity.getItems();

            OrderDTO dto = new OrderDTO()
                    .setId(orderEntity.getId())
                    .setOrderItems(convertOrderItemToOrderItemDTO(items));
            orderDTOS.add(dto);
        }
        return orderDTOS;
    }

    private RestaurantMenuItemEntity mapRestaurantMenuItem(RestaurantMenuItemDTO request) {
        //TODO: поменять на авторизованный ресторан

        return new RestaurantMenuItemEntity()
                .setRestaurantId(5L)
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setImage(request.getImage())
                .setDescription(request.getDescription());
    }

    @RabbitListener(queues = "orderToKitchen")
    public void processOrderQueue(String order) {
        log.debug("Received order from customer..");
        OrderEntity orderEntity;
        try {
            orderEntity = objectMapper.readValue(order, OrderEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.debug(orderEntity.toString());
    }

    @RabbitListener(queues = "courierResponse")
    public void processDeliveryQueue(String response) {
        log.debug("Response from courier: <<" + response + ">>");
    }

    public ResponseEntity<Map<String, Object>> getOrdersByStatus(String status, int pageIndex, int pageSize) {
        //TODO: поменять на авторизованный ресторан

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderEntities = orderRepository
                .findOrderEntitiesByStatus(OrderStatus.valueOf(status.toUpperCase()), pageRequest);

        if (orderEntities.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderEntity> orders = orderEntities.getContent();
        List<OrderDTO> orderDTOS = convertOrderToOrderDto(orders);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderDTOS);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> postNewRestaurantMenuItem(RestaurantMenuItemDTO request) {

        restaurantMenuItemRepository.save(mapRestaurantMenuItem(request));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<String> deleteRestaurantMenuItemById(Long id) {

        restaurantMenuItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<String> changePriceInMenuItemById(Long id, PriceDTO request) {

        Double newPrice = request.getNewPrice();
        if (newPrice <= 0) throw new IllegalArgumentException();

        RestaurantMenuItemEntity menuItem = restaurantMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

        menuItem.setPrice(newPrice);
        restaurantMenuItemRepository.save(menuItem);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> setOrderStatusById(Long id, OrderActionDTO orderAction) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        if (orderAction.getOrderAction().equalsIgnoreCase(OrderStatus.DELIVERY_PENDING.toString()))
            rabbitMQProducerService.sendMessage(tryToSerializeOrderEntityAsString(order), "new.delivery");

        return deliveryServiceClient.setOrderStatusById(id, orderAction);
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



    public ResponseEntity<RestaurantEntity> postNewRestaurant(RestaurantDTO dto) {

        RestaurantEntity restaurant = new RestaurantEntity()
                .setAddress(dto.getAddress())
                .setStatus(dto.getStatus())
                .setName(dto.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantRepository.save(restaurant));
    }

    public ResponseEntity<String> deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted!");
    }

    public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }
}
