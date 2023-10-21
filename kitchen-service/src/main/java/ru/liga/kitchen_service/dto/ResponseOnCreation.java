package ru.liga.kitchen_service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseOnCreation {

    private Long menuItemId;
    private String name;
}
