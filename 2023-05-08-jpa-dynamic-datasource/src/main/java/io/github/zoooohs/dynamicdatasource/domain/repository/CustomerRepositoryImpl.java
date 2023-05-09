package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CustomerRepository 의 구현체
 *
 * 직접적인 로직은 없으며, 실직적인 DAO(JPA, JDBC 등)을 주입 받아, 해당 DAO 의 메소드를 호출한다.
 * JPA Repository 를 주입하는 내용을 공통화 시키고, 이후 동적 반영을 위해 관련 내용을 abstract class 로 뺐다.
 */
@Repository
public class CustomerRepositoryImpl extends AbstractJPARepository<CustomerJPARepository> implements CustomerRepository {
    public CustomerRepositoryImpl(CustomerJPARepository repository) {
        super(repository);
    }

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public Optional<Customer> findByRnn(String rnn) {
        return repository.findByRnn(rnn);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Customer> findById(Integer customerId) {
        return repository.findById(customerId);
    }
}
