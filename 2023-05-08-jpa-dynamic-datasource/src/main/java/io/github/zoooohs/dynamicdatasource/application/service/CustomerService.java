package io.github.zoooohs.dynamicdatasource.application.service;

import io.github.zoooohs.dynamicdatasource.application.dto.CustomerSignUp;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import io.github.zoooohs.dynamicdatasource.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public Customer signUp(CustomerSignUp customerSignUp) {
        Customer newCustomer = customerSignUp.toCustomer();
        return customerRepository.save(newCustomer);
    }

    public Customer findByRnn(String rnn) {
        return customerRepository.findByRnn(rnn)
                .orElseThrow(() -> new RuntimeException("Customer Not Found!"));
    }
}
