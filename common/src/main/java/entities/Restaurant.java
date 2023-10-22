package entities;

import lombok.*;
import lombok.experimental.Accessors;
import service.OrderStatus;

import javax.persistence.*;
import java.util.List;

@Table(name = "restaurants")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurants_seq_gen")
    @SequenceGenerator(name = "restaurants_seq_gen", sequenceName = "restaurants_seq", allocationSize = 1)
    private Long id;

    private String address;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus kitchenStatus;

    private String name;

    private String coordinates;

    @OneToMany(mappedBy = "restaurant")
    private List<Order> orders;

    @OneToMany(mappedBy = "restaurant")
    private List<RestaurantMenuItem> restaurantMenuItems;
}
