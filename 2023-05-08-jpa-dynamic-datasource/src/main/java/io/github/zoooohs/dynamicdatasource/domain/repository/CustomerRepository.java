package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;

import java.util.Optional;

/**
 * domain 외부 영역에서 domain repository 를 참조하기 위해 사용하는 interface
 */
public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findByRnn(String rnn);
    void deleteAll();
}
