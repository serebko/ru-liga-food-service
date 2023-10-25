package entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import statuses.CourierStatus;

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

@Table(name = "courier")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CourierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courier_seq_gen")
    @SequenceGenerator(name = "courier_seq_gen", sequenceName = "courier_seq", allocationSize = 1)
    private Long id;

    private String phone;

    @Enumerated(EnumType.STRING)
    private CourierStatus status;

    private String coordinates;

    @OneToMany(mappedBy = "courierId", fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    public void addOrder(OrderEntity order) {
        this.orders.add(order);
        order.setCourierId(this.id);
    }

    public void removeOrder(OrderEntity order) {
        this.orders.remove(order);
        order.setCourierId(null);
    }
}
