package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "customers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq_generator")
    @SequenceGenerator(name = "customers_seq_generator", sequenceName = "customers_seq", allocationSize = 1)
    private Long id;

    private String phone;

    private String email;

    private String address;

    private String coordinates;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders = new ArrayList<>();

    public void addOrder(OrderEntity order) {
        this.orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(OrderEntity order) {
        this.orders.remove(order);
        order.setCustomer(null);
    }
}
