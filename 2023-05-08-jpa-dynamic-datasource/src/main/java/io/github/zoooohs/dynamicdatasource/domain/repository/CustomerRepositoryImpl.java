package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJPARepository customerJPARepository;

    @Override
    public Customer save(Customer customer) {
        return customerJPARepository.save(customer);
    }

    @Override
    public Optional<Customer> findByRnn(String rnn) {
        return customerJPARepository.findByRnn(rnn);
    }
}
