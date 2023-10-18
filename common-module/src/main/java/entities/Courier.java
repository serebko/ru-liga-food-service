package entities;

import lombok.*;
import service.OrderStatus;

import javax.persistence.*;

@Table(name = "couriers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "couriers_seq_gen")
    @SequenceGenerator(name = "couriers_seq_gen", sequenceName = "couriers_seq", allocationSize = 1)
    private Long id;

    private String phone;

    private OrderStatus deliveryStatus;

    private String coordinates;
}
