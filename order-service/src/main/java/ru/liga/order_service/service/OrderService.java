package ru.liga.order_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.CustomerEntity;
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
import repositories.CustomerRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import repositories.RestaurantMenuItemRepository;
import repositories.RestaurantRepository;
import ru.liga.order_service.dto.CustomerDTO;
import ru.liga.order_service.dto.MenuItemDTO;
import ru.liga.order_service.dto.OrderDTO;
import ru.liga.order_service.dto.OrderItemDTO;
import ru.liga.order_service.dto.OrderItemRequest;
import ru.liga.order_service.dto.OrderRequest;
import ru.liga.order_service.dto.ResponseOnCreation;
import ru.liga.order_service.dto.RestaurantDTO;
import ru.liga.order_service.mapper.RestaurantMapper;
import entities.OrderStatus;

import java.sql.Timestamp;
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
    private final CustomerRepository customerRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final RestaurantMapper restaurantMapper;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        RestaurantRepository restaurantRepository,
                        CustomerRepository customerRepository,
                        RestaurantMenuItemRepository restaurantMenuItemRepository,
                        RestaurantMapper restaurantMapper,
                        RabbitMQProducerServiceImpl rabbitMQProducerService,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
        this.restaurantMenuItemRepository = restaurantMenuItemRepository;
        this.restaurantMapper = restaurantMapper;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.objectMapper = objectMapper;
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

        String restaurantName = restaurantRepository.findById(orderEntity.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND)).getName();

        return new OrderDTO()
                .setId(orderEntity.getId())
                .setRestaurant(new RestaurantDTO().setName(restaurantName))
                .setItems(convertOrderItemToOrderItemDto(orderEntity.getItems()))
                .setTimestamp(orderEntity.getTimestamp());
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

    @RabbitListener(queues = "updatesToCustomer")
    public void processOrderQueue(String statusUpdate) {
        log.debug(statusUpdate);
    }

    public ResponseEntity<Map<String, Object>> getOrders(int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageRequest);

        if (orderPage.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderEntity> orders = orderPage.getContent();
        List<OrderDTO> orderDTOS = convertOrderToOrderDto(orders);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderDTOS);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderDTO> getOrderById(Long id) {

        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        return ResponseEntity.ok().body(convertOrderToOrderDto(orderEntity));
    }

    public ResponseEntity<ResponseOnCreation> postNewOrder(OrderRequest orderRequest) {
        if (orderRequest.getRestaurantId() <= 0) throw new IllegalArgumentException();
        //TODO: когда будет авторизация, поменять customer на залогиненного
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CUSTOMER_CREATED)
                .setCustomerId(6L)
                .setRestaurantId(orderRequest.getRestaurantId())
                .setTimestamp(new Timestamp(System.currentTimeMillis()));

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        for (MenuItemDTO dto : orderRequest.getMenuItems()) {

            RestaurantMenuItemEntity menuItem = restaurantMenuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_MENU_ITEM_NOT_FOUND));

            Long quantity = dto.getQuantity();
            if (quantity <= 0) throw new IllegalArgumentException();
            Double price = menuItem.getPrice();

            OrderItemEntity item = new OrderItemEntity()
                    .setOrderId(savedOrder.getId())
                    .setRestaurantMenuItem(menuItem)
                    .setQuantity(quantity)
                    .setPrice(price * quantity);

           orderItemRepository.save(item);
        }

        rabbitMQProducerService.sendMessage(tryToSerializeOrderEntityAsString(savedOrder), "new.order");

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
                .setOrderId(orderEntity.getId())
                .setQuantity(quantity)
                .setRestaurantMenuItem(restaurantMenuItemEntity)
                .setPrice(restaurantMenuItemEntity.getPrice() * quantity);

        orderItemRepository.save(newOrderItemEntity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<String> deleteOrderItemById(Long id) {

        orderItemRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<Map<String, Object>> getRestaurantByIdBatis(Long id) {

        if (id <= 0)
            throw new IllegalArgumentException();

        RestaurantEntity restaurant = restaurantMapper.findById(id);

        if (restaurant == null)
            throw new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND);

        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", restaurant);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getRestaurantByNameBatis(String name) {

        RestaurantEntity restaurant = restaurantMapper.findByName(name);
        if (restaurant == null)
            throw new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND);

        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", restaurant);

        return ResponseEntity.ok(response);
    }




    public ResponseEntity<CustomerEntity> postNewCustomer(CustomerDTO dto) {

        CustomerEntity customer = new CustomerEntity()
                .setPhone(dto.getPhone())
                .setEmail(dto.getEmail())
                .setAddress(dto.getAddress());

        return ResponseEntity.status(HttpStatus.CREATED).body(customerRepository.save(customer));
    }

    public ResponseEntity<String> deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Customer with id = " + id + "deleted successful");
    }

    public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }
}
