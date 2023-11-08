package ru.liga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.dto.Message;
import ru.liga.dto.OrderStatus;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class KitchenService {

    private static final String DEVICE_REGISTRATION_URL_PATH = "/message";
    @Value("${notification.service:#{NULL}}")
    String notificationService;

    @Value("${notification.kitchenLogin:#{NULL}}")
    String kitchenLogin;

    @Value("${notification.kitchenPass:#{NULL}}")
    String kitchenPass;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Message> orders = new HashMap<>();

    private boolean isKitchenAcceptOrder(Message message) {
        //логика по принятию/непринятию заказа
        return true;
    }

    private void refund(String orderId) {
        //возврат средств
    }


    public String sendMessageOfStatusUpdate(Message message, OrderStatus newStatus) {
        Message messageFromKitchen = new Message(newStatus, message.getOrderUid(), message.getOrderItems(), null);


        String plainCreds = kitchenLogin + ":" + kitchenPass;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        log.info("Отправляем запрос на изменение статуса заказа {} в notificationService", message.getOrderUid());
        HttpEntity<Message> httpEntity = new HttpEntity<>(messageFromKitchen, headers);
        restTemplate.postForObject(
          notificationService + DEVICE_REGISTRATION_URL_PATH, httpEntity,
          ResponseEntity.class);

        return "Статус заказа id=" + message.getOrderUid() + " изменён на: " + newStatus;
    }

    public void processNewOrder(String message) throws JsonProcessingException {
        Message orderMessage = objectMapper.readValue(message, Message.class);
        String orderUid = orderMessage.getOrderUid();
        if (isKitchenAcceptOrder(orderMessage)) {
            sendMessageOfStatusUpdate(orderMessage, OrderStatus.KITCHEN_ACCEPTED);
            orders.put(orderUid, orderMessage);
        } else {
            sendMessageOfStatusUpdate(orderMessage, OrderStatus.KITCHEN_DENIED);
            refund(orderUid);
            sendMessageOfStatusUpdate(orderMessage, OrderStatus.KITCHEN_REFUNDED);
            orders.remove(orderUid);
        }
    }
}