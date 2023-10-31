package ru.liga.kitchen_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ResponseDTO;
import entities.OrderEntity;
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
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.kitchen_service.client.DeliveryServiceClient;
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.OrderDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import statuses.OrderStatus;
import ru.liga.kitchen_service.rabbit.service.RabbitMQProducerServiceImpl;

import java.util.List;

@Service
@ComponentScan(basePackages = "repositories")
@Slf4j
@RequiredArgsConstructor
public class KitchenService {

    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryServiceClient deliveryServiceClient;
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
    public void processNewOrder(OrderEntity order) {
        //обработка нового заказа
    }

    public void processCourierResponse(String message) {
        //обработка ответа курьера
    }

    public ResponseEntity<ResponseDTO<OrderDTO>> getOrdersByStatus(String status, int pageIndex, int pageSize) {
        //TODO: авторизованный ресторан

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderEntities = orderRepository
                .findOrderEntitiesByStatus(OrderStatus.valueOf(status.toUpperCase()), pageRequest);

        List<OrderEntity> orders = orderEntities.getContent();

        if (orders.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderDTO> orderDTOS = OrderDTO.convertOrderToOrderDto(orders);

        ResponseDTO<OrderDTO> response = new ResponseDTO<OrderDTO>()
                .setOrders(orderDTOS)
                .setPageIndex(pageIndex)
                .setPageCount(pageSize);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<RestaurantMenuItemEntity> postNewRestaurantMenuItem(RestaurantMenuItemDTO request) {

        RestaurantMenuItemEntity newMenuItem = RestaurantMenuItemDTO
                .mapRestaurantMenuItem(request)
                .setRestaurantId(1L); //TODO: авторизованный ресторан

        RestaurantMenuItemEntity savedItem = restaurantMenuItemRepository.save(newMenuItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @Transactional
    public ResponseEntity<String> deleteRestaurantMenuItemById(Long id) {

        restaurantMenuItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    public ResponseEntity<RestaurantMenuItemEntity> changePriceInMenuItemById(Long id, PriceDTO request) {

        Double newPrice = request.getNewPrice();
        if (newPrice <= 0) throw new IllegalArgumentException();

        RestaurantMenuItemEntity menuItem = restaurantMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

        menuItem.setPrice(newPrice);
        RestaurantMenuItemEntity savedItem = restaurantMenuItemRepository.save(menuItem);

        return ResponseEntity.ok().body(savedItem);
    }

    @Transactional
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
