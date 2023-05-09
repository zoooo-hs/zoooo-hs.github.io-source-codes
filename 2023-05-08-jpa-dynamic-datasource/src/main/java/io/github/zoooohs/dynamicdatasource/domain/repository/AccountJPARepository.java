package io.github.zoooohs.dynamicdatasource.domain.repository;

import io.github.zoooohs.dynamicdatasource.datasource.DynamicalJPADatasource;
import io.github.zoooohs.dynamicdatasource.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@DynamicalJPADatasource
public interface AccountJPARepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(String accountNumber);
}
