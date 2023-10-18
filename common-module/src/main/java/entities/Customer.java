package entities;

import lombok.*;

import javax.persistence.*;

@Table(name = "customers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq_generator")
    @SequenceGenerator(name = "customers_seq_generator", sequenceName = "customers_seq", allocationSize = 1)
    private Long id;

    private String phone;

    private String email;

    private String address;


}
