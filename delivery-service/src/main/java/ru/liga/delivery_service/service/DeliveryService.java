package ru.liga.delivery_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import entities.CourierEntity;
import entities.CustomerEntity;
import entities.OrderEntity;
import entities.RestaurantEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.CourierRepository;
import repositories.CustomerRepository;
import repositories.OrderRepository;
import repositories.RestaurantRepository;
import ru.liga.delivery_service.dto.CustomerDTO;
import ru.liga.delivery_service.dto.DeliveryDTO;
import ru.liga.delivery_service.dto.OrderActionDTO;
import ru.liga.delivery_service.dto.RestaurantDTO;
import statuses.CourierStatus;
import statuses.OrderStatus;
import ru.liga.delivery_service.rabbit.RabbitMQProducerServiceImpl;

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

    @Autowired
    public DeliveryService(OrderRepository orderRepository,
                           RabbitMQProducerServiceImpl rabbitMQProducerService,
                           CourierRepository courierRepository,
                           RestaurantRepository restaurantRepository,
                           CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.courierRepository = courierRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
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
        List<DeliveryDTO> deliveryDtos = orders.stream()
                .map(this::convertOrderToDeliveryDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("orders", deliveryDtos);
        response.put("page_index", pageIndex);
        response.put("page_count", pageSize);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> setDeliveryStatusByOrderId(Long orderId, OrderActionDTO orderActionDto) {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String orderAction = orderActionDto.getOrderAction().toUpperCase();

        if (OrderStatus.DELIVERY_PICKING.toString().equals(orderAction)) {
            rabbitMQProducerService.sendMessage("<courier_name> will pick that order!", "courier.response");
            orderEntity.setCourierId(5L);
        } else if (OrderStatus.DELIVERY_DENIED.toString().equals(orderAction)) {
            rabbitMQProducerService.sendMessage("Delivery denied", "courier.response");
        }

        rabbitMQProducerService.sendMessage("New status: " + orderAction, "delivery.status.update");

        orderEntity.setStatus(OrderStatus.valueOf(orderAction));
        orderRepository.save(orderEntity);

        return ResponseEntity.ok().build();
    }

    public void processNewDelivery(OrderEntity order) {

        RestaurantEntity restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new EntityException(ExceptionStatus.RESTAURANT_NOT_FOUND));
        String restaurantCoordinates = restaurant.getAddress();

        List<CourierEntity> waitingCouriers = courierRepository.findAllByStatus(CourierStatus.PENDING);

        if (waitingCouriers.isEmpty()) {
            //повторный поиск через какое-то время
        }

        Map<Double, CourierEntity> courierDistances = new HashMap<>();

        for (CourierEntity courier : waitingCouriers) {
            String courierCoordinates = courier.getCoordinates();
            double courierDistanceToRestaurant = calculateDistance(restaurantCoordinates, courierCoordinates);
            courierDistances.put(courierDistanceToRestaurant, courier);
        }

        sendMessageToNearestCourier(courierDistances, order);
    }

    private void sendMessageToNearestCourier(Map<Double, CourierEntity> nearestCouriers, OrderEntity order) {

        CourierEntity nearestCourier = nearestCouriers.remove(Collections.min(nearestCouriers.keySet()));

        //TODO: отправляем сообщение о новой доставке nearestCourier'у, он отвечает изменением СВОЕГО статуса
        // если он PICKING то назначаем его в заказ, если DENIED то рекурсивно вызываем этот же метод

        if (nearestCourier.getStatus().equals(CourierStatus.PICKING)) {
            order.setCourierId(nearestCourier.getId());
        } else if (nearestCourier.getStatus().equals(CourierStatus.DENIED)) {
            sendMessageToNearestCourier(nearestCouriers, order);
        }
    }
}
