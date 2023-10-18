package entities;

import lombok.*;
import service.OrderStatus;

import javax.persistence.*;

@Table(name = "restaurants")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurants_seq_gen")
    @SequenceGenerator(name = "restaurants_seq_gen", sequenceName = "restaurants_seq", allocationSize = 1)
    private Long id;

    private String address;

    private OrderStatus kitchenStatus;
}
