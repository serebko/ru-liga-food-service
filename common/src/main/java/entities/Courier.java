package entities;

import lombok.*;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.util.Set;

@Table(name = "couriers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "couriers_seq_gen")
    @SequenceGenerator(name = "couriers_seq_gen", sequenceName = "couriers_seq", allocationSize = 1)
    private Long id;

    private String phone;

    @Column(name = "status")
    private String deliveryStatus;

    private String coordinates;

    @OneToMany(mappedBy = "courier")
    private Set<Order> orderSet;
}
