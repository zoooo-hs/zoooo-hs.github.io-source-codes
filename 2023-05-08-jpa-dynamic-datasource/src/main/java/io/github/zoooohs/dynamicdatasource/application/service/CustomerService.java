package io.github.zoooohs.dynamicdatasource.application.service;

import io.github.zoooohs.dynamicdatasource.application.dto.CustomerSignUp;
import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import io.github.zoooohs.dynamicdatasource.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional(TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME)
    public Customer signUp(CustomerSignUp customerSignUp) {
        Customer newCustomer = customerSignUp.toCustomer();
        return customerRepository.save(newCustomer);
    }

    @Transactional(value = TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME, readOnly = true)
    public Customer findByRnn(String rnn) {
        return customerRepository.findByRnn(rnn)
                .orElseThrow(() -> new RuntimeException("Customer Not Found!"));
    }

    @Transactional(TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME)
    public void deleteAll() {
        customerRepository.deleteAll();
    }

    @Transactional(value = TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME, readOnly = true)
    public Customer findById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer Not Found!"));
    }
}
