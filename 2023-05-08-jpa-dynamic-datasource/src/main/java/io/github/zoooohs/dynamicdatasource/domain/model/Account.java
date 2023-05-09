package io.github.zoooohs.dynamicdatasource.domain.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ACCOUNT")
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    @Column(name = "BALANCE")
    private Double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    public Account(Customer customer) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = 0d;
        this.customer = customer;
    }

    public Integer getId() {
        return id;
    }

    public Double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void withDraw(Double amount) {
        if (balance < amount) {
            throw new RuntimeException("가진돈 보다 많이 인출 하려고 함");
        }
        this.balance -= amount;
    }

    public void deposit(Double amount) {
        this.balance += amount;
    }

    public void send(Double amount, Account recieverAccount) {
        withDraw(amount);
        recieverAccount.deposit(amount);
    }
}
