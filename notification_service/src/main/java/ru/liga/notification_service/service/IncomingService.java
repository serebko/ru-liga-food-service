package ru.liga.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.liga.notification_service.dto.Message;
import ru.liga.notification_service.rabbit.service.RabbitMQProducerServiceImpl;

import java.util.List;

@Service
@Slf4j
public class IncomingService {

    private final RabbitMQProducerServiceImpl rabbitProducer;
    private final ObjectMapper objectMapper;

    public IncomingService(RabbitMQProducerServiceImpl rabbitProducer, ObjectMapper objectMapper) {
      this.rabbitProducer = rabbitProducer;
      this.objectMapper = objectMapper;
    }

    public void process(Message message) throws JsonProcessingException {
      List<String> routKeys = message.getStatus().getRoutKeys();
      String messageStr = objectMapper.writeValueAsString(message);
      for (String key : routKeys) {
        rabbitProducer.sendMessage(messageStr, key);
      }
      log.info("Отправляется сообщение: " + message);
    }
}
