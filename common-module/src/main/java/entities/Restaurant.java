package entities;

import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.util.Set;

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
    private String kitchenStatus;

    private String name;

    private String coordinates;

    @OneToMany(mappedBy = "restaurant")
    private Set<RestaurantMenuItem> restaurantMenuItemSet;
}
