package ru.liga.delivery_service.service;

import advice.EntityException;
import advice.ExceptionStatus;
import entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.OrderRepository;
import ru.liga.delivery_service.dto.CustomerDTO;
import ru.liga.delivery_service.dto.DeliveryDTO;
import ru.liga.delivery_service.dto.OrderActionDTO;
import ru.liga.delivery_service.dto.RestaurantDTO;
import service.OrderStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@ComponentScan(basePackages = "repositories")
public class DeliveryService {

    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private List<DeliveryDTO> convertOrderToDeliveryDTO(List<OrderEntity> orderEntities) {

        return orderEntities.stream()
                .map(this::convertOrderToDeliveryDTO)
                .collect(Collectors.toList());
    }

    private DeliveryDTO convertOrderToDeliveryDTO(OrderEntity orderEntity) {
        //TODO: Подумать про координаты.

        RestaurantDTO restaurantDto = new RestaurantDTO().setAddress(orderEntity.getRestaurant().getAddress())
                .setDistance("1200");
        CustomerDTO customerDto = new CustomerDTO().setAddress(orderEntity.getCustomer().getAddress())
                .setDistance("600");

        return new DeliveryDTO()
                .setOrderId(orderEntity.getId())
                .setPayment("payment")
                .setRestaurant(restaurantDto)
                .setCustomer(customerDto);
    }

    public ResponseEntity<Map<String, Object>> getDeliveriesByStatus(String status, int index, int size) {

        PageRequest pageRequest = PageRequest.of(index, size);
        Page<OrderEntity> orderEntitiesPage = orderRepository
                .findOrderEntitiesByStatus(OrderStatus.valueOf(status.toUpperCase()), pageRequest);

        if (orderEntitiesPage.isEmpty())
            throw new EntityException(ExceptionStatus.ORDER_NOT_FOUND);

        List<OrderEntity> orders = orderEntitiesPage.getContent();
        List<DeliveryDTO> deliveryDtos = convertOrderToDeliveryDTO(orders);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", deliveryDtos);
        response.put("page_index", index);
        response.put("page_count", size);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> setDeliveryStatusByOrderId(Long orderId, OrderActionDTO orderActionDto) {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityException(ExceptionStatus.ORDER_NOT_FOUND));

        String orderAction = orderActionDto.getOrderAction().toUpperCase();
        orderEntity.setStatus(OrderStatus.valueOf(orderAction));
        orderRepository.save(orderEntity);

        return ResponseEntity.ok().build();
    }
}
