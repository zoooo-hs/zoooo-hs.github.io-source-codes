package io.github.zoooohs.dynamicdatasource.application.dto;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerSignUp {
    private String name;
    private String rnn;

    public Customer toCustomer() {
        return new Customer(name, rnn);
    }
}
