package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import statuses.KitchenStatus;

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
@ToString
@Accessors(chain = true)
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
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "restaurantId", fetch = FetchType.LAZY)
    private List<RestaurantMenuItemEntity> restaurantMenuItems = new ArrayList<>();

    public void addMenuItem(RestaurantMenuItemEntity menuItem) {
        this.restaurantMenuItems.add(menuItem);
        menuItem.setRestaurantId(this.id);
    }

    public void removeMenuItem(RestaurantMenuItemEntity menuItem) {
        this.restaurantMenuItems.remove(menuItem);
        menuItem.setRestaurantId(null);
    }

    public void addOrder(OrderEntity order) {
        this.orders.add(order);
        order.setRestaurantId(this.id);
    }

    public void removeOrder(OrderEntity order) {
        this.orders.remove(order);
        order.setRestaurantId(null);
    }
}
