package io.github.zoooohs.dynamicdatasource.domain.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity(name = "CUSTOMER")
@Table(name = "CUSTOMER")
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "RNN", unique = true) // Resident Registration Number, 주민번호
    private String rnn;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts;

    public Customer (String name, String rnn) {
        this.name = name;
        this.rnn = rnn;
    }

    public Integer getId() {
        return id;
    }
}
