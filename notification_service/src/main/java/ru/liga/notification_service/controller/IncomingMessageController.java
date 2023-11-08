package ru.liga.notification_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.notification_service.dto.Message;
import ru.liga.notification_service.service.IncomingService;

@RestController
@Tag(name = "Приложение приёма сообщений от сервисов и их отправка получателям")
public class IncomingMessageController {

    private final IncomingService service;

    public IncomingMessageController(IncomingService service) {
      this.service = service;
    }

    @PostMapping("/message")
    @Operation(summary = "Пересылает сообщение")
    public ResponseEntity<String> forwardMessage(@RequestBody Message message) {
      try {
        service.process(message);
      } catch (JsonProcessingException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      return ResponseEntity.ok().build();
    }
  }
