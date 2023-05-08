package io.github.zoooohs.dynamicdatasource.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(name = "CUSTOMER")
@Table(name = "CUSTOMER")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "RNN", unique = true) // Resident Registration Number, 주민번호
    private String rnn;

    public Integer getId() {
        return id;
    }
}
