package ru.liga.order_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import entities.CustomerEntity;
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
import repositories.CustomerRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.dto.MenuItemDTO;
import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.dto.OrderItemDTO;
import ru.liga.order_service.dto.OrderItemRequest;
import ru.liga.order_service.dto.OrderRequest;
import ru.liga.order_service.dto.ResponseOnCreation;
import ru.liga.order_service.dto.RestaurantDTO;
import service.OrderStatus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
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

    private List<OrderItemDTO> convertOrderItemToOrderItemDto(List<OrderItemEntity> items) {

        return items.stream()
                .map(this::convertOrderItemToOrderItemDto)
                .collect(Collectors.toList());
    }

    private OrderItemDTO convertOrderItemToOrderItemDto(OrderItemEntity item) {

        return new OrderItemDTO()
                .setImage(item.getRestaurantMenuItem().getImage())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setDescription(item.getRestaurantMenuItem().getDescription());
    }

    private List<OrderDTO> convertOrderToOrderDto(List<OrderEntity> orderEntities) {

        return orderEntities.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
    }

    private OrderDTO convertOrderToOrderDto(OrderEntity orderEntity) {

        return new OrderDTO()
                .setId(orderEntity.getId())
                .setRestaurant(new RestaurantDTO().setName(orderEntity.getRestaurant().getName()))
                .setItems(convertOrderItemToOrderItemDto(orderEntity.getItems()))
                .setTimestamp(orderEntity.getTimestamp());
    }

    public ResponseEntity<Map<String, Object>> getOrders(int index, int size) {

        PageRequest pageRequest = PageRequest.of(index, size);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageRequest);

        if (orderPage.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderEntity> orders = orderPage.getContent();
        List<OrderDTO> orderDTOS = convertOrderToOrderDto(orders);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderDTOS);
        response.put("page_index", index);
        response.put("page_count", size);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderDTO> getOrderById(Long id) {

        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        return ResponseEntity.ok().body(convertOrderToOrderDto(orderEntity));
    }

    public ResponseEntity<ResponseOnCreation> postNewOrder(OrderRequest orderRequest) {

        RestaurantEntity restaurantEntity = restaurantRepository.findById(orderRequest.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));

        //Пока неизвестно как понимать кто именно заказывает, поэтому допустим, что заказывает customer с id=4
        CustomerEntity customerEntity = customerRepository.findById(4L)
                .orElseThrow(() -> new EntityException(ExceptionStatus.CUSTOMER_NOT_FOUND));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CUSTOMER_CREATED)
                .setCustomer(customerEntity)
                .setRestaurant(restaurantEntity)
                .setTimestamp(timestamp);

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        for (MenuItemDTO dto : orderRequest.getMenuItems()) {

            RestaurantMenuItemEntity menuItem = restaurantMenuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

            Long quantity = dto.getQuantity();
            if (quantity <= 0) throw new IllegalArgumentException();
            Double price = menuItem.getPrice();

            OrderItemEntity item = new OrderItemEntity()
                    .setOrder(orderEntity)
                    .setRestaurantMenuItem(menuItem)
                    .setQuantity(quantity)
                    .setPrice(price * quantity);

            OrderItemEntity savedItem = orderItemRepository.save(item);
            orderEntity.addOrderItem(savedItem);
        }

        restaurantEntity.addOrder(orderEntity);
        customerEntity.addOrder(orderEntity);

        ResponseOnCreation response = new ResponseOnCreation().setId(savedOrder.getId())
                .setSecretPaymentUrl("url")
                .setEstimatedTimeOfArrival("soon");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<String> deleteOrderById(Long id) {

        orderRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<String> createNewOrderItem(Long orderId, OrderItemRequest request) {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        Long restaurantMenuItemId = request.getRestaurantMenuItemId();
        Long quantity = request.getQuantity();
        if (restaurantMenuItemId <= 0 || quantity <= 0) throw new IllegalArgumentException();

        RestaurantMenuItemEntity restaurantMenuItemEntity = restaurantMenuItemRepository.findById(restaurantMenuItemId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

        OrderItemEntity newOrderItemEntity = new OrderItemEntity()
                .setOrder(orderEntity)
                .setQuantity(quantity)
                .setRestaurantMenuItem(restaurantMenuItemEntity)
                .setPrice(restaurantMenuItemEntity.getPrice() * quantity);

        OrderItemEntity savedOrderItemEntity = orderItemRepository.save(newOrderItemEntity);
        orderEntity.addOrderItem(savedOrderItemEntity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<String> deleteOrderItemById(Long id) {

        orderItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
