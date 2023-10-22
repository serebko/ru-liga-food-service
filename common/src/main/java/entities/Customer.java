package entities;

import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.util.List;

@Table(name = "customers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq_generator")
    @SequenceGenerator(name = "customers_seq_generator", sequenceName = "customers_seq", allocationSize = 1)
    private Long id;

    private String phone;

    private String email;

    private String address;

    private String coordinates;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}
