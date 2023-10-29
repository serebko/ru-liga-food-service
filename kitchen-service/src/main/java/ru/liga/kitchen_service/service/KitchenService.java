package ru.liga.kitchen_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.OrderEntity;
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
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import ru.liga.kitchen_service.client.DeliveryServiceClient;
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.OrderDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import statuses.OrderStatus;
import ru.liga.kitchen_service.rabbit.service.RabbitMQProducerServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
@Slf4j
public class KitchenService {

    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final OrderRepository orderRepository;
    private final DeliveryServiceClient deliveryServiceClient;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KitchenService(RestaurantMenuItemRepository restaurantMenuItemRepository,
                          OrderRepository orderRepository,
                          DeliveryServiceClient deliveryServiceClient,
                          RabbitMQProducerServiceImpl rabbitMQProducerService,
                          ObjectMapper objectMapper) {
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.orderRepository = orderRepository;
        this.deliveryServiceClient = deliveryServiceClient;
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
    public void processNewOrder(OrderEntity order) {
        order.setStatus(OrderStatus.KITCHEN_ACCEPTED);
        orderRepository.save(order);
        //обработка нового заказа
    }

    public void processCourierResponse(String message) {
        //обработка ответа курьера
    }

    public ResponseEntity<Map<String, Object>> getOrdersByStatus(String status, int pageIndex, int pageSize) {
        //TODO: поменять на авторизованный ресторан

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderEntities = orderRepository
                .findOrderEntitiesByStatus(OrderStatus.valueOf(status.toUpperCase()), pageRequest);

        List<OrderEntity> orders = orderEntities.getContent();

        if (orders.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderDTO> orderDTOS = OrderDTO.convertOrderToOrderDto(orders);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderDTOS);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> postNewRestaurantMenuItem(RestaurantMenuItemDTO request) {

        restaurantMenuItemRepository.save(RestaurantMenuItemDTO.mapRestaurantMenuItem(request));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Void> deleteRestaurantMenuItemById(Long id) {

        restaurantMenuItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Void> changePriceInMenuItemById(Long id, PriceDTO request) {

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

        rabbitMQProducerService.sendMessage("Order status update " + orderAction.getOrderAction(),
                "kitchen.status.update");

        return deliveryServiceClient.setOrderStatusById(id, orderAction);
    }
}
