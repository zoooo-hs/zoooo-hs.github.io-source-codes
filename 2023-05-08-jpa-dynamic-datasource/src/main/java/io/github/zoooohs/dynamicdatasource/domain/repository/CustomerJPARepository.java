package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA 를 이용한 Customer table DB Access Object
 * 동적 Datasource 반영을 위해 @DynamicalJPADatasource 를 추가했다.
 */
public interface CustomerJPARepository extends JpaRepository<Customer, Integer>, CustomerRepository {
}
