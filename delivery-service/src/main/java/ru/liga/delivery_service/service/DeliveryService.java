package ru.liga.delivery_service.service;

import entities.Courier;
import entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import repositories.CourierRepository;
import repositories.OrderRepository;
import ru.liga.delivery_service.dto.CustomerDto;
import ru.liga.delivery_service.dto.DeliveriesResponse;
import ru.liga.delivery_service.dto.DeliveryDto;
import ru.liga.delivery_service.dto.OrderActionDto;
import ru.liga.delivery_service.dto.RestaurantDto;
import service.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Service
@ComponentScan(basePackages = "repositories")
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    @Autowired
    public DeliveryService(OrderRepository orderRepository,
                           CourierRepository courierRepository) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
    }

    private List<DeliveryDto> transformOrderToDeliveryDto(List<Order> orders) {

        List<DeliveryDto> deliveryDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderActionDto orderActionDto = new OrderActionDto().setOrderAction(order.getStatus().toString());
            RestaurantDto restaurantDto = new RestaurantDto().setAddress(order.getRestaurant().getAddress())
                    .setDistance("1200");
            CustomerDto customerDto = new CustomerDto().setAddress(order.getCustomer().getAddress())
                    .setDistance("600");
            DeliveryDto dto = new DeliveryDto()
                    .setOrderId(order.getId())
                    .setPayment("payment")
                    .setOrderAction(orderActionDto)
                    .setRestaurant(restaurantDto)
                    .setCustomer(customerDto);
            deliveryDtos.add(dto);
        }
        return deliveryDtos;
    }
    public ResponseEntity<DeliveriesResponse> getDeliveriesByStatus(String status) {
        List<Order> orders = orderRepository.findOrdersByStatus(OrderStatus.valueOf(status.toUpperCase()));
        if (orders == null || orders.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        DeliveriesResponse response = new DeliveriesResponse();
        response.setDelivery(transformOrderToDeliveryDto(orders));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> setDeliveryStatusById(Long id, OrderActionDto orderActionDto) {

        if (id <= 0) throw new IllegalArgumentException();

        if (orderRepository.existsById(id)) {
            String orderAction = orderActionDto.getOrderAction().toUpperCase();
            Order order = orderRepository.findOrderById(id);
            order.setStatus(OrderStatus.valueOf(orderAction));
            orderRepository.save(order);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<String> setCourierForOrderById(Long id) {

        if (id <= 0) throw new IllegalArgumentException();

        //пока непонятно как назначать курьера, поэтому пусть это будет курьер с id=3
        Courier courier = courierRepository.findCourierById(3L);
        if (orderRepository.existsById(id)) {
            Order order = orderRepository.findOrderById(id).setCourier(courier);
            orderRepository.save(order);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
