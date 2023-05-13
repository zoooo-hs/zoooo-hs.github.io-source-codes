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

    @Column(name = "BLOCKED")
    private Boolean blocked;

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

    public Boolean isBlocked() {
        if (this.blocked == null) return false;
        return this.blocked;
    }

    public void blockAccount() {
        this.blocked = true;
    }

    public void unblockAccount() {
        this.blocked = false;
    }

    public void withDraw(Double amount) {
        if (isBlocked()) {
            throw new RuntimeException("거래 정지된 계좌입니다. " + this.accountNumber);
        }

        if (balance < amount) {
            throw new RuntimeException("가진돈 보다 많이 인출 하려고 함");
        }
        this.balance -= amount;
    }

    public void deposit(Double amount) {
        if (isBlocked()) {
            throw new RuntimeException("거래 정지된 계좌입니다. " + this.accountNumber);
        }

        this.balance += amount;
    }

    public void send(Double amount, Account recieverAccount) {
        withDraw(amount);
        recieverAccount.deposit(amount);
    }
}
