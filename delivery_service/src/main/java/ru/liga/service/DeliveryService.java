package ru.liga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.advice.EntityException;
import ru.liga.advice.ExceptionStatus;
import ru.liga.dao.CourierRepository;
import ru.liga.dto.Message;
import ru.liga.dto.OrderStatus;
import ru.liga.entity.CourierEntity;
import ru.liga.status.CourierStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ComponentScan
@Slf4j
public class DeliveryService {

    private static final String DEVICE_REGISTRATION_URL_PATH = "/message";
    private static final String RESTAURANT_COORDINATES_URL_PATH = "/orders/restCoord/";

    @Value("${notification.service}")
    String notificationService;

    @Value("${order.service}")
    String orderService;

    @Value("${notification.deliveryLogin}")
    String deliveryLoginForNotification;

    @Value("${notification.deliveryPass}")
    String deliveryPassForNotification;

    @Value("${order.deliveryLogin}")
    String deliveryLoginForOrder;

    @Value("${order.deliveryPass}")
    String deliveryPassForOrder;

    private final RestTemplate restTemplate = new RestTemplate();

    private final CourierRepository courierRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, Message> orders = new HashMap<>();

    private double calculateDistance(String courierCoordinates, String destinationCoordinates) {
        String[] parts1 = courierCoordinates.split(",");
        String[] parts2 = destinationCoordinates.split(",");

        if (parts1.length != 2 || parts2.length != 2) {
            throw new IllegalArgumentException("Неправильный формат координат\n" +
                    "Используйте следующий формат: '56.26851626074396, 46.4656705552914' ");
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

    public Collection<Message> getAvailableDeliveries() {
        return orders.values();
    }

    public void processNewDelivery(String message) throws JsonProcessingException {
        Message messageFromRabbit = objectMapper.readValue(message, Message.class);

        orders.put(messageFromRabbit.getOrderUid(), messageFromRabbit);

        String restaurantCoordinates = getRestaurantCoordinates(messageFromRabbit.getOrderUid());

        List<CourierEntity> waitingCouriers = courierRepository.findAllByStatus(CourierStatus.PENDING);
        Map<Double, CourierEntity> courierDistances = new HashMap<>();

        for (CourierEntity courier : waitingCouriers) {
            String courierCoordinates = courier.getCoordinates();
            double courierDistanceToRestaurant = calculateDistance(restaurantCoordinates, courierCoordinates);
            courierDistances.put(courierDistanceToRestaurant, courier);
        }

        CourierEntity courier = getNearestCourier(courierDistances, messageFromRabbit);
        if (courier != null) {
            changeOrderStatus(OrderStatus.DELIVERY_PICKING,
              new Message(messageFromRabbit.getStatus(),
                messageFromRabbit.getOrderUid(),
                messageFromRabbit.getOrderItems(),
                courier.getUid())
            );
        }
    }

    public String setDeliveryStatusByOrderId(String orderUid, OrderStatus newStatus) {
        Message message = new Message(newStatus, orderUid, null, null);
        changeOrderStatus(newStatus, message);
        if (newStatus == OrderStatus.DELIVERY_PICKING) {
            courierRepository.findById(4L) //todo авторизованный курьер
                    .orElseThrow(() -> new EntityException(ExceptionStatus.COURIER_NOT_FOUND));
        }
        orders.remove(orderUid);
        return "Статус заказа id=" + orderUid + " изменён на: " + newStatus;
    }

    private void changeOrderStatus(OrderStatus status, Message messageFromRabbit) {

        HttpHeaders headers = getBaseAuthenticationHeader(deliveryLoginForNotification, deliveryPassForNotification);

        HttpEntity<Message> httpEntity = new HttpEntity<>(
          new Message(status,
            messageFromRabbit.getOrderUid(),
            messageFromRabbit.getOrderItems(),
            messageFromRabbit.getCourierUid()
          ), headers);

        restTemplate.postForObject(
          notificationService + DEVICE_REGISTRATION_URL_PATH, httpEntity,
          ResponseEntity.class);

        log.info("Статус заказа id=" + messageFromRabbit.getOrderItems() + " изменён на: " + status.name());
    }

    private HttpHeaders getBaseAuthenticationHeader(String login, String pass) {
        String plainCreds = login + ":" + pass;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    private String getRestaurantCoordinates(String orderUid) {
        HttpHeaders headers = getBaseAuthenticationHeader(deliveryLoginForOrder, deliveryPassForOrder);

        HttpEntity<Message> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> restaurantCoordinates = restTemplate.exchange(
          orderService + RESTAURANT_COORDINATES_URL_PATH + orderUid, HttpMethod.GET, httpEntity,
          String.class);

        return restaurantCoordinates.getBody();
    }

    private CourierEntity getNearestCourier(Map<Double, CourierEntity> nearestCouriers, Message message) {
        if (nearestCouriers.isEmpty()) {
            log.info("Курьер не найден!");
            return null;
        }
        CourierEntity courier = nearestCouriers.remove(Collections.min(nearestCouriers.keySet()));
        log.info("Новая доставка: " + message.getOrderUid() + " Ты ближе всех!");
        courier.setStatus(CourierStatus.PICKING);
        courierRepository.save(courier);
        return courier;

    }


}
