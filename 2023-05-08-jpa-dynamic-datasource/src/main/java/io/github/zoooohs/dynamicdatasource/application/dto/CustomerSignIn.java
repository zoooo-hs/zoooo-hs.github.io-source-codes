package io.github.zoooohs.dynamicdatasource.application.dto;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerSignIn {
    private String name;
    private String rnn;

    public Customer toCustomer() {
        return new Customer(null, name, rnn);
    }
}
