package ru.liga.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Table(name = "restaurant_menu_item")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RestaurantMenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq_gen")
    @SequenceGenerator(name = "order_item_seq_gen", sequenceName = "order_item_seq", allocationSize = 1)
    private Long id;

    @JoinColumn(name = "restaurant_id")
    private Long restaurantId;

    private String name;

    private Double price;

    private String image;

    private String description;
}
