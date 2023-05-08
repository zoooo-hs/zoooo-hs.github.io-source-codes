package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJPARepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByRnn(String rnn);
}
