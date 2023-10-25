package ru.liga.order_service.requests;

import lombok.Data;
import ru.liga.order_service.dto.MenuItemDTO;

import java.util.List;

@Data
public class OrderRequest {

    private Long restaurantId;
    private List<MenuItemDTO> menuItems;
}
