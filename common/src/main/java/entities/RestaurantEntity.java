package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import service.OrderStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "restaurants")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RestaurantEntity {

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

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantMenuItemEntity> restaurantMenuItems = new ArrayList<>();

    public void addMenuItem(RestaurantMenuItemEntity menuItem) {
        this.restaurantMenuItems.add(menuItem);
        menuItem.setRestaurant(this);
    }

    public void removeMenuItem(RestaurantMenuItemEntity menuItem) {
        this.restaurantMenuItems.remove(menuItem);
        menuItem.setRestaurant(null);
    }

    public void addOrder(OrderEntity order) {
        this.orders.add(order);
        order.setRestaurant(this);
    }

    public void removeOrder(OrderEntity order) {
        this.orders.remove(order);
        order.setRestaurant(null);
    }
}
