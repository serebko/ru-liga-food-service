package ru.liga.delivery_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.CourierEntity;
import entities.CustomerEntity;
import entities.OrderEntity;
import entities.RestaurantEntity;
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
import repositories.CourierRepository;
import repositories.CustomerRepository;
import repositories.OrderRepository;
import repositories.RestaurantRepository;
import ru.liga.delivery_service.dto.CourierDTO;
import ru.liga.delivery_service.dto.CustomerDTO;
import ru.liga.delivery_service.dto.DeliveryDTO;
import ru.liga.delivery_service.dto.OrderActionDTO;
import ru.liga.delivery_service.dto.RestaurantDTO;
import entities.OrderStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
@Slf4j
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final RabbitMQProducerServiceImpl rabbitMQProducerService;
    private final CourierRepository courierRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DeliveryService(OrderRepository orderRepository,
                           RabbitMQProducerServiceImpl rabbitMQProducerService,
                           CourierRepository courierRepository,
                           RestaurantRepository restaurantRepository,
                           CustomerRepository customerRepository,
                           ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.courierRepository = courierRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
    }

    private List<DeliveryDTO> convertOrderToDeliveryDTO(List<OrderEntity> orderEntities) {

        return orderEntities.stream()
                .map(this::convertOrderToDeliveryDTO)
                .collect(Collectors.toList());
    }

    private double calculateDistance(String courierCoordinates, String destinationCoordinates) {
        String[] parts1 = courierCoordinates.split(",");
        String[] parts2 = destinationCoordinates.split(",");

        if (parts1.length != 2 || parts2.length != 2) {
            throw new IllegalArgumentException("Неправильный формат координат");
        }

        double latitude1 = Double.parseDouble(parts1[0].trim());
        double longitude1 = Double.parseDouble(parts1[1].trim());
        double latitude2 = Double.parseDouble(parts2[0].trim());
        double longitude2 = Double.parseDouble(parts2[1].trim());

        double result = calculateMathematically(latitude1, longitude1, latitude2, longitude2);

        return Math.round(result * 10.0) / 10.0;
    }

    private double calculateMathematically(double latitude1, double longitude1,
                                           double latitude2, double longitude2) {
        double earthRadius = 6371;

        double dLatitude = Math.toRadians(latitude2 - latitude1);
        double dLongitude = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private DeliveryDTO convertOrderToDeliveryDTO(OrderEntity orderEntity) {

        RestaurantEntity restaurant = restaurantRepository.findById(orderEntity.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));

        CustomerEntity customer = customerRepository.findById(orderEntity.getCustomerId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.CUSTOMER_NOT_FOUND));

        //TODO: поменять на авторизованного курьера
        CourierEntity courier = courierRepository.findById(5L)
                .orElseThrow(() -> new EntityException(ExceptionStatus.CUSTOMER_NOT_FOUND));

        String restaurantCoordinates = restaurant.getAddress();
        String customerCoordinates = customer.getAddress();
        String courierCoordinates = courier.getCoordinates();
        Double distanceFromCourierToRestaurant = calculateDistance(restaurantCoordinates, courierCoordinates);
        Double distanceFromRestaurantToCustomer = calculateDistance(customerCoordinates, restaurantCoordinates);

        RestaurantDTO restaurantDto = new RestaurantDTO()
                .setAddress(restaurant.getAddress())
                .setDistance(distanceFromCourierToRestaurant);

        CustomerDTO customerDto = new CustomerDTO()
                .setAddress(customer.getAddress())
                .setDistance(distanceFromRestaurantToCustomer);

        return new DeliveryDTO()
                .setOrderId(orderEntity.getId())
                .setPayment("payment")
                .setRestaurant(restaurantDto)
                .setCustomer(customerDto);
    }

    public ResponseEntity<Map<String, Object>> getDeliveriesByStatus(String status, int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<OrderEntity> orderEntitiesPage = orderRepository
                .findOrderEntitiesByStatus(OrderStatus.valueOf(status.toUpperCase()), pageRequest);

        if (orderEntitiesPage.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderEntity> orders = orderEntitiesPage.getContent();
        List<DeliveryDTO> deliveryDtos = convertOrderToDeliveryDTO(orders);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", deliveryDtos);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> setDeliveryStatusByOrderId(Long orderId, OrderActionDTO orderActionDto) {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String orderAction = orderActionDto.getOrderAction().toUpperCase();

        if (OrderStatus.DELIVERY_PICKING.toString().equals(orderAction)) {
            rabbitMQProducerService.sendMessage("<courier_name> will pick that order!", "delivery.status");
        } else if (OrderStatus.DELIVERY_DENIED.toString().equals(orderAction)) {
            rabbitMQProducerService.sendMessage("Delivery denied", "delivery.status");
        }

        orderEntity.setStatus(OrderStatus.valueOf(orderAction));
        orderRepository.save(orderEntity);

        return ResponseEntity.ok().build();
    }

    @RabbitListener(queues = "postNewDelivery")
    public void processDeliveryQueue(String message) {

        OrderEntity order;
        try {
            order = objectMapper.readValue(message, OrderEntity.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        RestaurantEntity restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));
        String restaurantCoordinates = restaurant.getAddress();

        List<CourierEntity> waitingCouriers = courierRepository.findAllByStatus(OrderStatus.DELIVERY_PENDING);

        Map<Double, CourierEntity> courierDistances = new HashMap<>();

        for (CourierEntity courier : waitingCouriers) {
            String courierCoordinates = courier.getCoordinates();
            double courierDistanceToRestaurant = calculateDistance(restaurantCoordinates, courierCoordinates);
            courierDistances.put(courierDistanceToRestaurant, courier);
        }

        CourierEntity nearestCourier = courierDistances.remove(Collections.min(courierDistances.keySet()));

        //TODO: отправляем сообщение nearestCourier, что есть новый заказ для него. Он отвечает изменением
        // своего статуса с PENDING на PICKING/DENIED
        // если ок, то назначаем его в заказ. Если нет, то отправляем сообщение следующему ближайшему курьеру.


        if (nearestCourier.getStatus().toString().equals("DELIVERY_PICKING")) {
            //TODO: назначить ордеру из сообщения - курьера, обновить статус
            order.setStatus(OrderStatus.DELIVERY_PICKING);
            order.setCourierId(nearestCourier.getId());
        } else if (nearestCourier.getStatus().toString().equals("DELIVERY_DENIED")) {
            //TODO берём следующего курьера и делаем то же самое (рекурсивно?)
            CourierEntity nextNearestCourier = courierDistances.remove(Collections.min(courierDistances.keySet()));
        }

    }




    public ResponseEntity<CourierEntity> postNewCourier(CourierDTO dto) {

        CourierEntity courier = new CourierEntity()
                .setPhone(dto.getPhone())
                .setStatus(OrderStatus.valueOf(dto.getStatus().toUpperCase()))
                .setCoordinates(dto.getCoordinates());

        return ResponseEntity.status(HttpStatus.CREATED).body(courierRepository.save(courier));
    }

    public ResponseEntity<String> deleteCourierById(Long id) {
        courierRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted!");
    }

    public ResponseEntity<List<CourierEntity>> getAllCouriers() {
        return ResponseEntity.ok(courierRepository.findAll());
    }

    public ResponseEntity<String> setCourierForOrder(Long orderId, Long courierId) {
        if (courierId <= 0) throw new IllegalArgumentException();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));
        order.setCourierId(courierId);
        orderRepository.save(order);
        return ResponseEntity.ok("Courier set!");
    }
}
