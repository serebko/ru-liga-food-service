package entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import service.OrderStatus;

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
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private OrderStatus deliveryStatus;

    private String coordinates;

    @OneToMany(mappedBy = "courier")
    private List<Order> orders;
}