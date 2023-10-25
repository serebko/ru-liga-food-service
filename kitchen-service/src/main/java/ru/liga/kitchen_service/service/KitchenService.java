package ru.liga.kitchen_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import entities.OrderEntity;
import entities.OrderItemEntity;
import entities.RestaurantEntity;
import entities.RestaurantMenuItemEntity;
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
import ru.liga.kitchen_service.client.DeliveryServiceClient;
import ru.liga.kitchen_service.dto.OrderActionDTO;
import ru.liga.kitchen_service.dto.PriceDTO;
import ru.liga.kitchen_service.dto.OrderItemDTO;
import ru.liga.kitchen_service.dto.OrderDTO;
import ru.liga.kitchen_service.dto.RestaurantMenuItemDTO;
import service.OrderStatus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
public class KitchenService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DeliveryServiceClient deliveryServiceClient;

    @Autowired
    public KitchenService(RestaurantRepository restaurantRepository,
                          RestaurantMenuItemRepository restaurantMenuItemRepository,
                          OrderItemRepository orderItemRepository,
                          OrderRepository orderRepository,
                          DeliveryServiceClient deliveryServiceClient) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.deliveryServiceClient = deliveryServiceClient;
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
        //TODO: когда будет авторизация, тогда изменить под запрос от определенного ресторана, пока id=4

        RestaurantEntity restaurant = restaurantRepository.findById(4L)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));

        return new RestaurantMenuItemEntity()
                .setRestaurant(restaurant)
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setImage(request.getImage())
                .setDescription(request.getDescription());
    }

    public ResponseEntity<Map<String, Object>> getOrdersByStatus(String status, int pageIndex, int pageSize) {
        //TODO: когда будет авторизация, тогда изменить под запрос от определенного ресторана

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

        RestaurantMenuItemEntity savedItem = restaurantMenuItemRepository.save(mapRestaurantMenuItem(request));
        savedItem.getRestaurant().addMenuItem(savedItem);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<String> deleteRestaurantMenuItemById(Long id) {

        // TODO: @если указать в связи меню и заказа cascadeType delete,
        //  то у нас будет удаляться каскадно запись в таблице order при только одном запросе@
        //  *** rest_menu_item не содержит в себе связь с order_item, только order_item имеет OneToOne связь с rest_menu_item

        try {
            List<OrderItemEntity> orderItemEntities = orderItemRepository.findAllByRestaurantMenuItemId(id)
                    .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_ITEM_NOT_FOUND));
            orderItemRepository.deleteAll(orderItemEntities);
        } catch (EntityException ignored) {}

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
        return deliveryServiceClient.setOrderStatusById(id, orderAction);
    }
}
