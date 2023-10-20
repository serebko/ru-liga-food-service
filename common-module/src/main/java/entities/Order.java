package entities;

import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Table(name = "orders")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq_gen")
    @SequenceGenerator(name = "orders_seq_gen", sequenceName = "orders_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String status;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    private Timestamp timestamp;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

}
