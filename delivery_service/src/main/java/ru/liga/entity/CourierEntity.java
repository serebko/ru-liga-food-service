package ru.liga.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.liga.status.CourierStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    private String uid;

    @Transient
    private List<String> orders = new ArrayList<>();
}
