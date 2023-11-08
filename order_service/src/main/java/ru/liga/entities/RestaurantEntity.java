package ru.liga.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.liga.statuses.KitchenStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "restaurant")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq_gen")
    @SequenceGenerator(name = "restaurant_seq_gen", sequenceName = "restaurant_seq", allocationSize = 1)
    private Long id;

    private String address;

    @Enumerated(EnumType.STRING)
    private KitchenStatus status;

    private String name;

    @OneToMany(mappedBy = "restaurantId", fetch = FetchType.LAZY)
    private List<RestaurantMenuItemEntity> restaurantMenuItems = new ArrayList<>();
}
