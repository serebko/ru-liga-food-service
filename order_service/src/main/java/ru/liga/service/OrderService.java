package ru.liga.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import ru.liga.advice.EntityException;
import ru.liga.advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.liga.dao.OrderItemRepository;
import ru.liga.dto.Message;
import ru.liga.entities.CustomerEntity;
import ru.liga.entities.OrderEntity;
import ru.liga.entities.OrderItemEntity;
import ru.liga.entities.RestaurantEntity;
import ru.liga.entities.RestaurantMenuItemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.liga.dao.CustomerRepository;
import ru.liga.dao.RestaurantMenuItemRepository;
import ru.liga.dao.RestaurantRepository;
import ru.liga.dao.OrderRepository;
import ru.liga.dto.MenuItemDTO;
import ru.liga.dto.OrderDTO;
import ru.liga.requests.OrderRequest;
import ru.liga.response.ResponseDTO;
import ru.liga.statuses.OrderStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ComponentScan
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private static final String DEVICE_REGISTRATION_URL_PATH = "/message";
    @Value("${notification.service}")
    String notificationService;

    @Value("${notification.orderLogin}")
    String kitchenLogin;

    @Value("${notification.orderPass}")
    String kitchenPass;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuItemRepository restaurantMenuItemRepository;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();



    private OrderItemEntity mapOrderItem(OrderEntity order, Long menuItemId, Long quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Количество не может быть меньше или равно нулю");
        RestaurantEntity restaurant = order.getRestaurant();

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
                .setCustomer(customer)
                .setRestaurant(restaurant)
                .setTimestamp(new Timestamp(System.currentTimeMillis()))
                .setUid(String.valueOf(UUID.randomUUID()));
    }

    public ResponseEntity<ResponseDTO<OrderDTO>> getOrders(int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageRequest);
        List<OrderEntity> orders = orderPage.getContent();

        if (orders.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (OrderEntity order : orders) {
            String restaurantName = order.getRestaurant().getName();
            OrderDTO dto = OrderDTO.convertOrderToOrderDto(order, restaurantName);
            orderDTOS.add(dto);
        }

        ResponseDTO<OrderDTO> response = new ResponseDTO<OrderDTO>()
                .setOrders(orderDTOS)
                .setPageIndex(pageIndex)
                .setPageCount(pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderDTO> getOrderById(String id) {

        OrderEntity orderEntity = orderRepository.findOrderEntityByUid(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String restaurantName = orderEntity.getRestaurant().getName();

        return ResponseEntity.ok().body(OrderDTO.convertOrderToOrderDto(orderEntity, restaurantName));
    }

    public ResponseEntity<String> postNewOrder(OrderRequest orderRequest) {

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

        String responseText = "Заказ создан (uid "+ savedOrder.getUid() + "), ожидаем оплату";
        return ResponseEntity.status(HttpStatus.CREATED).body(responseText);
    }

    public ResponseEntity<String> imitatePayment(String id) {

        OrderEntity orderEntity = orderRepository.findOrderEntityByUid(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        OrderStatus currentOrderStatus = orderEntity.getStatus();
        if (currentOrderStatus == OrderStatus.CUSTOMER_CREATED
                || currentOrderStatus == OrderStatus.CUSTOMER_CANCELLED) {

            orderEntity.setStatus(OrderStatus.CUSTOMER_PAID);

            String orderItemsString;
            try {
                orderItemsString = objectMapper.writeValueAsString(orderEntity.getItems());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String plainCreds = kitchenLogin + ":" + kitchenPass;
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64Creds);

            Message messageForKitchen = new Message(orderEntity.getStatus(), orderEntity.getUid(), orderItemsString, null);

            HttpEntity<Message> httpEntity = new HttpEntity<>(messageForKitchen, headers);
            restTemplate.postForObject(
                    notificationService + DEVICE_REGISTRATION_URL_PATH, httpEntity,
                    ResponseEntity.class);

        } else {
            orderEntity.setStatus(OrderStatus.CUSTOMER_CANCELLED);
        }

        orderRepository.save(orderEntity);
        return ResponseEntity.ok().build();
    }

    public String updateOrderStatusById(String id, OrderStatus newStatus) {

        OrderEntity order = orderRepository.findOrderEntityByUid(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        order.setStatus(newStatus);
        orderRepository.save(order);

        return "Статус заказа id=" + id + " изменён на: " + newStatus;
    }

    public void processStatusUpdate(String message) {
        Message updateMessage;
        try {
            updateMessage = objectMapper.readValue(message, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (updateMessage.getCourierUid() != null) {
            OrderEntity order = orderRepository.findOrderEntityByUid(updateMessage.getOrderUid())
                    .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));
            order.setCourierUid(updateMessage.getCourierUid());
            orderRepository.save(order);
        }

        updateOrderStatusById(updateMessage.getOrderUid(), updateMessage.getStatus());
    }

    public String getRestaurantCoordinates(String id) {
        OrderEntity order = orderRepository.findOrderEntityByUid(id)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));
        return order.getRestaurant().getAddress();
    }
}